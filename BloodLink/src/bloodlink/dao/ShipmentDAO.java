/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bloodlink.dao;

import bloodlink.db.DBConnection;
import bloodlink.model.Shipment;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author demi
 * 
 * Data Access Object for the shipments and shipment_items tables.
 * Used by: Technician (create shipments), Nurse (confirm receipt).
 */
public class ShipmentDAO {

    /**
     * Creates a new shipment record for an approved request.
     * @return the auto-generated shipment ID, or -1 on failure
     */
    public int createShipment(int requestId, int technicianId) {
        String sql = "INSERT INTO shipments (request_id, technician_id) VALUES (?, ?)";
        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, requestId);
            stmt.setInt(2, technicianId);
            if (stmt.executeUpdate() > 0) {
                ResultSet keys = stmt.getGeneratedKeys();
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Adds a blood unit to a shipment (inserts into shipment_items).
     */
    public boolean addShipmentItem(int shipmentId, int bloodUnitId) {
        String sql = "INSERT INTO shipment_items (shipment_id, blood_unit_id) VALUES (?, ?)";
        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, shipmentId);
            stmt.setInt(2, bloodUnitId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Marks a shipment as confirmed (received by the hospital).
     */
    public boolean confirmReceipt(int shipmentId) {
        String sql = "UPDATE shipments SET confirmed = TRUE, received_date = NOW() WHERE id = ?";
        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, shipmentId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Retrieves the shipment associated with a blood request.
     */
    public Shipment getShipmentByRequestId(int requestId) {
        String sql = "SELECT s.*, u.full_name AS technician_name "
                   + "FROM shipments s "
                   + "JOIN users u ON s.technician_id = u.id "
                   + "WHERE s.request_id = ?";
        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, requestId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Shipment sh = new Shipment(
                    rs.getInt("id"),
                    rs.getInt("request_id"),
                    rs.getInt("technician_id"),
                    rs.getTimestamp("ship_date"),
                    rs.getTimestamp("received_date"),
                    rs.getBoolean("confirmed")
                );
                sh.setTechnicianName(rs.getString("technician_name"));
                return sh;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Retrieves all shipments (for admin reporting).
     */
    public List<Shipment> getAllShipments() {
        List<Shipment> shipments = new ArrayList<>();
        String sql = "SELECT s.*, u.full_name AS technician_name "
                   + "FROM shipments s "
                   + "JOIN users u ON s.technician_id = u.id "
                   + "ORDER BY s.ship_date DESC";
        try (Statement stmt = DBConnection.getConnection().createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Shipment sh = new Shipment(
                    rs.getInt("id"),
                    rs.getInt("request_id"),
                    rs.getInt("technician_id"),
                    rs.getTimestamp("ship_date"),
                    rs.getTimestamp("received_date"),
                    rs.getBoolean("confirmed")
                );
                sh.setTechnicianName(rs.getString("technician_name"));
                shipments.add(sh);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return shipments;
    }
}
