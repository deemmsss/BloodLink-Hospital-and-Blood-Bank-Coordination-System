/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bloodlink.model;

import bloodlink.model.enums.BloodType;
import java.util.Date;

/**
 *
 * @author demi
 * 
 * Represents a patient in the hospital.
 * Managed by Hospital Nurses. Each patient is linked to the nurse
 * who registered them (nurse_id foreign key).
 */
public class Patient {

    private int id;
    private String fullName;
    private Date dateOfBirth;
    private BloodType bloodType;
    private String ward;
    private String medicalNotes;
    private int nurseId;

    // ── Constructors ─────────────────────────────
    public Patient() {
    }

    public Patient(int id, String fullName, Date dateOfBirth, BloodType bloodType,
                   String ward, String medicalNotes, int nurseId) {
        this.id = id;
        this.fullName = fullName;
        this.dateOfBirth = dateOfBirth;
        this.bloodType = bloodType;
        this.ward = ward;
        this.medicalNotes = medicalNotes;
        this.nurseId = nurseId;
    }

    // ── Getters & Setters ────────────────────────
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public BloodType getBloodType() {
        return bloodType;
    }

    public void setBloodType(BloodType bloodType) {
        this.bloodType = bloodType;
    }

    public String getWard() {
        return ward;
    }

    public void setWard(String ward) {
        this.ward = ward;
    }

    public String getMedicalNotes() {
        return medicalNotes;
    }

    public void setMedicalNotes(String medicalNotes) {
        this.medicalNotes = medicalNotes;
    }

    public int getNurseId() {
        return nurseId;
    }

    public void setNurseId(int nurseId) {
        this.nurseId = nurseId;
    }

    @Override
    public String toString() {
        return fullName + " (" + bloodType.getDisplayName() + ")";
    }
}
