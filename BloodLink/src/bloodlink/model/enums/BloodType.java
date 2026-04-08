/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bloodlink.model.enums;

/**
 *
 * @author demi
 */

/**
 * All 8 ABO/Rh blood types.
 */
public enum BloodType {
    A_POSITIVE("A+"),
    A_NEGATIVE("A-"),
    B_POSITIVE("B+"),
    B_NEGATIVE("B-"),
    AB_POSITIVE("AB+"),
    AB_NEGATIVE("AB-"),
    O_POSITIVE("O+"),
    O_NEGATIVE("O-");

    private final String displayName;

    BloodType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Returns the enum constant matching a display name like "A+" or "O-".
     */
    public static BloodType fromDisplayName(String name) {
        for (BloodType bt : values()) {
            if (bt.displayName.equals(name)) {
                return bt;
            }
        }
        throw new IllegalArgumentException("Unknown blood type: " + name);
    }

    @Override
    public String toString() {
        return displayName;
    }
}
