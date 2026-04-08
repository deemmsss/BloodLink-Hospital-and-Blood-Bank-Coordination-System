/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bloodlink.ui.bloodbank;

import bloodlink.dao.BloodUnitDAO;
import bloodlink.dao.DonorDAO;
import bloodlink.model.BloodUnit;
import bloodlink.model.Donor;
import bloodlink.model.enums.BloodType;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 *
 * @author demi
 * 
 * CRUD panel for the Blood Bank Admin to manage donor records
 * and add blood units to inventory from a donor.
 * This is Work Request #4 (internal to Blood Bank enterprise).
 * 
 * Layout:
 * ┌─────────────────────────────────────────────────────┐
 * │  Donor table                                        │
 * ├─────────────────────────────────────────────────────┤
 * │  Donor form (CRUD)                                  │
 * ├─────────────────────────────────────────────────────┤
 * │  "Add Blood Unit from Selected Donor" section       │
 * └─────────────────────────────────────────────────────┘
 */

public class BBAdminManageDonorsPanel extends JPanel {

    private DonorDAO donorDAO;
    private BloodUnitDAO unitDAO;
    private JTable donorTable;
    private DefaultTableModel tableModel;

    // Donor form
    private JTextField nameField, dobField, phoneField, lastDonationField;
    private JComboBox<String> bloodTypeCombo;
    private JCheckBox eligibleCheck;
    private JButton addBtn, updateBtn, deleteBtn, clearBtn;
    private int selectedDonorId = -1;

    // Add blood unit section
    private JButton addUnitBtn;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public BBAdminManageDonorsPanel() {
        this.donorDAO = new DonorDAO();
        this.unitDAO = new BloodUnitDAO();
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        initComponents();
        loadDonors();
    }

