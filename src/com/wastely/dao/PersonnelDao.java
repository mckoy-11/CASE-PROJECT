package com.wastely.dao;

import com.wastely.database.SQLConnection;
import com.wastely.model.Personnel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PersonnelDao {

    // ========================= READ ALL =========================

    public List<Personnel> getAllPersonnel() {
        List<Personnel> list = new ArrayList<>();

        String sql =
            "SELECT p.*, " +
            "gl.gender_label AS gender, " +
            "rl.role_label AS role, " +
            "sl.status_label AS status " +
            "FROM personnel p " +
            "LEFT JOIN gender_lookup gl ON p.gender_id = gl.gender_id " +
            "LEFT JOIN role_lookup rl ON p.role_id = rl.role_id " +
            "LEFT JOIN status_lookup sl ON p.status_id = sl.status_id " +
            "ORDER BY p.personnel_name ASC";

        try (Connection conn = SQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(map(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // ========================= UNASSIGNED =========================

    public List<Personnel> getAllUnassignedPersonnel() {
        List<Personnel> list = new ArrayList<>();

        String sql =
            "SELECT p.*, " +
            "gl.gender_label AS gender, " +
            "rl.role_label AS role, " +
            "sl.status_label AS status " +
            "FROM personnel p " +
            "LEFT JOIN gender_lookup gl ON p.gender_id = gl.gender_id " +
            "LEFT JOIN role_lookup rl ON p.role_id = rl.role_id " +
            "LEFT JOIN status_lookup sl ON p.status_id = sl.status_id " +
            "WHERE p.team_id IS NULL " +
            "OR rl.role_key = 'NONE' " +
            "ORDER BY p.personnel_name ASC";

        try (Connection conn = SQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(map(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // ========================= BY ID =========================

    public Personnel getPersonnelById(int id) {
        String sql =
            "SELECT p.*, " +
            "gl.gender_label AS gender, " +
            "rl.role_label AS role, " +
            "sl.status_label AS status " +
            "FROM personnel p " +
            "LEFT JOIN gender_lookup gl ON p.gender_id = gl.gender_id " +
            "LEFT JOIN role_lookup rl ON p.role_id = rl.role_id " +
            "LEFT JOIN status_lookup sl ON p.status_id = sl.status_id " +
            "WHERE p.personnel_id = ?";

        try (Connection conn = SQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return map(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // ========================= BY ROLE =========================

    public List<Personnel> getPersonnelByRole(String roleKey) {
        List<Personnel> list = new ArrayList<>();

        String sql =
            "SELECT p.*, " +
            "gl.gender_label AS gender, " +
            "rl.role_label AS role, " +
            "sl.status_label AS status " +
            "FROM personnel p " +
            "LEFT JOIN gender_lookup gl ON p.gender_id = gl.gender_id " +
            "LEFT JOIN role_lookup rl ON p.role_id = rl.role_id " +
            "LEFT JOIN status_lookup sl ON p.status_id = sl.status_id " +
            "WHERE rl.role_key = ? " +
            "ORDER BY p.personnel_name ASC";

        try (Connection conn = SQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, roleKey.toUpperCase());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // ========================= CREATE =========================

    public boolean addPersonnel(Personnel p) {
        String sql =
            "INSERT INTO personnel " +
            "(personnel_name, age, gender_id, address, contact_number, team_id, role_id, status_id) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = SQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, p.getFullName());
            stmt.setInt(2, p.getAge());
            stmt.setObject(3, getGenderId(p.getGender()));
            stmt.setString(4, p.getAddress());
            stmt.setString(5, p.getPhoneNumber());
            stmt.setObject(6, null);
            stmt.setObject(7, getRoleId(p.getRole()));
            stmt.setObject(8, getStatusId(p.getStatus()));

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // ========================= UPDATE =========================

    public boolean updatePersonnel(Personnel p) {
        String sql =
            "UPDATE personnel SET " +
            "personnel_name = ?, age = ?, gender_id = ?, address = ?, contact_number = ?, " +
            "team_id = ?, role_id = ?, status_id = ? " +
            "WHERE personnel_id = ?";

        try (Connection conn = SQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, p.getFullName());
            stmt.setInt(2, p.getAge());
            stmt.setObject(3, getGenderId(p.getGender()));
            stmt.setString(4, p.getAddress());
            stmt.setString(5, p.getPhoneNumber());
            stmt.setObject(6, null);
            stmt.setObject(7, getRoleId(p.getRole()));
            stmt.setObject(8, getStatusId(p.getStatus()));
            stmt.setInt(9, p.getId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // ========================= ROLE UPDATE (FIXED CORE METHOD) =========================

    public boolean updatePersonnelRole(int personnelId, String roleKey) {
        String sql =
            "UPDATE personnel SET role_id = " +
            "(SELECT role_id FROM role_lookup WHERE role_key = ?) " +
            "WHERE personnel_id = ?";

        try (Connection conn = SQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, roleKey.toUpperCase());
            stmt.setInt(2, personnelId);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean updateStatus(int id, String statusKey) {
        String sql = "UPDATE personnel SET status_id = " +
                    "(SELECT status_id FROM status_lookup WHERE status_key = ?) " +
                    "WHERE personnel_id = ?";

        try (Connection conn = SQLConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, statusKey.toUpperCase());
            stmt.setInt(2, id);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // ========================= DELETE =========================

    public boolean deletePersonnel(int id) {
        String sql = "DELETE FROM personnel WHERE personnel_id = ?";

        try (Connection conn = SQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // ========================= COUNT =========================

    public int getTotalPersonnelCount() {
        return count("SELECT COUNT(*) FROM personnel");
    }

    public int getActivePersonnelCount() {
        return count(
            "SELECT COUNT(*) FROM personnel p " +
            "JOIN status_lookup sl ON p.status_id = sl.status_id " +
            "WHERE sl.status_key = 'ACTIVE'"
        );
    }

    public int getUnassignedPersonnelCount() {
        return count(
            "SELECT COUNT(*) FROM personnel p " +
            "JOIN status_lookup sl ON p.status_id = sl.status_id " +
            "WHERE sl.status_key = 'UNASSIGNED'"
        );
    }

    private int count(String sql) {
        try (Connection conn = SQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) return rs.getInt(1);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    // ========================= MAPPER =========================

    private Personnel map(ResultSet rs) throws SQLException {
        Personnel p = new Personnel();

        p.setId(rs.getInt("personnel_id"));
        p.setFullName(rs.getString("personnel_name"));
        p.setAge(rs.getInt("age"));
        p.setGender(rs.getString("gender"));
        p.setAddress(rs.getString("address"));
        p.setPhoneNumber(rs.getString("contact_number"));
        p.setRole(rs.getString("role"));
        p.setStatus(rs.getString("status"));

        return p;
    }

    // ========================= LOOKUPS =========================

    private Integer getRoleId(String roleKey) throws SQLException {
        if (roleKey == null || roleKey.isEmpty()) roleKey = "NONE";

        String sql = "SELECT role_id FROM role_lookup WHERE role_key = ?";

        try (Connection conn = SQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, roleKey.toUpperCase());

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getInt(1) : null;
            }
        }
    }

    private Integer getStatusId(String statusKey) throws SQLException {
        if (statusKey == null || statusKey.isEmpty()) statusKey = "UNASSIGNED";

        String sql = "SELECT status_id FROM status_lookup WHERE status_key = ?";

        try (Connection conn = SQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, statusKey.toUpperCase());

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getInt(1) : null;
            }
        }
    }

    private Integer getGenderId(String genderKey) throws SQLException {
        if (genderKey == null || genderKey.isEmpty()) return null;

        String sql = "SELECT gender_id FROM gender_lookup WHERE gender_key = ?";

        try (Connection conn = SQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, genderKey.toUpperCase());

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getInt(1) : null;
            }
        }
    }
}