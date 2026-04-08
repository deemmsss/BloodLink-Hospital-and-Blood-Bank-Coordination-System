/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bloodlink.ui.hospital;

import bloodlink.dao.BloodRequestDAO;
import bloodlink.model.BloodRequest;
import bloodlink.model.enums.RequestPriority;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 *
 * @author demi
 * 
 * Panel for the Hospital Admin to view all blood requests and
 * escalate/prioritize them (Work Request #3 — internal).
 * 
 * The admin selects a request, picks a new priority from the dropdown,
 * and clicks "Escalate". This is critical during emergencies when
 * a routine request needs to jump the queue.
 */
public class AdminEscalatePanel extends JPanel {

    private BloodRequestDAO requestDAO;
    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<String> priorityCombo;
    private JButton escalateBtn, refreshBtn;
    private int selectedRequestId = -1;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public AdminEscalatePanel() {
        this.requestDAO = new BloodRequestDAO();
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        initComponents();
        loadRequests();
    }

    private void initComponents() {
        // ── Header ───────────────────────────────
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        JLabel title = new JLabel("All Blood Requests — Escalate Priority");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        headerPanel.add(title, BorderLayout.WEST);
        refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadRequests());
        headerPanel.add(refreshBtn, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // ── Table ────────────────────────────────
        String[] columns = {"ID", "Patient", "Nurse", "Blood Type", "Units", "Priority", "Status", "Date"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(28);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));

        // Color-code priority
        table.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    String p = value != null ? value.toString() : "";
                    switch (p) {
                        case "Emergency": c.setForeground(Color.RED); c.setFont(c.getFont().deriveFont(Font.BOLD)); break;
                        case "Urgent":    c.setForeground(new Color(200, 150, 0)); c.setFont(c.getFont().deriveFont(Font.BOLD)); break;
                        default:          c.setForeground(Color.BLACK); break;
                    }
                }
                setHorizontalAlignment(SwingConstants.CENTER);
                return c;
            }
        });

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    selectedRequestId = (int) tableModel.getValueAt(row, 0);
                }
            }
        });

        add(new JScrollPane(table), BorderLayout.CENTER);

        // ── Escalation controls ──────────────────
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        controlPanel.setBackground(Color.WHITE);
        controlPanel.setBorder(BorderFactory.createTitledBorder("Change Priority"));

        controlPanel.add(new JLabel("New Priority:"));
        String[] priorities = new String[RequestPriority.values().length];
        for (int i = 0; i < RequestPriority.values().length; i++) {
            priorities[i] = RequestPriority.values()[i].getDisplayName();
        }
        priorityCombo = new JComboBox<>(priorities);
        controlPanel.add(priorityCombo);

        escalateBtn = new JButton("Escalate Selected Request");
        escalateBtn.setBackground(new Color(200, 100, 0));
        escalateBtn.setForeground(Color.WHITE);
        escalateBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        escalateBtn.setFocusPainted(false);
        escalateBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        escalateBtn.addActionListener(e -> escalateRequest());
        controlPanel.add(escalateBtn);

        add(controlPanel, BorderLayout.SOUTH);
    }

    public void loadRequests() {
        tableModel.setRowCount(0);
        selectedRequestId = -1;
        List<BloodRequest> requests = requestDAO.getAllRequests();
        for (BloodRequest r : requests) {
            tableModel.addRow(new Object[]{
                r.getId(),
                r.getPatientName(),
                r.getNurseName(),
                r.getBloodType().getDisplayName(),
                r.getUnitsRequested(),
                r.getPriority().getDisplayName(),
                r.getStatus().getDisplayName(),
                DATE_FORMAT.format(r.getRequestDate())
            });
        }
    }

    private void escalateRequest() {
        if (selectedRequestId == -1) {
            JOptionPane.showMessageDialog(this, "Select a request first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        RequestPriority newPriority = RequestPriority.values()[priorityCombo.getSelectedIndex()];

        int confirm = JOptionPane.showConfirmDialog(this,
                "Change Request #" + selectedRequestId + " priority to " + newPriority.getDisplayName() + "?",
                "Confirm Escalation", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (requestDAO.updatePriority(selectedRequestId, newPriority)) {
                JOptionPane.showMessageDialog(this,
                        "Request #" + selectedRequestId + " escalated to " + newPriority.getDisplayName() + ".");
                loadRequests();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to escalate.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
