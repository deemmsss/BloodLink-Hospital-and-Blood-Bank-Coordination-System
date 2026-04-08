/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bloodlink.ui.hospital;

import bloodlink.dao.UserDAO;
import bloodlink.model.HospitalNurse;
import bloodlink.model.User;
import bloodlink.model.enums.UserRole;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 *
 * @author demi
 * 
 * CRUD panel for the Hospital Admin to manage nurse accounts.
 * Implements the AccountManager interface requirement:
 *   create, update, deactivate, and list nurse accounts.
 * 
 * Layout mirrors NursePatientPanel: table on top, form below.
 */

public class AdminManageNursesPanel extends JPanel {

    private UserDAO userDAO;
    private JTable table;
    private DefaultTableModel tableModel;

    // Form fields
    private JTextField usernameField, passwordField, nameField, emailField, deptField;
    private JButton addBtn, updateBtn, deactivateBtn, clearBtn;
    private int selectedUserId = -1;

    public AdminManageNursesPanel() {
        this.userDAO = new UserDAO();
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        initComponents();
        loadNurses();
    }

    private void initComponents() {
        // ── Title ────────────────────────────────
        JLabel title = new JLabel("Manage Nurse Accounts");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        // ── Table ────────────────────────────────
        String[] columns = {"ID", "Username", "Full Name", "Email", "Department", "Active"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(25);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) populateForm();
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(0, 220));
        add(scrollPane, BorderLayout.CENTER);

        // ── Form ─────────────────────────────────
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createTitledBorder("Nurse Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Username:"), gbc);
        usernameField = new JTextField(15);
        gbc.gridx = 1;
        formPanel.add(usernameField, gbc);

        gbc.gridx = 2; gbc.gridy = 0;
        formPanel.add(new JLabel("Password:"), gbc);
        passwordField = new JTextField(15);
        gbc.gridx = 3;
        formPanel.add(passwordField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Full Name:"), gbc);
        nameField = new JTextField(15);
        gbc.gridx = 1;
        formPanel.add(nameField, gbc);

        gbc.gridx = 2; gbc.gridy = 1;
        formPanel.add(new JLabel("Email:"), gbc);
        emailField = new JTextField(15);
        gbc.gridx = 3;
        formPanel.add(emailField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Department:"), gbc);
        deptField = new JTextField(15);
        gbc.gridx = 1;
        formPanel.add(deptField, gbc);

        // Buttons
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 4;
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        btnPanel.setBackground(Color.WHITE);
        addBtn = createStyledButton("Add Nurse", new Color(46, 125, 50));
        updateBtn = createStyledButton("Update", new Color(21, 101, 192));
        deactivateBtn = createStyledButton("Deactivate", new Color(198, 40, 40));
        clearBtn = createStyledButton("Clear", new Color(120, 120, 120));
        btnPanel.add(addBtn);
        btnPanel.add(updateBtn);
        btnPanel.add(deactivateBtn);
        btnPanel.add(clearBtn);
        formPanel.add(btnPanel, gbc);

        add(formPanel, BorderLayout.SOUTH);

        // ── Actions ──────────────────────────────
        addBtn.addActionListener(e -> addNurse());
        updateBtn.addActionListener(e -> updateNurse());
        deactivateBtn.addActionListener(e -> deactivateNurse());
        clearBtn.addActionListener(e -> clearForm());
    }

    private void loadNurses() {
        tableModel.setRowCount(0);
        List<User> nurses = userDAO.getUsersByRole(UserRole.HOSPITAL_NURSE);
        for (User u : nurses) {
            HospitalNurse n = (HospitalNurse) u;
            tableModel.addRow(new Object[]{
                n.getId(), n.getUsername(), n.getFullName(), n.getEmail(),
                n.getDepartment(), n.isActive() ? "Yes" : "No"
            });
        }
    }

    private void populateForm() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        selectedUserId = (int) tableModel.getValueAt(row, 0);
        usernameField.setText((String) tableModel.getValueAt(row, 1));
        passwordField.setText(""); // Don't show existing password
        nameField.setText((String) tableModel.getValueAt(row, 2));
        emailField.setText((String) tableModel.getValueAt(row, 3));
        deptField.setText((String) tableModel.getValueAt(row, 4));
    }

    private String validateForm() {
        if (usernameField.getText().trim().isEmpty()) return "Username is required.";
        if (nameField.getText().trim().isEmpty()) return "Full Name is required.";
        if (emailField.getText().trim().isEmpty()) return "Email is required.";
        return null;
    }

    private void addNurse() {
        String error = validateForm();
        if (error != null) { showWarning(error); return; }
        if (passwordField.getText().trim().isEmpty()) { showWarning("Password is required for new accounts."); return; }

        HospitalNurse nurse = new HospitalNurse();
        nurse.setUsername(usernameField.getText().trim());
        nurse.setPassword(passwordField.getText().trim());
        nurse.setFullName(nameField.getText().trim());
        nurse.setEmail(emailField.getText().trim());
        nurse.setDepartment(deptField.getText().trim());
        nurse.setActive(true);

        if (userDAO.createUser(nurse)) {
            JOptionPane.showMessageDialog(this, "Nurse account created.");
            clearForm();
            loadNurses();
        } else {
            showError("Failed to create nurse. Username may already exist.");
        }
    }

    private void updateNurse() {
        if (selectedUserId == -1) { showWarning("Select a nurse first."); return; }
        String error = validateForm();
        if (error != null) { showWarning(error); return; }

        // Fetch existing user to preserve password if not changed
        User existing = userDAO.getUserById(selectedUserId);
        HospitalNurse nurse = new HospitalNurse();
        nurse.setId(selectedUserId);
        nurse.setUsername(usernameField.getText().trim());
        nurse.setPassword(passwordField.getText().trim().isEmpty()
                ? existing.getPassword() : passwordField.getText().trim());
        nurse.setFullName(nameField.getText().trim());
        nurse.setEmail(emailField.getText().trim());
        nurse.setDepartment(deptField.getText().trim());
        nurse.setActive(existing.isActive());

        if (userDAO.updateUser(nurse)) {
            JOptionPane.showMessageDialog(this, "Nurse updated.");
            clearForm();
            loadNurses();
        } else {
            showError("Failed to update nurse.");
        }
    }

    private void deactivateNurse() {
        if (selectedUserId == -1) { showWarning("Select a nurse first."); return; }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Deactivate this nurse account? They will no longer be able to log in.",
                "Confirm Deactivation", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (userDAO.deactivateUser(selectedUserId)) {
                JOptionPane.showMessageDialog(this, "Nurse deactivated.");
                clearForm();
                loadNurses();
            } else {
                showError("Failed to deactivate nurse.");
            }
        }
    }

    private void clearForm() {
        selectedUserId = -1;
        usernameField.setText("");
        passwordField.setText("");
        nameField.setText("");
        emailField.setText("");
        deptField.setText("");
        table.clearSelection();
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void showWarning(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Warning", JOptionPane.WARNING_MESSAGE);
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}