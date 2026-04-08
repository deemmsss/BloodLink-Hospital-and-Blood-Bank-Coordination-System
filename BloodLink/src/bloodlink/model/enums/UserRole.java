/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bloodlink.model.enums;

/**
 *
 * @author demi
 * 
 * The four roles in the BloodLink system.
 * Maps directly to the ENUM column in the users table.
 */
public enum UserRole {
    HOSPITAL_NURSE("Hospital Nurse"),
    HOSPITAL_ADMIN("Hospital Admin"),
    BLOOD_BANK_TECHNICIAN("Blood Bank Technician"),
    BLOOD_BANK_ADMIN("Blood Bank Admin");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Returns true if this role belongs to the General Hospital enterprise.
     */
    public boolean isHospitalRole() {
        return this == HOSPITAL_NURSE || this == HOSPITAL_ADMIN;
    }

    /**
     * Returns true if this role belongs to the City Blood Bank enterprise.
     */
    public boolean isBloodBankRole() {
        return this == BLOOD_BANK_TECHNICIAN || this == BLOOD_BANK_ADMIN;
    }

    /**
     * Returns true if this role is an admin role.
     */
    public boolean isAdmin() {
        return this == HOSPITAL_ADMIN || this == BLOOD_BANK_ADMIN;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
