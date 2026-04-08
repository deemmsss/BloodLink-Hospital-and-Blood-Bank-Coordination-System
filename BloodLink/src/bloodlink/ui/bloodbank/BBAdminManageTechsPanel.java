/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bloodlink.ui.bloodbank;

import bloodlink.dao.UserDAO;
import bloodlink.model.BloodBankTechnician;
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
 * CRUD panel for the Blood Bank Admin to manage technician accounts.
 * Mirrors AdminManageNursesPanel but creates BloodBankTechnician objects
 * with a lab_certification field instead of department.
 */

public class BBAdminManageTechsPanel extends JPanel {

    private UserDAO userDAO;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField usernameField, passwordField, nameField, emailField, certField;
    private JButton addBtn, updateBtn, deactivateBtn, clearBtn;
    private int selectedUserId = -1;

    public BBAdminManageTechsPanel() {
        this.userDAO = new UserDAO();
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        initComponents();
        loadTechs();
    }

    private void initComponents() {
        JLabel title = new JLabel("Manage Technician Accounts");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        // ── Table ────────────────────────────────
        String[] columns = {"ID", "Username", "Full Name", "Email", "Lab Certification", "Active"};
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
        formPanel.setBorder(BorderFactory.createTitledBorder("Technician Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Username:"), gbc);
        usernameField = new JTextField(15);
        gbc.gridx = 1; formPanel.add(usernameField, gbc);

        gbc.gridx = 2;
        formPanel.add(new JLabel("Password:"), gbc);
        passwordField = new JTextField(15);
        gbc.gridx = 3; formPanel.add(passwordField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Full Name:"), gbc);
        nameField = new JTextField(15);
        gbc.gridx = 1; formPanel.add(nameField, gbc);

        gbc.gridx = 2;
        formPanel.add(new JLabel("Email:"), gbc);
        emailField = new JTextField(15);
        gbc.gridx = 3; formPanel.add(emailField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Lab Certification:"), gbc);
        certField = new JTextField(15);
        gbc.gridx = 1; formPanel.add(certField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 4;
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        btnPanel.setBackground(Color.WHITE);
        addBtn = styled("Add Technician", new Color(46, 125, 50));
        updateBtn = styled("Update", new Color(21, 101, 192));
        deactivateBtn = styled("Deactivate", new Color(198, 40, 40));
        clearBtn = styled("Clear", new Color(120, 120, 120));
        btnPanel.add(addBtn); btnPanel.add(updateBtn);
        btnPanel.add(deactivateBtn); btnPanel.add(clearBtn);
        formPanel.add(btnPanel, gbc);

        add(formPanel, BorderLayout.SOUTH);

        addBtn.addActionListener(e -> addTech());
        updateBtn.addActionListener(e -> updateTech());
        deactivateBtn.addActionListener(e -> deactivateTech());
        clearBtn.addActionListener(e -> clearForm());
    }

    private void loadTechs() {
        tableModel.setRowCount(0);
        List<User> techs = userDAO.getUsersByRole(UserRole.BLOOD_BANK_TECHNICIAN);
        for (User u : techs) {
            BloodBankTechnician t = (BloodBankTechnician) u;
            tableModel.addRow(new Object[]{
                t.getId(), t.getUsername(), t.getFullName(), t.getEmail(),
                t.getLabCertification(), t.isActive() ? "Yes" : "No"
            });
        }
    }

    private void populateForm() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        selectedUserId = (int) tableModel.getValueAt(row, 0);
        usernameField.setText((String) tableModel.getValueAt(row, 1));
        passwordField.setText("");
        nameField.setText((String) tableModel.getValueAt(row, 2));
        emailField.setText((String) tableModel.getValueAt(row, 3));
        certField.setText((String) tableModel.getValueAt(row, 4));
    }

    private String validateForm() {
        if (usernameField.getText().trim().isEmpty()) return "Username is required.";
        if (nameField.getText().trim().isEmpty()) return "Full Name is required.";
        if (emailField.getText().trim().isEmpty()) return "Email is required.";
        return null;
    }

    private void addTech() {
        String err = validateForm();
        if (err != null) { warn(err); return; }
        if (passwordField.getText().trim().isEmpty()) { warn("Password is required."); return; }

        BloodBankTechnician tech = new BloodBankTechnician();
        tech.setUsername(usernameField.getText().trim());
        tech.setPassword(passwordField.getText().trim());
        tech.setFullName(nameField.getText().trim());
        tech.setEmail(emailField.getText().trim());
        tech.setLabCertification(certField.getText().trim());
        tech.setActive(true);

        if (userDAO.createUser(tech)) {
            JOptionPane.showMessageDialog(this, "Technician account created.");
            clearForm(); loadTechs();
        } else { error("Failed. Username may already exist."); }
    }

    private void updateTech() {
        if (selectedUserId == -1) { warn("Select a technician first."); return; }
        String err = validateForm();
        if (err != null) { warn(err); return; }

        User existing = userDAO.getUserById(selectedUserId);
        BloodBankTechnician tech = new BloodBankTechnician();
        tech.setId(selectedUserId);
        tech.setUsername(usernameField.getText().trim());
        tech.setPassword(passwordField.getText().trim().isEmpty()
                ? existing.getPassword() : passwordField.getText().trim());
        tech.setFullName(nameField.getText().trim());
        tech.setEmail(emailField.getText().trim());
        tech.setLabCertification(certField.getText().trim());
        tech.setActive(existing.isActive());

        if (userDAO.updateUser(tech)) {
            JOptionPane.showMessageDialog(this, "Technician updated.");
            clearForm(); loadTechs();
        } else { error("Failed to update."); }
    }

    private void deactivateTech() {
        if (selectedUserId == -1) { warn("Select a technician first."); return; }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Deactivate this technician?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (userDAO.deactivateUser(selectedUserId)) {
                JOptionPane.showMessageDialog(this, "Technician deactivated.");
                clearForm(); loadTechs();
            } else { error("Failed to deactivate."); }
        }
    }

    private void clearForm() {
        selectedUserId = -1;
        usernameField.setText(""); passwordField.setText("");
        nameField.setText(""); emailField.setText(""); certField.setText("");
        table.clearSelection();
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