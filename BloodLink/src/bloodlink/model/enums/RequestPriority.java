/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bloodlink.model.enums;

/**
 *
 * @author demi
 * 
 * Priority level for blood requests.
 * Hospital Admin can escalate a request from ROUTINE to URGENT or EMERGENCY.
 */
public enum RequestPriority {
    ROUTINE("Routine"),
    URGENT("Urgent"),
    EMERGENCY("Emergency");

    private final String displayName;

    RequestPriority(String displayName) {
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
