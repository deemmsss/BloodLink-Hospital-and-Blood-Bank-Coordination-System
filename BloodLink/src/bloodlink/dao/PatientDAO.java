/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bloodlink.dao;

import bloodlink.db.DBConnection;
import bloodlink.model.Patient;
import bloodlink.model.enums.BloodType;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author demi
 * 
 * Data Access Object for the patients table.
 * Used by: Hospital Nurse (full CRUD), Hospital Admin (read-only views).
 */
public class PatientDAO {

    /**
     * Inserts a new patient record.
     * @return true if the insert succeeded
     */
    public boolean createPatient(Patient patient) {
        String sql = "INSERT INTO patients (full_name, date_of_birth, blood_type, ward, medical_notes, nurse_id) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, patient.getFullName());
            stmt.setDate(2, new java.sql.Date(patient.getDateOfBirth().getTime()));
            stmt.setString(3, patient.getBloodType().name());
            stmt.setString(4, patient.getWard());
            stmt.setString(5, patient.getMedicalNotes());
            stmt.setInt(6, patient.getNurseId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Updates an existing patient record.
     */
    public boolean updatePatient(Patient patient) {
        String sql = "UPDATE patients SET full_name = ?, date_of_birth = ?, blood_type = ?, "
                   + "ward = ?, medical_notes = ? WHERE id = ?";
        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, patient.getFullName());
            stmt.setDate(2, new java.sql.Date(patient.getDateOfBirth().getTime()));
            stmt.setString(3, patient.getBloodType().name());
            stmt.setString(4, patient.getWard());
            stmt.setString(5, patient.getMedicalNotes());
            stmt.setInt(6, patient.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Deletes a patient record by ID.
     * Only allowed if the patient has no blood requests (enforced in UI).
     */
    public boolean deletePatient(int patientId) {
        String sql = "DELETE FROM patients WHERE id = ?";
        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, patientId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Checks whether a patient has any blood requests.
     * Used to prevent deletion of patients with existing requests.
     */
    public boolean hasBloodRequests(int patientId) {
        String sql = "SELECT COUNT(*) FROM blood_requests WHERE patient_id = ?";
        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, patientId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Retrieves all patients assigned to a specific nurse.
     */
    public List<Patient> getPatientsByNurse(int nurseId) {
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT * FROM patients WHERE nurse_id = ? ORDER BY full_name";
        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, nurseId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                patients.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return patients;
    }

    /**
     * Retrieves all patients (used by Hospital Admin for reporting).
     */
    public List<Patient> getAllPatients() {
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT * FROM patients ORDER BY full_name";
        try (Statement stmt = DBConnection.getConnection().createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                patients.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return patients;
    }

    /**
     * Retrieves a single patient by ID.
     */
    public Patient getPatientById(int id) {
        String sql = "SELECT * FROM patients WHERE id = ?";
        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Maps a ResultSet row to a Patient object.
     */
    private Patient mapRow(ResultSet rs) throws SQLException {
        return new Patient(
            rs.getInt("id"),
            rs.getString("full_name"),
            rs.getDate("date_of_birth"),
            BloodType.valueOf(rs.getString("blood_type")),
            rs.getString("ward"),
            rs.getString("medical_notes"),
            rs.getInt("nurse_id")
        );
    }
}