    private void initComponents() {
        // ── Title ────────────────────────────────
        JLabel title = new JLabel("Manage Donors & Blood Units");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        // ── Donor table ──────────────────────────
        String[] cols = {"ID", "Name", "DOB", "Blood Type", "Phone", "Last Donation", "Eligible"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        donorTable = new JTable(tableModel);
        donorTable.setRowHeight(25);
        donorTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        donorTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        donorTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) populateForm();
        });

        JScrollPane scrollPane = new JScrollPane(donorTable);
        scrollPane.setPreferredSize(new Dimension(0, 200));
        add(scrollPane, BorderLayout.CENTER);

        // ── Bottom: form + add unit button ───────
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setBackground(Color.WHITE);

        // Donor form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createTitledBorder("Donor Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Full Name:"), gbc);
        nameField = new JTextField(14);
        gbc.gridx = 1; formPanel.add(nameField, gbc);

        gbc.gridx = 2;
        formPanel.add(new JLabel("DOB (yyyy-MM-dd):"), gbc);
        dobField = new JTextField(10);
        gbc.gridx = 3; formPanel.add(dobField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Blood Type:"), gbc);
        String[] types = new String[BloodType.values().length];
        for (int i = 0; i < BloodType.values().length; i++) types[i] = BloodType.values()[i].getDisplayName();
        bloodTypeCombo = new JComboBox<>(types);
        gbc.gridx = 1; formPanel.add(bloodTypeCombo, gbc);

        gbc.gridx = 2;
        formPanel.add(new JLabel("Phone:"), gbc);
        phoneField = new JTextField(10);
        gbc.gridx = 3; formPanel.add(phoneField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Last Donation (yyyy-MM-dd):"), gbc);
        lastDonationField = new JTextField(10);
        gbc.gridx = 1; formPanel.add(lastDonationField, gbc);

        gbc.gridx = 2;
        eligibleCheck = new JCheckBox("Eligible to Donate");
        eligibleCheck.setBackground(Color.WHITE);
        eligibleCheck.setSelected(true);
        gbc.gridx = 3; formPanel.add(eligibleCheck, gbc);

        // CRUD buttons
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 4;
        JPanel crudPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 5));
        crudPanel.setBackground(Color.WHITE);
        addBtn = styled("Add Donor", new Color(46, 125, 50));
        updateBtn = styled("Update", new Color(21, 101, 192));
        deleteBtn = styled("Delete", new Color(198, 40, 40));
        clearBtn = styled("Clear", new Color(120, 120, 120));
        crudPanel.add(addBtn); crudPanel.add(updateBtn);
        crudPanel.add(deleteBtn); crudPanel.add(clearBtn);
        formPanel.add(crudPanel, gbc);

        bottomPanel.add(formPanel);

        // Add blood unit section
        JPanel unitPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        unitPanel.setBackground(Color.WHITE);
        unitPanel.setBorder(BorderFactory.createTitledBorder("Add Blood Unit to Inventory"));
        addUnitBtn = styled("Add 1 Blood Unit from Selected Donor", new Color(120, 60, 180));
        addUnitBtn.setPreferredSize(new Dimension(300, 35));
        unitPanel.add(new JLabel("Collects blood from donor and adds a unit to inventory."));
        unitPanel.add(addUnitBtn);
        bottomPanel.add(unitPanel);

        add(bottomPanel, BorderLayout.SOUTH);

        // ── Actions ──────────────────────────────
        addBtn.addActionListener(e -> addDonor());
        updateBtn.addActionListener(e -> updateDonor());
        deleteBtn.addActionListener(e -> deleteDonor());
        clearBtn.addActionListener(e -> clearForm());
        addUnitBtn.addActionListener(e -> addBloodUnit());
    }

    private void loadDonors() {
        tableModel.setRowCount(0);
        List<Donor> donors = donorDAO.getAllDonors();
        for (Donor d : donors) {
            tableModel.addRow(new Object[]{
                d.getId(), d.getFullName(),
                DATE_FORMAT.format(d.getDateOfBirth()),
                d.getBloodType().getDisplayName(),
                d.getPhone(),
                d.getLastDonationDate() != null ? DATE_FORMAT.format(d.getLastDonationDate()) : "Never",
                d.isEligibleToDonate() ? "Yes" : "No"
            });
        }
    }

    private void populateForm() {
        int row = donorTable.getSelectedRow();
        if (row < 0) return;
        selectedDonorId = (int) tableModel.getValueAt(row, 0);
        nameField.setText((String) tableModel.getValueAt(row, 1));
        dobField.setText((String) tableModel.getValueAt(row, 2));
        bloodTypeCombo.setSelectedItem(tableModel.getValueAt(row, 3));
        phoneField.setText((String) tableModel.getValueAt(row, 4));
        String lastDon = (String) tableModel.getValueAt(row, 5);
        lastDonationField.setText("Never".equals(lastDon) ? "" : lastDon);
        eligibleCheck.setSelected("Yes".equals(tableModel.getValueAt(row, 6)));
    }

    private String validateForm() {
        if (nameField.getText().trim().isEmpty()) return "Full Name is required.";
        if (dobField.getText().trim().isEmpty()) return "Date of Birth is required.";
        try { DATE_FORMAT.setLenient(false); DATE_FORMAT.parse(dobField.getText().trim()); }
        catch (ParseException e) { return "DOB must be yyyy-MM-dd format."; }
        if (phoneField.getText().trim().isEmpty()) return "Phone is required.";
        return null;
    }

    private Donor buildDonor() throws ParseException {
        Donor d = new Donor();
        d.setFullName(nameField.getText().trim());
        d.setDateOfBirth(DATE_FORMAT.parse(dobField.getText().trim()));
        d.setBloodType(BloodType.fromDisplayName((String) bloodTypeCombo.getSelectedItem()));
        d.setPhone(phoneField.getText().trim());
        if (!lastDonationField.getText().trim().isEmpty()) {
            d.setLastDonationDate(DATE_FORMAT.parse(lastDonationField.getText().trim()));
        }
        d.setEligibleToDonate(eligibleCheck.isSelected());
        return d;
    }

    private void addDonor() {
        String err = validateForm();
        if (err != null) { warn(err); return; }
        try {
            Donor d = buildDonor();
            if (donorDAO.createDonor(d)) {
                JOptionPane.showMessageDialog(this, "Donor added.");
                clearForm(); loadDonors();
            } else { error("Failed to add donor."); }
        } catch (ParseException e) { error("Invalid date format."); }
    }

    private void updateDonor() {
        if (selectedDonorId == -1) { warn("Select a donor first."); return; }
        String err = validateForm();
        if (err != null) { warn(err); return; }
        try {
            Donor d = buildDonor();
            d.setId(selectedDonorId);
            if (donorDAO.updateDonor(d)) {
                JOptionPane.showMessageDialog(this, "Donor updated.");
                clearForm(); loadDonors();
            } else { error("Failed to update donor."); }
        } catch (ParseException e) { error("Invalid date format."); }
    }

    private void deleteDonor() {
        if (selectedDonorId == -1) { warn("Select a donor first."); return; }
        if (donorDAO.hasBloodUnits(selectedDonorId)) {
            warn("Cannot delete donor with existing blood units in the system.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this donor?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (donorDAO.deleteDonor(selectedDonorId)) {
                JOptionPane.showMessageDialog(this, "Donor deleted.");
                clearForm(); loadDonors();
            } else { error("Failed to delete donor."); }
        }
    }

    /**
     * Adds a blood unit to inventory from the selected donor.
     * Sets collection date to today and expiry to 42 days later
     * (standard red blood cell shelf life).
     */
    private void addBloodUnit() {
        if (selectedDonorId == -1) { warn("Select a donor first."); return; }

        Donor donor = donorDAO.getDonorById(selectedDonorId);
        if (donor == null) { error("Donor not found."); return; }
        if (!donor.isEligibleToDonate()) { warn("This donor is not eligible to donate."); return; }

        Date today = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        cal.add(Calendar.DAY_OF_MONTH, 42); // Red blood cells expire in ~42 days
        Date expiry = cal.getTime();

        BloodUnit unit = new BloodUnit();
        unit.setBloodType(donor.getBloodType());
        unit.setCollectionDate(today);
        unit.setExpiryDate(expiry);
        unit.setDonorId(donor.getId());
        unit.setAvailable(true);

        if (unitDAO.createBloodUnit(unit)) {
            // Update donor's last donation date
            donor.setLastDonationDate(today);
            donorDAO.updateDonor(donor);

            JOptionPane.showMessageDialog(this,
                    "Blood unit added to inventory!\n"
                    + "Type: " + donor.getBloodType().getDisplayName() + "\n"
                    + "Expires: " + DATE_FORMAT.format(expiry));
            loadDonors();
        } else {
            error("Failed to add blood unit.");
        }
    }

    private void clearForm() {
        selectedDonorId = -1;
        nameField.setText(""); dobField.setText(""); phoneField.setText("");
        lastDonationField.setText(""); bloodTypeCombo.setSelectedIndex(0);
        eligibleCheck.setSelected(true);
        donorTable.clearSelection();
    }

    private JButton styled(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg); btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false); btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void warn(String msg) { JOptionPane.showMessageDialog(this, msg, "Warning", JOptionPane.WARNING_MESSAGE); }
    private void error(String msg) { JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE); }
}