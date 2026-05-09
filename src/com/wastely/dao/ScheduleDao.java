package com.wastely.dao;

import com.wastely.database.SQLConnection;
import com.wastely.model.CollectionInfo;
import com.wastely.model.Schedule;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class ScheduleDao {

    /**
     * Retrieves all schedules with related barangay, team, truck, and status information.
     *
     * @return list of schedules
     */
    public List<Schedule> findAll() {
        List<Schedule> schedules = new ArrayList<>();

        String sql =
                "SELECT " +
                "s.schedule_id, " +
                "s.barangay_id, " +
                "s.team_id, " +
                "s.schedule_date, " +
                "s.schedule_time, " +
                "sl.status_label AS status, " +
                "b.barangay_name, " +
                "b.contact, " +
                "ba.admin_name AS barangay_admin, " +
                "t.team_name, " +
                "tr.plate_number AS truck_plate_number, " +
                "ttl.truck_type_label AS truck_type " +
                "FROM schedule s " +
                "LEFT JOIN status_lookup sl ON s.status_id = sl.status_id " +
                "LEFT JOIN barangay b ON s.barangay_id = b.barangay_id " +
                "LEFT JOIN barangay_admin ba ON b.barangay_id = ba.barangay_id " +
                "LEFT JOIN team t ON s.team_id = t.team_id " +
                "LEFT JOIN truck tr ON t.truck_id = tr.truck_id " +
                "LEFT JOIN truck_type_lookup ttl ON tr.truck_type_id = ttl.truck_type_id " +
                "ORDER BY s.schedule_id AND s.schedule_date ASC";

        try (
                Connection connection = SQLConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()
        ) {

            while (resultSet.next()) {
                schedules.add(mapSchedule(resultSet));
            }

        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        return schedules;
    }

    /**
     * Saves a new schedule or updates an existing one.
     *
     * @param schedule schedule object
     * @return true if operation succeeds
     */
    public boolean saveOrUpdate(Schedule schedule) {

        if (schedule == null || isBlank(schedule.getBarangayName())) {
            return false;
        }

        try (Connection connection = SQLConnection.getConnection()) {

            Integer barangayId =
                    findBarangayId(connection, schedule.getBarangayName());

            if (barangayId == null) {
                return false;
            }

            Integer teamId =
                    findTeamId(connection, schedule.getCollectorTeam());

            Integer existingScheduleId = null;

            if (schedule.getId() > 0) {
                existingScheduleId = schedule.getId();
            } else {
                existingScheduleId =
                        findScheduleIdByBarangay(connection, barangayId);
            }

            if (existingScheduleId != null) {

                return updateSchedule(
                        connection,
                        existingScheduleId,
                        schedule,
                        teamId
                );
            }

            return insertSchedule(
                    connection,
                    barangayId,
                    schedule,
                    teamId
            );

        } catch (SQLException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes a schedule by ID.
     *
     * @param scheduleId schedule ID
     * @return true if deleted successfully
     */
    public boolean deleteById(int scheduleId) {

        String sql =
                "DELETE FROM schedule WHERE schedule_id = ?";

        try (
                Connection connection = SQLConnection.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, scheduleId);

            return statement.executeUpdate() > 0;

        } catch (SQLException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    /**
     * Retrieves current collection information for a barangay.
     *
     * @param barangayName barangay name
     * @return collection information
     */
    public CollectionInfo findCurrentCollectionInfo(String barangayName) {

        String sql =
                "SELECT " +
                "t.team_name, " +
                "tr.plate_number AS truck_plate_number, " +
                "ttl.truck_type_label AS truck_type, " +
                "s.schedule_time, " +
                "sl.status_label AS status " +
                "FROM schedule s " +
                "LEFT JOIN status_lookup sl ON s.status_id = sl.status_id " +
                "LEFT JOIN barangay b ON s.barangay_id = b.barangay_id " +
                "LEFT JOIN team t ON s.team_id = t.team_id " +
                "LEFT JOIN truck tr ON t.truck_id = tr.truck_id " +
                "LEFT JOIN truck_type_lookup ttl ON tr.truck_type_id = ttl.truck_type_id " +
                "WHERE UPPER(TRIM(b.barangay_name)) = UPPER(TRIM(?)) " +
                "ORDER BY s.schedule_date ASC, s.schedule_time ASC " +
                "LIMIT 1";

        try (
                Connection connection = SQLConnection.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(1, barangayName);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {

                    CollectionInfo info = new CollectionInfo();

                    info.setAssignedTeam(
                            resultSet.getString("team_name")
                    );

                    info.setTruckPlateNumber(
                            resultSet.getString("truck_plate_number")
                    );

                    info.setTruckType(
                            resultSet.getString("truck_type")
                    );

                    Time etaTime =
                            resultSet.getTime("schedule_time");

                    info.setEta(
                            etaTime == null
                                    ? null
                                    : etaTime.toString()
                    );

                    info.setStatus(
                            resultSet.getString("status")
                    );

                    return info;
                }
            }

        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        return null;
    }

    /**
     * Updates an existing schedule.
     *
     * @param connection database connection
     * @param scheduleId schedule ID
     * @param schedule schedule object
     * @param teamId team ID
     * @return true if updated successfully
     * @throws SQLException SQL exception
     */
    private boolean updateSchedule(
            Connection connection,
            int scheduleId,
            Schedule schedule,
            Integer teamId
    ) throws SQLException {

        String sql =
                "UPDATE schedule SET " +
                "team_id = ?, " +
                "schedule_date = ?, " +
                "schedule_time = ?, " +
                "status_id = ? " +
                "WHERE schedule_id = ?";

        try (
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            bindSchedule(statement, schedule, teamId, false);

            statement.setInt(5, scheduleId);

            return statement.executeUpdate() > 0;
        }
    }

    /**
     * Inserts a new schedule.
     *
     * @param connection database connection
     * @param barangayId barangay ID
     * @param schedule schedule object
     * @param teamId team ID
     * @return true if inserted successfully
     * @throws SQLException SQL exception
     */
    private boolean insertSchedule(
            Connection connection,
            int barangayId,
            Schedule schedule,
            Integer teamId
    ) throws SQLException {

        String sql =
                "INSERT INTO schedule " +
                "(" +
                "barangay_id, " +
                "team_id, " +
                "schedule_date, " +
                "schedule_time, " +
                "status_id" +
                ") " +
                "VALUES (?, ?, ?, ?, ?)";

        try (
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, barangayId);

            bindSchedule(statement, schedule, teamId, true);

            return statement.executeUpdate() > 0;
        }
    }

    /**
     * Binds schedule values into a prepared statement.
     *
     * @param statement prepared statement
     * @param schedule schedule object
     * @param teamId team ID
     * @param skipBarangay whether barangay index is skipped
     * @throws SQLException SQL exception
     */
    private void bindSchedule(
            PreparedStatement statement,
            Schedule schedule,
            Integer teamId,
            boolean skipBarangay
    ) throws SQLException {

        int parameterIndex = skipBarangay ? 2 : 1;

        setNullableTeamId(statement, parameterIndex++, teamId);

        statement.setDate(
                parameterIndex++,
                schedule.getDate() == null
                        ? null
                        : Date.valueOf(schedule.getDate())
        );

        statement.setTime(
                parameterIndex++,
                schedule.getTime() == null
                        ? null
                        : Time.valueOf(schedule.getTime())
        );

        statement.setInt(
                parameterIndex,
                getStatusId(
                        isBlank(schedule.getStatus())
                                ? "SCHEDULED"
                                : schedule.getStatus()
                )
        );
    }

    /**
     * Maps a result set row into a Schedule object.
     *
     * @param resultSet result set
     * @return mapped schedule
     * @throws SQLException SQL exception
     */
    private Schedule mapSchedule(ResultSet resultSet)
            throws SQLException {

        Schedule schedule = new Schedule();

        schedule.setId(
                resultSet.getInt("schedule_id")
        );

        schedule.setBarangayName(
                resultSet.getString("barangay_name")
        );

        schedule.setBarangayAdmin(
                resultSet.getString("barangay_admin")
        );

        schedule.setContactNumber(
                resultSet.getString("contact")
        );

        schedule.setCollectorTeam(
                resultSet.getString("team_name")
        );

        schedule.setTruckPlateNumber(
                resultSet.getString("truck_plate_number")
        );

        schedule.setTruckType(
                resultSet.getString("truck_type")
        );

        Date date =
                resultSet.getDate("schedule_date");

        if (date != null) {
            schedule.setDate(date.toLocalDate());
        }

        Time time =
                resultSet.getTime("schedule_time");

        if (time != null) {
            schedule.setTime(time.toLocalTime());
            schedule.setEta(time.toString());
        }

        schedule.setStatus(
                resultSet.getString("status")
        );

        return schedule;
    }

    /**
     * Finds barangay ID by barangay name.
     *
     * @param connection database connection
     * @param barangayName barangay name
     * @return barangay ID
     * @throws SQLException SQL exception
     */
    private Integer findBarangayId(
            Connection connection,
            String barangayName
    ) throws SQLException {

        String sql =
                "SELECT barangay_id " +
                "FROM barangay " +
                "WHERE UPPER(TRIM(barangay_name)) = UPPER(TRIM(?))";

        try (
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(1, barangayName);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return resultSet.getInt("barangay_id");
                }
            }
        }

        return null;
    }

    /**
     * Finds team ID by team name.
     *
     * @param connection database connection
     * @param teamName team name
     * @return team ID
     * @throws SQLException SQL exception
     */
    private Integer findTeamId(
            Connection connection,
            String teamName
    ) throws SQLException {

        if (isBlank(teamName)) {
            return null;
        }

        String sql =
                "SELECT team_id " +
                "FROM team " +
                "WHERE UPPER(TRIM(team_name)) = UPPER(TRIM(?))";

        try (
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(1, teamName);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return resultSet.getInt("team_id");
                }
            }
        }

        return null;
    }

    /**
     * Finds existing schedule ID by barangay ID.
     *
     * @param connection database connection
     * @param barangayId barangay ID
     * @return schedule ID
     * @throws SQLException SQL exception
     */
    private Integer findScheduleIdByBarangay(
            Connection connection,
            int barangayId
    ) throws SQLException {

        String sql =
                "SELECT schedule_id " +
                "FROM schedule " +
                "WHERE barangay_id = ? " +
                "LIMIT 1";

        try (
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, barangayId);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return resultSet.getInt("schedule_id");
                }
            }
        }

        return null;
    }

    /**
     * Sets nullable team ID parameter.
     *
     * @param statement prepared statement
     * @param index parameter index
     * @param teamId team ID
     * @throws SQLException SQL exception
     */
    private void setNullableTeamId(
            PreparedStatement statement,
            int index,
            Integer teamId
    ) throws SQLException {

        if (teamId != null && teamId > 0) {
            statement.setInt(index, teamId);
        } else {
            statement.setNull(index, Types.INTEGER);
        }
    }

    /**
     * Determines whether a string is blank.
     *
     * @param value string value
     * @return true if blank
     */
    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    /**
     * Retrieves status ID from status lookup.
     *
     * @param statusName status key
     * @return status ID
     * @throws SQLException SQL exception
     */
    private Integer getStatusId(String statusName)
            throws SQLException {

        if (isBlank(statusName)) {
            return 24;
        }

        String sql =
                "SELECT status_id " +
                "FROM status_lookup " +
                "WHERE status_key = ? " +
                "AND status_domain_id = 8";

        try (
                Connection connection = SQLConnection.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    statusName.toUpperCase()
            );

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return resultSet.getInt("status_id");
                }
            }
        }

        return 24;
    } 
}