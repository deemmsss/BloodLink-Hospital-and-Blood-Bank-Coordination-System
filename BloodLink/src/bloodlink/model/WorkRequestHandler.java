/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bloodlink.model;

import java.util.List;

/**
 *
 * @author demi
 * 
 * Interface for roles that can view and process incoming blood requests.
 * Implemented by BloodBankTechnician and BloodBankAdmin.
 */
public interface WorkRequestHandler {

    /**
     * Retrieves all blood requests visible to this role.
     * @return list of BloodRequest objects
     */
    List<BloodRequest> viewRequestQueue();

    /**
     * Approves or rejects a blood request.
     * @param requestId the ID of the request to process
     * @param approve   true to approve, false to reject
     */
    void processRequest(int requestId, boolean approve);
}
