/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bloodlink.model;

import java.util.Date;

/**
 *
 * @author demi
 * 
 * Represents a shipment of blood units from the Blood Bank to the Hospital.
 * Created by a Technician when fulfilling an approved blood request.
 * Confirmed by the Nurse when the units arrive at the hospital.
 * 
 * This is the second cross-enterprise work request
 * (Blood Bank → Hospital confirmation).
 */
public class Shipment {

    private int id;
    private int requestId;
    private int technicianId;
    private Date shipDate;
    private Date receivedDate;
    private boolean confirmed;

    // ── Extra fields for display ─────────────────
    private String technicianName;

    // ── Constructors ─────────────────────────────
    public Shipment() {
    }

    public Shipment(int id, int requestId, int technicianId,
                    Date shipDate, Date receivedDate, boolean confirmed) {
        this.id = id;
        this.requestId = requestId;
        this.technicianId = technicianId;
        this.shipDate = shipDate;
        this.receivedDate = receivedDate;
        this.confirmed = confirmed;
    }

    // ── Business logic ───────────────────────────
    /**
     * Marks this shipment as received by the hospital.
     * @param receivedDate the date/time the units arrived
     */
    public void confirm(Date receivedDate) {
        this.receivedDate = receivedDate;
        this.confirmed = true;
    }

    // ── Getters & Setters ────────────────────────
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public int getTechnicianId() {
        return technicianId;
    }

    public void setTechnicianId(int technicianId) {
        this.technicianId = technicianId;
    }

    public Date getShipDate() {
        return shipDate;
    }

    public void setShipDate(Date shipDate) {
        this.shipDate = shipDate;
    }

    public Date getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(Date receivedDate) {
        this.receivedDate = receivedDate;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public String getTechnicianName() {
        return technicianName;
    }

    public void setTechnicianName(String technicianName) {
        this.technicianName = technicianName;
    }

    @Override
    public String toString() {
        return "Shipment #" + id + " for Request #" + requestId
                + (confirmed ? " (Received)" : " (In Transit)");
    }
}
