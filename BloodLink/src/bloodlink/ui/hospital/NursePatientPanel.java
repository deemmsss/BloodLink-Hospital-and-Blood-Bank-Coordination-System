/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bloodlink.ui.hospital;

import bloodlink.dao.PatientDAO;
import bloodlink.model.Patient;
import bloodlink.model.User;
import bloodlink.model.enums.BloodType;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 *
 * @author demi
 * 
 * Full CRUD panel for the Hospital Nurse to manage their patients.
 * 
 * Layout:
 * ┌─────────────────────────────────────────────────────┐
 * │  Title label                                        │
 * ├─────────────────────────────────────────────────────┤
 * │  JTable (scrollable) showing all nurse's patients   │
 * ├─────────────────────────────────────────────────────┤
 * │  Form: Name, DOB, Blood Type, Ward, Notes           │
 * │  Buttons: [Add] [Update] [Delete] [Clear]           │
 * └─────────────────────────────────────────────────────┘
 * 
 * Clicking a row in the table populates the form for editing.
 */
public class NursePatientPanel extends JPanel {

    private User currentUser;
    private PatientDAO patientDAO;

    // Table
    private JTable patientTable;
    private DefaultTableModel tableModel;

    // Form fields
    private JTextField nameField;
    private JTextField dobField;        // Format: yyyy-MM-dd
    private JComboBox<String> bloodTypeCombo;
    private JTextField wardField;
    private JTextArea notesArea;

    // Buttons
    private JButton addBtn, updateBtn, deleteBtn, clearBtn;

    // Tracks which patient is selected for editing
    private int selectedPatientId = -1;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public NursePatientPanel(User currentUser) {
        this.currentUser = currentUser;
        this.patientDAO = new PatientDAO();
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        initComponents();
        loadPatients();
    }

