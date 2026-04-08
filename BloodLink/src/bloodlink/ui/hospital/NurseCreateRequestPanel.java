/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bloodlink.ui.hospital;

import bloodlink.dao.BloodRequestDAO;
import bloodlink.dao.PatientDAO;
import bloodlink.model.BloodRequest;
import bloodlink.model.Patient;
import bloodlink.model.User;
import bloodlink.model.enums.BloodType;
import bloodlink.model.enums.RequestPriority;
import bloodlink.model.enums.RequestStatus;
import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 *
 * @author demi
 * 
 * Panel for the Hospital Nurse to create a new blood unit request.
 * This is Work Request #1 (cross-enterprise: Hospital → Blood Bank).
 * 
 * Layout: A centered form card with dropdowns and a submit button.
 * The nurse selects a patient, blood type, number of units,
 * priority level, and optional notes.
 */
public class NurseCreateRequestPanel extends JPanel {

    private User currentUser;
    private PatientDAO patientDAO;
    private BloodRequestDAO requestDAO;

    // Form fields
    private JComboBox<String> patientCombo;
    private JComboBox<String> bloodTypeCombo;
    private JSpinner unitsSpinner;
    private JComboBox<String> priorityCombo;
    private JTextArea notesArea;
    private JButton submitBtn, refreshBtn;

    // Stores patient IDs parallel to the combo box items
    private List<Patient> patientList;

    public NurseCreateRequestPanel(User currentUser) {
        this.currentUser = currentUser;
        this.patientDAO = new PatientDAO();
        this.requestDAO = new BloodRequestDAO();
        setLayout(new GridBagLayout());
        setBackground(Color.WHITE);
        initComponents();
        loadPatients();
    }

    private void initComponents() {
        // ── Outer form panel with a titled border ──
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("New Blood Unit Request"),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Row 0: Patient selection
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Patient:"), gbc);
        patientCombo = new JComboBox<>();
        patientCombo.setPreferredSize(new Dimension(250, 30));
        gbc.gridx = 1;
        formPanel.add(patientCombo, gbc);

        // Refresh button to reload patients if they just added one
        refreshBtn = new JButton("Refresh");
        refreshBtn.setFont(new Font("SansSerif", Font.PLAIN, 11));
        gbc.gridx = 2;
        formPanel.add(refreshBtn, gbc);

        // Row 1: Blood Type
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Blood Type Needed:"), gbc);
        String[] types = new String[BloodType.values().length];
        for (int i = 0; i < BloodType.values().length; i++) {
            types[i] = BloodType.values()[i].getDisplayName();
        }
        bloodTypeCombo = new JComboBox<>(types);
        gbc.gridx = 1;
        formPanel.add(bloodTypeCombo, gbc);

        // Row 2: Units requested
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Units Requested:"), gbc);
        unitsSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 20, 1));
        gbc.gridx = 1;
        formPanel.add(unitsSpinner, gbc);

        // Row 3: Priority
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Priority:"), gbc);
        String[] priorities = new String[RequestPriority.values().length];
        for (int i = 0; i < RequestPriority.values().length; i++) {
            priorities[i] = RequestPriority.values()[i].getDisplayName();
        }
        priorityCombo = new JComboBox<>(priorities);
        gbc.gridx = 1;
        formPanel.add(priorityCombo, gbc);

        // Row 4: Notes
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        formPanel.add(new JLabel("Notes:"), gbc);
        notesArea = new JTextArea(4, 25);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        gbc.gridx = 1; gbc.gridwidth = 2;
        formPanel.add(new JScrollPane(notesArea), gbc);

        // Row 5: Submit button
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        submitBtn = new JButton("Submit Blood Request");
        submitBtn.setBackground(new Color(180, 30, 30));
        submitBtn.setForeground(Color.WHITE);
        submitBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        submitBtn.setFocusPainted(false);
        submitBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        formPanel.add(submitBtn, gbc);

        // Add the form card to the center of this panel
        add(formPanel);

        // ── Event handlers ───────────────────────
        submitBtn.addActionListener(e -> submitRequest());
        refreshBtn.addActionListener(e -> loadPatients());

        // Auto-fill blood type when a patient is selected
        patientCombo.addActionListener(e -> autoFillBloodType());
    }

    /**
     * Loads the nurse's patients into the combo box.
     */
    private void loadPatients() {
        patientCombo.removeAllItems();
        patientList = patientDAO.getPatientsByNurse(currentUser.getId());
        if (patientList.isEmpty()) {
            patientCombo.addItem("-- No patients. Add patients first. --");
            submitBtn.setEnabled(false);
        } else {
            submitBtn.setEnabled(true);
            for (Patient p : patientList) {
                patientCombo.addItem(p.getFullName() + " (ID: " + p.getId() + ")");
            }
            autoFillBloodType();
        }
    }

    /**
     * When a patient is selected, automatically set the blood type
     * combo to match the patient's blood type.
     */
    private void autoFillBloodType() {
        int idx = patientCombo.getSelectedIndex();
        if (idx >= 0 && idx < patientList.size()) {
            Patient selected = patientList.get(idx);
            bloodTypeCombo.setSelectedItem(selected.getBloodType().getDisplayName());
        }
    }

    /**
     * Validates and submits the blood request.
     */
    private void submitRequest() {
        int idx = patientCombo.getSelectedIndex();
        if (idx < 0 || idx >= patientList.size()) {
            JOptionPane.showMessageDialog(this, "Please select a valid patient.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Patient patient = patientList.get(idx);
        BloodType bloodType = BloodType.fromDisplayName((String) bloodTypeCombo.getSelectedItem());
        int units = (int) unitsSpinner.getValue();
        RequestPriority priority = RequestPriority.values()[priorityCombo.getSelectedIndex()];

        // Build the request object
        BloodRequest req = new BloodRequest();
        req.setPatientId(patient.getId());
        req.setNurseId(currentUser.getId());
        req.setBloodType(bloodType);
        req.setUnitsRequested(units);
        req.setStatus(RequestStatus.PENDING);
        req.setPriority(priority);
        req.setNotes(notesArea.getText().trim());

        if (requestDAO.createRequest(req)) {
            JOptionPane.showMessageDialog(this,
                "Blood request submitted successfully!\n"
                + "Status: PENDING — awaiting Blood Bank processing.",
                "Request Submitted", JOptionPane.INFORMATION_MESSAGE);
            // Reset form
            unitsSpinner.setValue(1);
            priorityCombo.setSelectedIndex(0);
            notesArea.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "Failed to submit request.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
