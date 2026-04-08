/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bloodlink.dao;

import bloodlink.db.DBConnection;
import bloodlink.model.InventoryThreshold;
import bloodlink.model.enums.BloodType;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author demi
 * 
 * Data Access Object for the inventory_thresholds table.
 * Used by: Blood Bank Admin (CRUD), TechInventoryPanel (read).
 */
public class InventoryThresholdDAO {

    /**
     * Retrieves all threshold settings.
     */
    public List<InventoryThreshold> getAllThresholds() {
        List<InventoryThreshold> thresholds = new ArrayList<>();
        String sql = "SELECT * FROM inventory_thresholds ORDER BY blood_type";
        try (Statement stmt = DBConnection.getConnection().createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                thresholds.add(new InventoryThreshold(
                    rs.getInt("id"),
                    BloodType.valueOf(rs.getString("blood_type")),
                    rs.getInt("minimum_units")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return thresholds;
    }

    /**
     * Updates the minimum threshold for a specific blood type.
     */
    public boolean updateThreshold(BloodType bloodType, int minimumUnits) {
        String sql = "UPDATE inventory_thresholds SET minimum_units = ? WHERE blood_type = ?";
        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, minimumUnits);
            stmt.setString(2, bloodType.name());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
