/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bloodlink.ui.bloodbank;

import bloodlink.dao.InventoryThresholdDAO;
import bloodlink.model.InventoryThreshold;
import bloodlink.model.enums.BloodType;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 *
 * @author demi
 * 
 * Panel for the Blood Bank Admin to view and edit minimum inventory thresholds.
 * Each blood type has a configurable minimum — if stock drops below this,
 * the inventory panel shows a "LOW STOCK" warning.
 * 
 * The admin clicks a row, types a new minimum in the spinner, and clicks Update.
 */
public class BBAdminThresholdsPanel extends JPanel {

    private InventoryThresholdDAO thresholdDAO;
    private JTable table;
    private DefaultTableModel tableModel;
    private JSpinner minSpinner;
    private JButton updateBtn;
    private BloodType selectedBloodType = null;

    public BBAdminThresholdsPanel() {
        this.thresholdDAO = new InventoryThresholdDAO();
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        initComponents();
        loadThresholds();
    }

    private void initComponents() {
        // ── Title ────────────────────────────────
        JLabel title = new JLabel("Inventory Thresholds");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        // ── Table ────────────────────────────────
        String[] cols = {"Blood Type", "Current Minimum Units"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(30);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    selectedBloodType = BloodType.fromDisplayName((String) tableModel.getValueAt(row, 0));
                    minSpinner.setValue(tableModel.getValueAt(row, 1));
                }
            }
        });

        add(new JScrollPane(table), BorderLayout.CENTER);

        // ── Edit controls ────────────────────────
        JPanel editPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        editPanel.setBackground(Color.WHITE);
        editPanel.setBorder(BorderFactory.createTitledBorder("Edit Threshold"));

        editPanel.add(new JLabel("New Minimum:"));
        minSpinner = new JSpinner(new SpinnerNumberModel(5, 0, 100, 1));
        minSpinner.setFont(new Font("SansSerif", Font.PLAIN, 14));
        editPanel.add(minSpinner);

        updateBtn = new JButton("Update Threshold");
        updateBtn.setBackground(new Color(21, 101, 192));
        updateBtn.setForeground(Color.WHITE);
        updateBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        updateBtn.setFocusPainted(false);
        updateBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        updateBtn.addActionListener(e -> updateThreshold());
        editPanel.add(updateBtn);

        add(editPanel, BorderLayout.SOUTH);
    }

    private void loadThresholds() {
        tableModel.setRowCount(0);
        List<InventoryThreshold> thresholds = thresholdDAO.getAllThresholds();
        for (InventoryThreshold t : thresholds) {
            tableModel.addRow(new Object[]{
                t.getBloodType().getDisplayName(),
                t.getMinimumUnits()
            });
        }
    }

    private void updateThreshold() {
        if (selectedBloodType == null) {
            JOptionPane.showMessageDialog(this, "Select a blood type first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int newMin = (int) minSpinner.getValue();
        if (thresholdDAO.updateThreshold(selectedBloodType, newMin)) {
            JOptionPane.showMessageDialog(this,
                    selectedBloodType.getDisplayName() + " threshold updated to " + newMin + " units.");
            loadThresholds();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}