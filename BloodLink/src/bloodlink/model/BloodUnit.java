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
 * Represents a single unit of blood in the Blood Bank's inventory.
 * Each unit is linked to the donor who provided it.
 * When assigned to a shipment, 'available' is set to false.
 */
public class BloodUnit {

    private int id;
    private BloodType bloodType;
    private Date collectionDate;
    private Date expiryDate;
    private int donorId;
    private boolean available;

    // ── Constructors ─────────────────────────────
    public BloodUnit() {
    }

    public BloodUnit(int id, BloodType bloodType, Date collectionDate,
                     Date expiryDate, int donorId, boolean available) {
        this.id = id;
        this.bloodType = bloodType;
        this.collectionDate = collectionDate;
        this.expiryDate = expiryDate;
        this.donorId = donorId;
        this.available = available;
    }

    // ── Business logic ───────────────────────────
    /**
     * Checks whether this blood unit has passed its expiry date.
     */
    public boolean isExpired() {
        return new Date().after(expiryDate);
    }

    // ── Getters & Setters ────────────────────────
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public BloodType getBloodType() {
        return bloodType;
    }

    public void setBloodType(BloodType bloodType) {
        this.bloodType = bloodType;
    }

    public Date getCollectionDate() {
        return collectionDate;
    }

    public void setCollectionDate(Date collectionDate) {
        this.collectionDate = collectionDate;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public int getDonorId() {
        return donorId;
    }

    public void setDonorId(int donorId) {
        this.donorId = donorId;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public String toString() {
        return "Unit #" + id + " (" + bloodType.getDisplayName() + ")";
    }
}
