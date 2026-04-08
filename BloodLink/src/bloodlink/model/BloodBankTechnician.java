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
 * Represents a Blood Bank Technician.
 * Technicians view incoming requests, process them (approve/reject),
 * prepare shipments, and check inventory levels.
 * 
 * Implements WorkRequestHandler (shared contract for request processing).
 */
public class BloodBankTechnician extends User implements WorkRequestHandler {

    private String labCertification;

    // ── Constructors ─────────────────────────────
    public BloodBankTechnician() {
        setRole(UserRole.BLOOD_BANK_TECHNICIAN);
    }

    public BloodBankTechnician(int id, String username, String password, String fullName,
                               String email, boolean active, String labCertification) {
        super(id, username, password, fullName, email, UserRole.BLOOD_BANK_TECHNICIAN, active);
        this.labCertification = labCertification;
    }

    // ── Abstract method implementation ───────────
    @Override
    public String getDashboardTitle() {
        return "Technician Dashboard — City Blood Bank";
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
    public Shipment prepareShipment(int requestId) {
        // Delegated to ShipmentDAO
        return null;
    }

    public List<BloodUnit> viewInventory() {
        return new ArrayList<>();
    }

    // ── Getters & Setters ────────────────────────
    public String getLabCertification() {
        return labCertification;
    }

    public void setLabCertification(String labCertification) {
        this.labCertification = labCertification;
    }
}
