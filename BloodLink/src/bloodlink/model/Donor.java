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
 * Represents a blood donor registered with the City Blood Bank.
 * Managed by the Blood Bank Admin.
 */
public class Donor {

    private int id;
    private String fullName;
    private Date dateOfBirth;
    private BloodType bloodType;
    private String phone;
    private Date lastDonationDate;
    private boolean eligibleToDonate;

    // ── Constructors ─────────────────────────────
    public Donor() {
    }

    public Donor(int id, String fullName, Date dateOfBirth, BloodType bloodType,
                 String phone, Date lastDonationDate, boolean eligibleToDonate) {
        this.id = id;
        this.fullName = fullName;
        this.dateOfBirth = dateOfBirth;
        this.bloodType = bloodType;
        this.phone = phone;
        this.lastDonationDate = lastDonationDate;
        this.eligibleToDonate = eligibleToDonate;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Date getLastDonationDate() {
        return lastDonationDate;
    }

    public void setLastDonationDate(Date lastDonationDate) {
        this.lastDonationDate = lastDonationDate;
    }

    public boolean isEligibleToDonate() {
        return eligibleToDonate;
    }

    public void setEligibleToDonate(boolean eligibleToDonate) {
        this.eligibleToDonate = eligibleToDonate;
    }

    @Override
    public String toString() {
        return fullName + " (" + bloodType.getDisplayName() + ")";
    }
}