    private void initComponents() {
        // ── Title ────────────────────────────────
        JLabel title = new JLabel("My Patients");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        // ── Table ────────────────────────────────
        String[] columns = {"ID", "Name", "Date of Birth", "Blood Type", "Ward", "Notes"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Prevent inline editing — edits go through the form
            }
        };
        patientTable = new JTable(tableModel);
        patientTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        patientTable.setRowHeight(25);
        patientTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));

        // Hide the ID column from view (we still need it for lookups)
        patientTable.getColumnModel().getColumn(0).setMinWidth(0);
        patientTable.getColumnModel().getColumn(0).setMaxWidth(0);
        patientTable.getColumnModel().getColumn(0).setPreferredWidth(0);

        // When a row is clicked, populate the form
        patientTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                populateFormFromTable();
            }
        });

        JScrollPane scrollPane = new JScrollPane(patientTable);
        scrollPane.setPreferredSize(new Dimension(0, 250));
        add(scrollPane, BorderLayout.CENTER);

        // ── Form panel ───────────────────────────
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createTitledBorder("Patient Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 0: Name
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Full Name:"), gbc);
        nameField = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(nameField, gbc);

        // Row 0 (right side): DOB
        gbc.gridx = 2;
        formPanel.add(new JLabel("Date of Birth (yyyy-MM-dd):"), gbc);
        dobField = new JTextField(10);
        gbc.gridx = 3;
        formPanel.add(dobField, gbc);

        // Row 1: Blood Type
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Blood Type:"), gbc);
        String[] bloodTypes = new String[BloodType.values().length];
        for (int i = 0; i < BloodType.values().length; i++) {
            bloodTypes[i] = BloodType.values()[i].getDisplayName();
        }
        bloodTypeCombo = new JComboBox<>(bloodTypes);
        gbc.gridx = 1;
        formPanel.add(bloodTypeCombo, gbc);

        // Row 1 (right side): Ward
        gbc.gridx = 2;
        formPanel.add(new JLabel("Ward:"), gbc);
        wardField = new JTextField(10);
        gbc.gridx = 3;
        formPanel.add(wardField, gbc);

        // Row 2: Notes (spans full width)
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Medical Notes:"), gbc);
        notesArea = new JTextArea(3, 30);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        JScrollPane notesScroll = new JScrollPane(notesArea);
        gbc.gridx = 1; gbc.gridwidth = 3;
        formPanel.add(notesScroll, gbc);

        // Row 3: Buttons
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 4;
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        btnPanel.setBackground(Color.WHITE);

        addBtn = new JButton("Add Patient");
        updateBtn = new JButton("Update Patient");
        deleteBtn = new JButton("Delete Patient");
        clearBtn = new JButton("Clear Form");

        styleButton(addBtn, new Color(46, 125, 50));
        styleButton(updateBtn, new Color(21, 101, 192));
        styleButton(deleteBtn, new Color(198, 40, 40));
        styleButton(clearBtn, new Color(120, 120, 120));

        btnPanel.add(addBtn);
        btnPanel.add(updateBtn);
        btnPanel.add(deleteBtn);
        btnPanel.add(clearBtn);
        formPanel.add(btnPanel, gbc);

        add(formPanel, BorderLayout.SOUTH);

        // ── Button actions ───────────────────────
        addBtn.addActionListener(e -> addPatient());
        updateBtn.addActionListener(e -> updatePatient());
        deleteBtn.addActionListener(e -> deletePatient());
        clearBtn.addActionListener(e -> clearForm());
    }

    /**
     * Loads all patients for the current nurse into the table.
     */
    private void loadPatients() {
        tableModel.setRowCount(0); // Clear existing rows
        List<Patient> patients = patientDAO.getPatientsByNurse(currentUser.getId());
        for (Patient p : patients) {
            tableModel.addRow(new Object[]{
                p.getId(),
                p.getFullName(),
                DATE_FORMAT.format(p.getDateOfBirth()),
                p.getBloodType().getDisplayName(),
                p.getWard(),
                p.getMedicalNotes()
            });
        }
    }

    /**
     * When a table row is clicked, fill the form fields with that patient's data.
     */
    private void populateFormFromTable() {
        int row = patientTable.getSelectedRow();
        if (row < 0) return;

        selectedPatientId = (int) tableModel.getValueAt(row, 0);
        nameField.setText((String) tableModel.getValueAt(row, 1));
        dobField.setText((String) tableModel.getValueAt(row, 2));
        bloodTypeCombo.setSelectedItem(tableModel.getValueAt(row, 3));
        wardField.setText((String) tableModel.getValueAt(row, 4));
        notesArea.setText((String) tableModel.getValueAt(row, 5));
    }

    /**
     * Validates the form fields.
     * @return error message if invalid, or null if all fields are valid
     */
    private String validateForm() {
        if (nameField.getText().trim().isEmpty()) return "Full Name is required.";
        if (dobField.getText().trim().isEmpty()) return "Date of Birth is required.";
        try {
            DATE_FORMAT.setLenient(false);
            DATE_FORMAT.parse(dobField.getText().trim());
        } catch (ParseException e) {
            return "Date of Birth must be in yyyy-MM-dd format.";
        }
        if (wardField.getText().trim().isEmpty()) return "Ward is required.";
        return null;
    }

    /**
     * Creates a Patient object from the current form values.
     */
    private Patient buildPatientFromForm() throws ParseException {
        Patient p = new Patient();
        p.setFullName(nameField.getText().trim());
        p.setDateOfBirth(DATE_FORMAT.parse(dobField.getText().trim()));
        p.setBloodType(BloodType.fromDisplayName((String) bloodTypeCombo.getSelectedItem()));
        p.setWard(wardField.getText().trim());
        p.setMedicalNotes(notesArea.getText().trim());
        p.setNurseId(currentUser.getId());
        return p;
    }

    private void addPatient() {
        String error = validateForm();
        if (error != null) {
            JOptionPane.showMessageDialog(this, error, "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            Patient p = buildPatientFromForm();
            if (patientDAO.createPatient(p)) {
                JOptionPane.showMessageDialog(this, "Patient added successfully.");
                clearForm();
                loadPatients();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add patient.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Invalid date format.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updatePatient() {
        if (selectedPatientId == -1) {
            JOptionPane.showMessageDialog(this, "Select a patient from the table first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String error = validateForm();
        if (error != null) {
            JOptionPane.showMessageDialog(this, error, "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            Patient p = buildPatientFromForm();
            p.setId(selectedPatientId);
            if (patientDAO.updatePatient(p)) {
                JOptionPane.showMessageDialog(this, "Patient updated successfully.");
                clearForm();
                loadPatients();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update patient.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Invalid date format.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deletePatient() {
        if (selectedPatientId == -1) {
            JOptionPane.showMessageDialog(this, "Select a patient from the table first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // Safety check: don't delete patients with existing blood requests
        if (patientDAO.hasBloodRequests(selectedPatientId)) {
            JOptionPane.showMessageDialog(this,
                "Cannot delete this patient — they have existing blood requests.\nDelete or complete their requests first.",
                "Delete Blocked", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this patient?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (patientDAO.deletePatient(selectedPatientId)) {
                JOptionPane.showMessageDialog(this, "Patient deleted.");
                clearForm();
                loadPatients();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete patient.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Resets the form and deselects the table.
     */
    private void clearForm() {
        selectedPatientId = -1;
        nameField.setText("");
        dobField.setText("");
        bloodTypeCombo.setSelectedIndex(0);
        wardField.setText("");
        notesArea.setText("");
        patientTable.clearSelection();
    }

    /**
     * Applies consistent styling to a button.
     */
    private void styleButton(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}
