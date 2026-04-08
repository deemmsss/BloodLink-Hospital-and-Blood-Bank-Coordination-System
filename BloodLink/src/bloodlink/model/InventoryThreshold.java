/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bloodlink.model;

import bloodlink.model.enums.BloodType;

/**
 *
 * @author demi
 * 
 * Represents the minimum acceptable inventory level for a blood type.
 * Configured by the Blood Bank Admin.
 * Used to display low-stock warnings in the UI.
 */
public class InventoryThreshold {

    private int id;
    private BloodType bloodType;
    private int minimumUnits;

    // ── Constructors ─────────────────────────────
    public InventoryThreshold() {
    }

    public InventoryThreshold(int id, BloodType bloodType, int minimumUnits) {
        this.id = id;
        this.bloodType = bloodType;
        this.minimumUnits = minimumUnits;
    }

    // ── Business logic ───────────────────────────
    /**
     * Returns true if the current stock is below the threshold.
     * @param currentCount the number of available units of this blood type
     */
    public boolean isLow(int currentCount) {
        return currentCount < minimumUnits;
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

    public int getMinimumUnits() {
        return minimumUnits;
    }

    public void setMinimumUnits(int minimumUnits) {
        this.minimumUnits = minimumUnits;
    }

    @Override
    public String toString() {
        return bloodType.getDisplayName() + " — min: " + minimumUnits;
    }
}
