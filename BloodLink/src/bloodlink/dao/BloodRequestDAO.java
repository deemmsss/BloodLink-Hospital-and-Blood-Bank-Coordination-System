/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bloodlink.dao;

import bloodlink.db.DBConnection;
import bloodlink.model.BloodRequest;
import bloodlink.model.enums.BloodType;
import bloodlink.model.enums.RequestPriority;
import bloodlink.model.enums.RequestStatus;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author demi
 * 
 * Data Access Object for the blood_requests table.
 * Used by: Nurse (create, view own), Technician (view queue, approve/reject),
 *          Hospital Admin (view all, escalate), Blood Bank Admin (overview).
 */
public class BloodRequestDAO {

    /**
     * Creates a new blood request.
     */
    public boolean createRequest(BloodRequest req) {
        String sql = "INSERT INTO blood_requests (patient_id, nurse_id, blood_type, units_requested, status, priority, notes) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, req.getPatientId());
            stmt.setInt(2, req.getNurseId());
            stmt.setString(3, req.getBloodType().name());
            stmt.setInt(4, req.getUnitsRequested());
            stmt.setString(5, req.getStatus().name());
            stmt.setString(6, req.getPriority().name());
            stmt.setString(7, req.getNotes());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Updates the status of a blood request.
     * Used when a technician approves/rejects, or when a nurse confirms receipt.
     */
    public boolean updateStatus(int requestId, RequestStatus newStatus) {
        String sql = "UPDATE blood_requests SET status = ? WHERE id = ?";
        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, newStatus.name());
            stmt.setInt(2, requestId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Updates the priority of a blood request.
     * Used by Hospital Admin for escalation (Work Request 3).
     */
    public boolean updatePriority(int requestId, RequestPriority newPriority) {
        String sql = "UPDATE blood_requests SET priority = ? WHERE id = ?";
        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, newPriority.name());
            stmt.setInt(2, requestId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Retrieves all requests created by a specific nurse.
     * Includes patient and nurse names via JOINs for display.
     */
    public List<BloodRequest> getRequestsByNurse(int nurseId) {
        List<BloodRequest> requests = new ArrayList<>();
        String sql = "SELECT br.*, p.full_name AS patient_name, u.full_name AS nurse_name "
                   + "FROM blood_requests br "
                   + "JOIN patients p ON br.patient_id = p.id "
                   + "JOIN users u ON br.nurse_id = u.id "
                   + "WHERE br.nurse_id = ? "
                   + "ORDER BY br.request_date DESC";
        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, nurseId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                requests.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requests;
    }

    /**
     * Retrieves all blood requests (for admin overview and technician queue).
     */
    public List<BloodRequest> getAllRequests() {
        List<BloodRequest> requests = new ArrayList<>();
        String sql = "SELECT br.*, p.full_name AS patient_name, u.full_name AS nurse_name "
                   + "FROM blood_requests br "
                   + "JOIN patients p ON br.patient_id = p.id "
                   + "JOIN users u ON br.nurse_id = u.id "
                   + "ORDER BY FIELD(br.priority, 'EMERGENCY', 'URGENT', 'ROUTINE'), br.request_date DESC";
        try (Statement stmt = DBConnection.getConnection().createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                requests.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requests;
    }

    /**
     * Retrieves requests filtered by status.
     * Used by technician to see only PENDING or APPROVED requests.
     */
    public List<BloodRequest> getRequestsByStatus(RequestStatus status) {
        List<BloodRequest> requests = new ArrayList<>();
        String sql = "SELECT br.*, p.full_name AS patient_name, u.full_name AS nurse_name "
                   + "FROM blood_requests br "
                   + "JOIN patients p ON br.patient_id = p.id "
                   + "JOIN users u ON br.nurse_id = u.id "
                   + "WHERE br.status = ? "
                   + "ORDER BY FIELD(br.priority, 'EMERGENCY', 'URGENT', 'ROUTINE'), br.request_date DESC";
        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, status.name());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                requests.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requests;
    }

    /**
     * Retrieves requests that have been shipped but not yet received by the hospital.
     * Used by the nurse's "Confirm Receipt" screen.
     */
    public List<BloodRequest> getShippedRequestsForNurse(int nurseId) {
        List<BloodRequest> requests = new ArrayList<>();
        String sql = "SELECT br.*, p.full_name AS patient_name, u.full_name AS nurse_name "
                   + "FROM blood_requests br "
                   + "JOIN patients p ON br.patient_id = p.id "
                   + "JOIN users u ON br.nurse_id = u.id "
                   + "WHERE br.nurse_id = ? AND br.status = 'SHIPPED' "
                   + "ORDER BY br.request_date DESC";
        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, nurseId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                requests.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requests;
    }

    /**
     * Gets a single request by ID.
     */
    public BloodRequest getRequestById(int id) {
        String sql = "SELECT br.*, p.full_name AS patient_name, u.full_name AS nurse_name "
                   + "FROM blood_requests br "
                   + "JOIN patients p ON br.patient_id = p.id "
                   + "JOIN users u ON br.nurse_id = u.id "
                   + "WHERE br.id = ?";
        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Maps a ResultSet row (with JOINed names) to a BloodRequest object.
     */
    private BloodRequest mapRow(ResultSet rs) throws SQLException {
        BloodRequest req = new BloodRequest(
            rs.getInt("id"),
            rs.getInt("patient_id"),
            rs.getInt("nurse_id"),
            BloodType.valueOf(rs.getString("blood_type")),
            rs.getInt("units_requested"),
            RequestStatus.valueOf(rs.getString("status")),
            RequestPriority.valueOf(rs.getString("priority")),
            rs.getTimestamp("request_date"),
            rs.getString("notes")
        );
        req.setPatientName(rs.getString("patient_name"));
        req.setNurseName(rs.getString("nurse_name"));
        return req;
    }
}