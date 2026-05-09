package com.wastely.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.wastely.database.SQLConnection;
import com.wastely.model.Barangay;

public class BarangayDao {

    public boolean save(Barangay barangay) throws SQLException {
        String sql = "INSERT INTO barangay(barangay_name, barangay_household, purok_count, population, contact, collection_day, status_id)"
                + " VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = SQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bindBarangay(ps, barangay, false);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        barangay.setBarangayId(keys.getInt(1));
                    }
                }
                return true;
            }
            return false;
        }
    }

    public boolean update(Barangay barangay) throws SQLException {
        String sql = "UPDATE barangay SET barangay_name = ?, barangay_household = ?, purok_count = ?, population = ?, contact = ?,"
                + " collection_day = ?, status_id = ? WHERE barangay_id = ?";

        try (Connection conn = SQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            bindBarangay(ps, barangay, true);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        try (Connection conn = SQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM barangay WHERE barangay_id = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public Barangay findById(int id) throws SQLException {
        String sql = "SELECT b.*, sl.status_label as status FROM barangay b " +
                     "LEFT JOIN status_lookup sl ON b.status_id = sl.status_id " +
                     "WHERE b.barangay_id = ?";
        try (Connection conn = SQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public List<Barangay> findAll() throws SQLException {
        List<Barangay> results = new ArrayList<Barangay>();
        String sql = "SELECT b.*, sl.status_label as status FROM barangay b " +
                     "LEFT JOIN status_lookup sl ON b.status_id = sl.status_id " +
                     "ORDER BY b.barangay_id";
        try (Connection conn = SQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                results.add(map(rs));
            }
        }
        return results;
    }

    public Barangay findByName(String name) throws SQLException {
        String sql = "SELECT b.*, sl.status_label as status FROM barangay b " +
                     "LEFT JOIN status_lookup sl ON b.status_id = sl.status_id " +
                     "WHERE UPPER(TRIM(b.barangay_name)) = UPPER(TRIM(?))";
        try (Connection conn = SQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public List<Barangay> findByCollectionDay(String day) throws SQLException {
        List<Barangay> results = new ArrayList<Barangay>();
        String sql = "SELECT b.*, sl.status_label as status FROM barangay b " +
                     "LEFT JOIN status_lookup sl ON b.status_id = sl.status_id " +
                     "WHERE b.collection_day = ? ORDER BY b.barangay_name";
        try (Connection conn = SQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, day);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    results.add(map(rs));
                }
            }
        }
        return results;
    }

    public int getTotalSchedBarangay() throws SQLException {
        String sql = "SELECT COUNT(*) FROM barangay b " +
                     "JOIN status_lookup sl ON b.status_id = sl.status_id " +
                     "WHERE sl.status_id = 23";
        return count(sql);
    }

    public int getTotalCount() throws SQLException {
        return count("SELECT COUNT(*) FROM barangay");
    }

    public int getTotalHousehold() throws SQLException {
        return count("SELECT COALESCE(SUM(barangay_household), 0) FROM barangay");
    }
    
    public int getTotalPopulation() throws SQLException {
        return count("SELECT COALESCE(SUM(population), 0) FROM barangay");
    }

    private int count(String sql) throws SQLException {
        try (Connection conn = SQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private void bindBarangay(PreparedStatement ps, Barangay barangay, boolean includeId) throws SQLException {
        ps.setString(1, barangay.getBarangayName());
        ps.setInt(2, barangay.getBarangayHousehold());
        ps.setInt(3, barangay.getPurokCount());
        ps.setInt(4, barangay.getPopulation());
        ps.setString(5, barangay.getContact());
        ps.setString(6, barangay.getCollectionDay());
        ps.setInt(7, getStatusId(barangay.getStatus() != null ? barangay.getStatus() : "ACTIVE"));
        if (includeId) {
            ps.setInt(8, barangay.getBarangayId());
        }
    }

    private Barangay map(ResultSet rs) throws SQLException {
        Barangay barangay = new Barangay();
        barangay.setBarangayId(rs.getInt("barangay_id"));
        barangay.setBarangayName(rs.getString("barangay_name"));
        barangay.setBarangayHousehold(rs.getInt("barangay_household"));
        
        try {
            barangay.setPurokCount(rs.getInt("purok_count"));
        } catch (SQLException e) {
            barangay.setPurokCount(0);
        }
        
        try {
            barangay.setPopulation(rs.getInt("population"));
        } catch (SQLException e) {
            barangay.setPopulation(0);
        }
        
        barangay.setContact(rs.getString("contact"));
        barangay.setCollectionDay(rs.getString("collection_day"));
        barangay.setStatus(rs.getString("status"));
        return barangay;
    }

    private Integer getStatusId(String statusName) throws SQLException {
        if (statusName == null || statusName.trim().isEmpty()) return 4; // Default to ACTIVE for barangay
        String sql = "SELECT status_id FROM status_lookup WHERE status_key = ? AND status_domain_id = 2"; // Barangay domain
        try (Connection conn = SQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, statusName.toUpperCase());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("status_id") : 4; // Default to ACTIVE
            }
        }
    }

    public String findAdminByBarangayName(Barangay barangay) {

        String sql =
            "SELECT ba.admin_name " +
            "FROM barangay_admin ba " +
            "JOIN barangay b ON ba.barangay_id = b.barangay_id " +
            "WHERE UPPER(TRIM(b.barangay_name)) = UPPER(TRIM(?))";

        try (Connection conn = SQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, barangay.getBarangayName());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("admin_name");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("DAO error: failed to fetch admin", e);
        }

        return null;
    }

    public String findAdminByBarangayId(int barangayId) {

        String sql =
            "SELECT admin_name FROM barangay_admin WHERE barangay_id = ?";

        try (Connection conn = SQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, barangayId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("admin_name");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("DAO error: failed to fetch admin", e);
        }

        return null;
    }

    public String getScheduleCreatedDateByBarangayId(int barangayId) {
        String sql = "SELECT MIN(DATE(s.created_at)) as schedule_date " +
                     "FROM schedule s " +
                     "WHERE s.barangay_id = ?";

        try (Connection conn = SQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, barangayId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String dateStr = rs.getString("schedule_date");
                    return dateStr != null ? dateStr : "";
                }
            }

        } catch (SQLException e) {
            System.err.println("Failed to fetch schedule created date: " + e.getMessage());
        }

        return "";
    }

}
