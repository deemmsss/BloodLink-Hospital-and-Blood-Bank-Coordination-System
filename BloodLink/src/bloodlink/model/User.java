/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bloodlink.model;

import bloodlink.model.enums.UserRole;

/**
 *
 * @author demi
 * 
 * Abstract base class for all users in BloodLink.
 * Contains shared fields (id, username, password, etc.) and forces
 * each subclass to implement getDashboardTitle().
 * 
 * This satisfies: inheritance, abstract class, abstract method, encapsulation.
 */
public abstract class User {

    private int id;
    private String username;
    private String password;
    private String fullName;
    private String email;
    private UserRole role;
    private boolean active;

    // ── Constructor ──────────────────────────────
    public User() {
    }

    public User(int id, String username, String password, String fullName,
                String email, UserRole role, boolean active) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.active = active;
    }

    // ── Abstract method ──────────────────────────
    /**
     * Returns the title shown at the top of this role's dashboard.
     * Each subclass provides its own label.
     */
    public abstract String getDashboardTitle();

    // ── Getters & Setters ────────────────────────
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return fullName + " (" + role.getDisplayName() + ")";
    }
}
