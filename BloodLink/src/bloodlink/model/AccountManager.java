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
 * Interface for roles that manage user accounts.
 * Implemented by HospitalAdmin (manages nurses) and BloodBankAdmin (manages technicians).
 * 
 * This enforces a shared CRUD contract for account management across both enterprises.
 */
public interface AccountManager {

    /**
     * Creates a new user account.
     * @param user the User object to persist
     */
    void createAccount(User user);

    /**
     * Updates an existing user account.
     * @param user the User object with updated fields
     */
    void updateAccount(User user);

    /**
     * Deactivates (soft-deletes) a user account.
     * @param userId the ID of the user to deactivate
     */
    void deleteAccount(int userId);

    /**
     * Lists all accounts managed by this role.
     * @return list of User objects
     */
    List<User> listAccounts();
}
