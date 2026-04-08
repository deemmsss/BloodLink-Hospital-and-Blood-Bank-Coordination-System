/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bloodlink.dao;

import bloodlink.db.DBConnection;
import bloodlink.model.*;
import bloodlink.model.enums.UserRole;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author demi
 * 
 * Data Access Object for the users table.
 * Handles all SQL operations related to user accounts.
 * Used by: Login screen, HospitalAdmin (manage nurses), BloodBankAdmin (manage techs).
 */
public class UserDAO {

    // ──────────────────────────────────────────────
    // AUTHENTICATE — used by the login screen
    // ──────────────────────────────────────────────
    /**
     * Attempts to find a user with the given username and password.
     * Returns the appropriate User subclass based on role, or null if not found.
     */
    public User authenticate(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ? AND active = TRUE";
        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapRowToUser(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ──────────────────────────────────────────────
    // CRUD OPERATIONS
    // ──────────────────────────────────────────────

    /**
     * Inserts a new user into the database.
     * Used by admins when creating nurse or technician accounts.
     */
    public boolean createUser(User user) {
        String sql = "INSERT INTO users (username, password, full_name, email, role, department, lab_certification, active) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getFullName());
            stmt.setString(4, user.getEmail());
            stmt.setString(5, user.getRole().name());
            // Set department only for nurses, null for others
            if (user instanceof HospitalNurse) {
                stmt.setString(6, ((HospitalNurse) user).getDepartment());
            } else {
                stmt.setNull(6, Types.VARCHAR);
            }
            // Set lab certification only for technicians, null for others
            if (user instanceof BloodBankTechnician) {
                stmt.setString(7, ((BloodBankTechnician) user).getLabCertification());
            } else {
                stmt.setNull(7, Types.VARCHAR);
            }
            stmt.setBoolean(8, user.isActive());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Updates an existing user's details.
     */
    public boolean updateUser(User user) {
        String sql = "UPDATE users SET username = ?, password = ?, full_name = ?, email = ?, "
                   + "department = ?, lab_certification = ?, active = ? WHERE id = ?";
        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getFullName());
            stmt.setString(4, user.getEmail());
            if (user instanceof HospitalNurse) {
                stmt.setString(5, ((HospitalNurse) user).getDepartment());
            } else {
                stmt.setNull(5, Types.VARCHAR);
            }
            if (user instanceof BloodBankTechnician) {
                stmt.setString(6, ((BloodBankTechnician) user).getLabCertification());
            } else {
                stmt.setNull(6, Types.VARCHAR);
            }
            stmt.setBoolean(7, user.isActive());
            stmt.setInt(8, user.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Soft-deletes a user by setting active = false.
     * We don't hard-delete because other tables reference user IDs.
     */
    public boolean deactivateUser(int userId) {
        String sql = "UPDATE users SET active = FALSE WHERE id = ?";
        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Retrieves all users with a specific role.
     * Used by HospitalAdmin to list nurses, BloodBankAdmin to list technicians.
     */
    public List<User> getUsersByRole(UserRole role) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role = ? ORDER BY full_name";
        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, role.name());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                users.add(mapRowToUser(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    /**
     * Retrieves a single user by ID.
     */
    public User getUserById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapRowToUser(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ──────────────────────────────────────────────
    // HELPER — maps a ResultSet row to the correct User subclass
    // ──────────────────────────────────────────────
    /**
     * Reads one row from the ResultSet and returns the appropriate
     * User subclass (HospitalNurse, HospitalAdmin, etc.) based on
     * the role column. This is where polymorphism starts — the caller
     * gets back a User reference that is actually a specific subclass.
     */
    private User mapRowToUser(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String username = rs.getString("username");
        String password = rs.getString("password");
        String fullName = rs.getString("full_name");
        String email = rs.getString("email");
        boolean active = rs.getBoolean("active");
        UserRole role = UserRole.valueOf(rs.getString("role"));

        switch (role) {
            case HOSPITAL_NURSE:
                String dept = rs.getString("department");
                return new HospitalNurse(id, username, password, fullName, email, active, dept);

            case HOSPITAL_ADMIN:
                return new HospitalAdmin(id, username, password, fullName, email, active);

            case BLOOD_BANK_TECHNICIAN:
                String cert = rs.getString("lab_certification");
                return new BloodBankTechnician(id, username, password, fullName, email, active, cert);

            case BLOOD_BANK_ADMIN:
                return new BloodBankAdmin(id, username, password, fullName, email, active);

            default:
                throw new SQLException("Unknown role: " + role);
        }
    }
}
