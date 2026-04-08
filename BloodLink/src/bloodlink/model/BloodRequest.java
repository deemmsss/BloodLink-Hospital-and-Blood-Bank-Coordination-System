/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bloodlink.model;

import bloodlink.model.enums.BloodType;
import bloodlink.model.enums.RequestPriority;
import bloodlink.model.enums.RequestStatus;
import java.util.Date;

/**
 *
 * @author demi
 * 
 * Represents a blood unit request created by a Hospital Nurse
 * for a specific patient. This is the primary cross-enterprise
 * work request — it originates in the Hospital and is processed
 * by the Blood Bank.
 * 
 * Status lifecycle:
 *   PENDING → APPROVED → FULFILLED → SHIPPED → RECEIVED
 *   PENDING → REJECTED  (alternative path)
 */
public class BloodRequest {

    private int id;
    private int patientId;
    private int nurseId;
    private BloodType bloodType;
    private int unitsRequested;
    private RequestStatus status;
    private RequestPriority priority;
    private Date requestDate;
    private String notes;

    // ── Extra fields for display purposes ────────
    // These are populated by JOIN queries in the DAO
    // so the UI can show names instead of raw IDs.
    private String patientName;
    private String nurseName;

    // ── Constructors ─────────────────────────────
    public BloodRequest() {
    }

    public BloodRequest(int id, int patientId, int nurseId, BloodType bloodType,
                        int unitsRequested, RequestStatus status, RequestPriority priority,
                        Date requestDate, String notes) {
        this.id = id;
        this.patientId = patientId;
        this.nurseId = nurseId;
        this.bloodType = bloodType;
        this.unitsRequested = unitsRequested;
        this.status = status;
        this.priority = priority;
        this.requestDate = requestDate;
        this.notes = notes;
    }

    // ── Business logic ───────────────────────────
    /**
     * Returns true if this request is still waiting to be processed.
     */
    public boolean isPending() {
        return status == RequestStatus.PENDING;
    }

    /**
     * Returns true because blood requests always cross enterprise boundaries
     * (Hospital → Blood Bank).
     */
    public boolean isCrossEnterprise() {
        return true;
    }

    // ── Getters & Setters ────────────────────────
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public int getNurseId() {
        return nurseId;
    }

    public void setNurseId(int nurseId) {
        this.nurseId = nurseId;
    }

    public BloodType getBloodType() {
        return bloodType;
    }

    public void setBloodType(BloodType bloodType) {
        this.bloodType = bloodType;
    }

    public int getUnitsRequested() {
        return unitsRequested;
    }

    public void setUnitsRequested(int unitsRequested) {
        this.unitsRequested = unitsRequested;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public RequestPriority getPriority() {
        return priority;
    }

    public void setPriority(RequestPriority priority) {
        this.priority = priority;
    }

    public Date getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getNurseName() {
        return nurseName;
    }

    public void setNurseName(String nurseName) {
        this.nurseName = nurseName;
    }

    @Override
    public String toString() {
        return "Request #" + id + " — " + bloodType.getDisplayName()
                + " x" + unitsRequested + " (" + status.getDisplayName() + ")";
    }
}