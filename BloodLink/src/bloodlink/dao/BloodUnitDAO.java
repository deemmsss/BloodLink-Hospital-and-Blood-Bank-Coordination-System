/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bloodlink.dao;

import bloodlink.db.DBConnection;
import bloodlink.model.BloodUnit;
import bloodlink.model.enums.BloodType;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 *
 * @author demi
 * 
 * Data Access Object for the blood_units table.
 * Used by: Technician (view inventory, assign units to shipments),
 *          Blood Bank Admin (add/remove units, view counts).
 */
public class BloodUnitDAO {

    /**
     * Inserts a new blood unit into inventory.
     */
    public boolean createBloodUnit(BloodUnit unit) {
        String sql = "INSERT INTO blood_units (blood_type, collection_date, expiry_date, donor_id, available) "
                   + "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, unit.getBloodType().name());
            stmt.setDate(2, new java.sql.Date(unit.getCollectionDate().getTime()));
            stmt.setDate(3, new java.sql.Date(unit.getExpiryDate().getTime()));
            stmt.setInt(4, unit.getDonorId());
            stmt.setBoolean(5, unit.isAvailable());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Marks a blood unit as unavailable (assigned to a shipment).
     */
    public boolean markUnavailable(int unitId) {
        String sql = "UPDATE blood_units SET available = FALSE WHERE id = ?";
        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, unitId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Deletes a blood unit by ID.
     */
    public boolean deleteBloodUnit(int unitId) {
        String sql = "DELETE FROM blood_units WHERE id = ?";
        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, unitId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Retrieves all blood units (for full inventory view).
     */
    public List<BloodUnit> getAllBloodUnits() {
        List<BloodUnit> units = new ArrayList<>();
        String sql = "SELECT * FROM blood_units ORDER BY blood_type, expiry_date";
        try (Statement stmt = DBConnection.getConnection().createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                units.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return units;
    }

    /**
     * Retrieves available (not assigned, not expired) units of a specific blood type.
     * Used when preparing a shipment to show which units can be assigned.
     */
    public List<BloodUnit> getAvailableByType(BloodType bloodType) {
        List<BloodUnit> units = new ArrayList<>();
        String sql = "SELECT * FROM blood_units "
                   + "WHERE blood_type = ? AND available = TRUE AND expiry_date > CURDATE() "
                   + "ORDER BY expiry_date ASC"; // Oldest first (use soonest-expiring units first)
        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, bloodType.name());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                units.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return units;
    }

    /**
     * Returns a count of available units per blood type.
     * Used for the inventory summary / threshold comparison.
     * Key = BloodType, Value = count of available non-expired units.
     */
    public Map<BloodType, Integer> getAvailableCounts() {
        Map<BloodType, Integer> counts = new LinkedHashMap<>();
        // Initialize all types to 0
        for (BloodType bt : BloodType.values()) {
            counts.put(bt, 0);
        }
        String sql = "SELECT blood_type, COUNT(*) AS cnt FROM blood_units "
                   + "WHERE available = TRUE AND expiry_date > CURDATE() "
                   + "GROUP BY blood_type";
        try (Statement stmt = DBConnection.getConnection().createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                BloodType bt = BloodType.valueOf(rs.getString("blood_type"));
                counts.put(bt, rs.getInt("cnt"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return counts;
    }

    private BloodUnit mapRow(ResultSet rs) throws SQLException {
        return new BloodUnit(
            rs.getInt("id"),
            BloodType.valueOf(rs.getString("blood_type")),
            rs.getDate("collection_date"),
            rs.getDate("expiry_date"),
            rs.getInt("donor_id"),
            rs.getBoolean("available")
        );
    }
}