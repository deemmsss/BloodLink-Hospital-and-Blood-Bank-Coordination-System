/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bloodlink.ui.hospital;

import bloodlink.dao.BloodRequestDAO;
import bloodlink.model.BloodRequest;
import bloodlink.model.User;
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
 * Read-only panel showing all blood requests created by the current nurse.
 * Displays current status so the nurse can track progress through the pipeline:
 * PENDING → APPROVED → FULFILLED → SHIPPED → RECEIVED
 * 
 * Includes a Refresh button to poll for status changes
 * (e.g., after a technician approves a request).
 */
public class NurseMyRequestsPanel extends JPanel {

    private User currentUser;
    private BloodRequestDAO requestDAO;
    private JTable requestTable;
    private DefaultTableModel tableModel;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public NurseMyRequestsPanel(User currentUser) {
        this.currentUser = currentUser;
        this.requestDAO = new BloodRequestDAO();
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        initComponents();
        loadRequests();
    }

    private void initComponents() {
        // ── Header with title + refresh button ───
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);

        JLabel title = new JLabel("My Blood Requests");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        headerPanel.add(title, BorderLayout.WEST);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        refreshBtn.addActionListener(e -> loadRequests());
        headerPanel.add(refreshBtn, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // ── Table ────────────────────────────────
        String[] columns = {"ID", "Patient", "Blood Type", "Units", "Priority", "Status", "Date", "Notes"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        requestTable = new JTable(tableModel);
        requestTable.setRowHeight(28);
        requestTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));

        // Color-code the Status column based on request state
        requestTable.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    String status = value != null ? value.toString() : "";
                    switch (status) {
                        case "Pending":    c.setForeground(new Color(200, 150, 0)); break;
                        case "Approved":   c.setForeground(new Color(21, 101, 192)); break;
                        case "Rejected":   c.setForeground(Color.RED); break;
                        case "Fulfilled":  c.setForeground(new Color(46, 125, 50)); break;
                        case "Shipped":    c.setForeground(new Color(120, 60, 180)); break;
                        case "Received":   c.setForeground(new Color(46, 125, 50)); break;
                        default:           c.setForeground(Color.BLACK);
                    }
                }
                setHorizontalAlignment(SwingConstants.CENTER);
                return c;
            }
        });

        // Center-align Priority column too
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        requestTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);

        add(new JScrollPane(requestTable), BorderLayout.CENTER);
    }

    /**
     * Loads all requests for the current nurse.
     * Public so other panels can trigger a refresh after creating a request.
     */
    public void loadRequests() {
        tableModel.setRowCount(0);
        List<BloodRequest> requests = requestDAO.getRequestsByNurse(currentUser.getId());
        for (BloodRequest r : requests) {
            tableModel.addRow(new Object[]{
                r.getId(),
                r.getPatientName(),
                r.getBloodType().getDisplayName(),
                r.getUnitsRequested(),
                r.getPriority().getDisplayName(),
                r.getStatus().getDisplayName(),
                DATE_FORMAT.format(r.getRequestDate()),
                r.getNotes()
            });
        }
    }
}
