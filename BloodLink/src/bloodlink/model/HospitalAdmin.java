/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bloodlink.model;

import bloodlink.model.enums.RequestPriority;
import bloodlink.model.enums.UserRole;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author demi
 * 
 * Represents a Hospital Admin.
 * Admins manage nurse accounts, view hospital-wide blood usage,
 * and escalate/prioritize urgent requests.
 * 
 * Implements AccountManager (shared CRUD contract for user management).
 */
public class HospitalAdmin extends User implements AccountManager {

    // ── Constructors ─────────────────────────────
    public HospitalAdmin() {
        setRole(UserRole.HOSPITAL_ADMIN);
    }

    public HospitalAdmin(int id, String username, String password, String fullName,
                         String email, boolean active) {
        super(id, username, password, fullName, email, UserRole.HOSPITAL_ADMIN, active);
    }

    // ── Abstract method implementation ───────────
    @Override
    public String getDashboardTitle() {
        return "Admin Dashboard — General Hospital";
    }

    // ── AccountManager interface ─────────────────
    @Override
    public void createAccount(User user) {
        // Delegated to UserDAO
    }

    @Override
    public void updateAccount(User user) {
        // Delegated to UserDAO
    }

    @Override
    public void deleteAccount(int userId) {
        // Delegated to UserDAO
    }

    @Override
    public List<User> listAccounts() {
        return new ArrayList<>();
    }

    // ── Role-specific methods ────────────────────
    public void escalateRequest(int requestId, RequestPriority priority) {
        // Delegated to BloodRequestDAO
    }

    public List<BloodRequest> viewHospitalBloodUsage() {
        return new ArrayList<>();
    }
}
