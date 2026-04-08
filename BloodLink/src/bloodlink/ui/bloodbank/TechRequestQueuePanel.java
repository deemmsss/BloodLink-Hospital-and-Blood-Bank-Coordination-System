/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bloodlink.ui.bloodbank;

import bloodlink.dao.BloodRequestDAO;
import bloodlink.model.BloodRequest;
import bloodlink.model.User;
import bloodlink.model.enums.RequestStatus;
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
 * Panel for the Blood Bank Technician to view incoming PENDING requests
 * and approve or reject them.
 * 
 * This is the Blood Bank's side of Work Request #1:
 * the nurse created the request, and now the technician processes it.
 * 
 * Layout:
 * ┌─────────────────────────────────────────────────────┐
 * │  Title + Refresh                                    │
 * ├─────────────────────────────────────────────────────┤
 * │  JTable showing PENDING requests                    │
 * │  (sorted: EMERGENCY → URGENT → ROUTINE)             │
 * ├─────────────────────────────────────────────────────┤
 * │  [Approve Selected]  [Reject Selected]              │
 * └─────────────────────────────────────────────────────┘
 */
public class TechRequestQueuePanel extends JPanel {

    private User currentUser;
    private BloodRequestDAO requestDAO;
    private JTable table;
    private DefaultTableModel tableModel;
    private JButton approveBtn, rejectBtn, refreshBtn;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public TechRequestQueuePanel(User currentUser) {
        this.currentUser = currentUser;
        this.requestDAO = new BloodRequestDAO();
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        initComponents();
        loadPendingRequests();
    }

    private void initComponents() {
        // ── Header ───────────────────────────────
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);

        JLabel title = new JLabel("Incoming Request Queue");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        headerPanel.add(title, BorderLayout.WEST);

        refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadPendingRequests());
        headerPanel.add(refreshBtn, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // ── Table ────────────────────────────────
        String[] columns = {"Request ID", "Patient", "Nurse", "Blood Type", "Units", "Priority", "Date", "Notes"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setRowHeight(28);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));

        // Color-code the Priority column
        table.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    String priority = value != null ? value.toString() : "";
                    switch (priority) {
                        case "Emergency": c.setForeground(Color.RED); c.setFont(c.getFont().deriveFont(Font.BOLD)); break;
                        case "Urgent":    c.setForeground(new Color(200, 150, 0)); c.setFont(c.getFont().deriveFont(Font.BOLD)); break;
                        case "Routine":   c.setForeground(new Color(70, 70, 70)); break;
                        default:          c.setForeground(Color.BLACK);
                    }
                }
                setHorizontalAlignment(SwingConstants.CENTER);
                return c;
            }
        });

        add(new JScrollPane(table), BorderLayout.CENTER);

        // ── Buttons ──────────────────────────────
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        btnPanel.setBackground(Color.WHITE);

        approveBtn = new JButton("Approve Selected");
        approveBtn.setBackground(new Color(46, 125, 50));
        approveBtn.setForeground(Color.WHITE);
        approveBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        approveBtn.setFocusPainted(false);
        approveBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        approveBtn.addActionListener(e -> processRequest(true));

        rejectBtn = new JButton("Reject Selected");
        rejectBtn.setBackground(new Color(198, 40, 40));
        rejectBtn.setForeground(Color.WHITE);
        rejectBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        rejectBtn.setFocusPainted(false);
        rejectBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        rejectBtn.addActionListener(e -> processRequest(false));

        btnPanel.add(approveBtn);
        btnPanel.add(rejectBtn);
        add(btnPanel, BorderLayout.SOUTH);
    }

    /**
     * Loads all PENDING requests into the table.
     */
    public void loadPendingRequests() {
        tableModel.setRowCount(0);
        List<BloodRequest> requests = requestDAO.getRequestsByStatus(RequestStatus.PENDING);
        for (BloodRequest r : requests) {
            tableModel.addRow(new Object[]{
                r.getId(),
                r.getPatientName(),
                r.getNurseName(),
                r.getBloodType().getDisplayName(),
                r.getUnitsRequested(),
                r.getPriority().getDisplayName(),
                DATE_FORMAT.format(r.getRequestDate()),
                r.getNotes()
            });
        }
    }

    /**
     * Approves or rejects the selected request.
     */
    private void processRequest(boolean approve) {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a request first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int requestId = (int) tableModel.getValueAt(row, 0);
        String action = approve ? "approve" : "reject";

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to " + action + " Request #" + requestId + "?",
                "Confirm " + (approve ? "Approval" : "Rejection"),
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            RequestStatus newStatus = approve ? RequestStatus.APPROVED : RequestStatus.REJECTED;
            if (requestDAO.updateStatus(requestId, newStatus)) {
                JOptionPane.showMessageDialog(this,
                        "Request #" + requestId + " has been " + (approve ? "APPROVED" : "REJECTED") + ".");
                loadPendingRequests();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update request.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
