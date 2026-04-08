/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bloodlink.model;

import bloodlink.model.enums.UserRole;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author demi
 * 
 * Represents a Hospital Nurse.
 * Nurses manage patients, create blood requests, and confirm receipt of shipments.
 */
public class HospitalNurse extends User {

    private String department;

    // ── Constructors ─────────────────────────────
    public HospitalNurse() {
        setRole(UserRole.HOSPITAL_NURSE);
    }

    public HospitalNurse(int id, String username, String password, String fullName,
                         String email, boolean active, String department) {
        super(id, username, password, fullName, email, UserRole.HOSPITAL_NURSE, active);
        this.department = department;
    }

    // ── Abstract method implementation ───────────
    @Override
    public String getDashboardTitle() {
        return "Nurse Dashboard — General Hospital";
    }

    // ── Role-specific methods ────────────────────
    // Actual DB logic will live in the DAO layer.

    public void createBloodRequest(BloodRequest request) {
        // Delegated to BloodRequestDAO in the UI layer
    }

    public List<BloodRequest> viewMyRequests() {
        return new ArrayList<>();
    }

    public void confirmReceipt(int requestId) {
        // Delegated to ShipmentDAO in the UI layer
    }

    public void managePatient(Patient patient) {
        // Delegated to PatientDAO in the UI layer
    }

    // ── Getters & Setters ────────────────────────
    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
}
