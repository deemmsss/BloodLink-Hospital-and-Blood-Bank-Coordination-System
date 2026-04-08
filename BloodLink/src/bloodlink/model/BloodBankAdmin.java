/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bloodlink.model;

import bloodlink.model.enums.BloodType;
import bloodlink.model.enums.UserRole;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author demi
 * 
 * Represents a Blood Bank Admin.
 * Admins manage technician accounts, oversee donor records,
 * manage inventory thresholds, and can also view/process requests.
 * 
 * Implements BOTH AccountManager and WorkRequestHandler,
 * demonstrating that a single class can fulfill multiple interface contracts.
 */
public class BloodBankAdmin extends User implements AccountManager, WorkRequestHandler {

    // ── Constructors ─────────────────────────────
    public BloodBankAdmin() {
        setRole(UserRole.BLOOD_BANK_ADMIN);
    }

    public BloodBankAdmin(int id, String username, String password, String fullName,
                          String email, boolean active) {
        super(id, username, password, fullName, email, UserRole.BLOOD_BANK_ADMIN, active);
    }

    // ── Abstract method implementation ───────────
    @Override
    public String getDashboardTitle() {
        return "Admin Dashboard — City Blood Bank";
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

    // ── WorkRequestHandler interface ─────────────
    @Override
    public List<BloodRequest> viewRequestQueue() {
        return new ArrayList<>();
    }

    @Override
    public void processRequest(int requestId, boolean approve) {
        // Delegated to BloodRequestDAO
    }

    // ── Role-specific methods ────────────────────
    public void manageDonor(Donor donor) {
        // Delegated to DonorDAO
    }

    public void setInventoryThreshold(BloodType bloodType, int min) {
        // Delegated to InventoryThresholdDAO
    }
}
