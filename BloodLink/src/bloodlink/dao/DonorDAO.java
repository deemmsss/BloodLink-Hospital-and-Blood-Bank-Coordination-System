/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bloodlink.dao;

import bloodlink.db.DBConnection;
import bloodlink.model.Donor;
import bloodlink.model.enums.BloodType;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author demi
 * 
 * Data Access Object for the donors table.
 * Used by: Blood Bank Admin (full CRUD — Work Request #4).
 */
public class DonorDAO {

    public boolean createDonor(Donor donor) {
        String sql = "INSERT INTO donors (full_name, date_of_birth, blood_type, phone, last_donation_date, eligible_to_donate) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, donor.getFullName());
            stmt.setDate(2, new java.sql.Date(donor.getDateOfBirth().getTime()));
            stmt.setString(3, donor.getBloodType().name());
            stmt.setString(4, donor.getPhone());
            if (donor.getLastDonationDate() != null) {
                stmt.setDate(5, new java.sql.Date(donor.getLastDonationDate().getTime()));
            } else {
                stmt.setNull(5, Types.DATE);
            }
            stmt.setBoolean(6, donor.isEligibleToDonate());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateDonor(Donor donor) {
        String sql = "UPDATE donors SET full_name = ?, date_of_birth = ?, blood_type = ?, phone = ?, "
                   + "last_donation_date = ?, eligible_to_donate = ? WHERE id = ?";
        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, donor.getFullName());
            stmt.setDate(2, new java.sql.Date(donor.getDateOfBirth().getTime()));
            stmt.setString(3, donor.getBloodType().name());
            stmt.setString(4, donor.getPhone());
            if (donor.getLastDonationDate() != null) {
                stmt.setDate(5, new java.sql.Date(donor.getLastDonationDate().getTime()));
            } else {
                stmt.setNull(5, Types.DATE);
            }
            stmt.setBoolean(6, donor.isEligibleToDonate());
            stmt.setInt(7, donor.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteDonor(int donorId) {
        String sql = "DELETE FROM donors WHERE id = ?";
        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, donorId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Checks if a donor has blood units in the system.
     * Prevents deletion of donors with existing units.
     */
    public boolean hasBloodUnits(int donorId) {
        String sql = "SELECT COUNT(*) FROM blood_units WHERE donor_id = ?";
        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, donorId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Donor> getAllDonors() {
        List<Donor> donors = new ArrayList<>();
        String sql = "SELECT * FROM donors ORDER BY full_name";
        try (Statement stmt = DBConnection.getConnection().createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                donors.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return donors;
    }

    public Donor getDonorById(int id) {
        String sql = "SELECT * FROM donors WHERE id = ?";
        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Donor mapRow(ResultSet rs) throws SQLException {
        return new Donor(
            rs.getInt("id"),
            rs.getString("full_name"),
            rs.getDate("date_of_birth"),
            BloodType.valueOf(rs.getString("blood_type")),
            rs.getString("phone"),
            rs.getDate("last_donation_date"),
            rs.getBoolean("eligible_to_donate")
        );
    }
}
