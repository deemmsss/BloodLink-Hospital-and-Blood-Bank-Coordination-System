/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bloodlink.model.enums;

/**
 *
 * @author demi
 * 
 * Tracks a blood request through its life cycle.
 * 
 * Flow: PENDING → APPROVED → FULFILLED → SHIPPED → RECEIVED
 *       PENDING → REJECTED (alternative terminal state)
 */
public enum RequestStatus {
    PENDING("Pending"),
    APPROVED("Approved"),
    REJECTED("Rejected"),
    FULFILLED("Fulfilled"),
    SHIPPED("Shipped"),
    RECEIVED("Received");

    private final String displayName;

    RequestStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
