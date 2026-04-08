/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bloodlink.ui.hospital;

import bloodlink.dao.BloodRequestDAO;
import bloodlink.dao.ShipmentDAO;
import bloodlink.model.BloodRequest;
import bloodlink.model.Shipment;
import bloodlink.model.User;
import bloodlink.model.enums.RequestStatus;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 *
 * @author demi
 * 
 * Panel for the Hospital Nurse to confirm receipt of shipped blood units.
 * This completes the cross-enterprise round-trip:
 *   Nurse creates request → Blood Bank ships → Nurse confirms receipt
 * 
 * Shows only requests with status = SHIPPED for this nurse.
 * Confirming receipt sets the shipment as confirmed and the request status to RECEIVED.
 */
public class NurseConfirmReceiptPanel extends JPanel {

    private User currentUser;
    private BloodRequestDAO requestDAO;
    private ShipmentDAO shipmentDAO;
    private JTable table;
    private DefaultTableModel tableModel;
    private JButton confirmBtn, refreshBtn;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public NurseConfirmReceiptPanel(User currentUser) {
        this.currentUser = currentUser;
        this.requestDAO = new BloodRequestDAO();
        this.shipmentDAO = new ShipmentDAO();
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        initComponents();
        loadShippedRequests();
    }

    private void initComponents() {
        // ── Header ───────────────────────────────
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);

        JLabel title = new JLabel("Confirm Receipt of Blood Shipments");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        headerPanel.add(title, BorderLayout.WEST);

        refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadShippedRequests());
        headerPanel.add(refreshBtn, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // ── Table ────────────────────────────────
        String[] columns = {"Request ID", "Patient", "Blood Type", "Units", "Priority", "Shipped Date"};
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
        add(new JScrollPane(table), BorderLayout.CENTER);

        // ── Confirm button ───────────────────────
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.setBackground(Color.WHITE);
        confirmBtn = new JButton("Confirm Receipt of Selected Shipment");
        confirmBtn.setBackground(new Color(46, 125, 50));
        confirmBtn.setForeground(Color.WHITE);
        confirmBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        confirmBtn.setFocusPainted(false);
        confirmBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        confirmBtn.addActionListener(e -> confirmReceipt());
        btnPanel.add(confirmBtn);
        add(btnPanel, BorderLayout.SOUTH);
    }

    /**
     * Loads all SHIPPED requests for this nurse.
     */
    public void loadShippedRequests() {
        tableModel.setRowCount(0);
        List<BloodRequest> requests = requestDAO.getShippedRequestsForNurse(currentUser.getId());
        for (BloodRequest r : requests) {
            tableModel.addRow(new Object[]{
                r.getId(),
                r.getPatientName(),
                r.getBloodType().getDisplayName(),
                r.getUnitsRequested(),
                r.getPriority().getDisplayName(),
                DATE_FORMAT.format(r.getRequestDate())
            });
        }
        if (requests.isEmpty()) {
            confirmBtn.setEnabled(false);
        } else {
            confirmBtn.setEnabled(true);
        }
    }

    /**
     * Confirms receipt of the selected shipment.
     * Updates both the shipment (confirmed = true) and the request (status = RECEIVED).
     */
    private void confirmReceipt() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a shipment to confirm.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int requestId = (int) tableModel.getValueAt(row, 0);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Confirm that you have received the blood units for Request #" + requestId + "?",
                "Confirm Receipt", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            // Find the shipment for this request
            Shipment shipment = shipmentDAO.getShipmentByRequestId(requestId);
            if (shipment != null) {
                // Mark shipment as confirmed
                shipmentDAO.confirmReceipt(shipment.getId());
                // Update request status to RECEIVED
                requestDAO.updateStatus(requestId, RequestStatus.RECEIVED);
                JOptionPane.showMessageDialog(this, "Receipt confirmed! Request #" + requestId + " is now marked as RECEIVED.");
                loadShippedRequests();
            } else {
                JOptionPane.showMessageDialog(this, "No shipment found for this request.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
