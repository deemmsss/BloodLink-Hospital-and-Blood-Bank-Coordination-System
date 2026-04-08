/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bloodlink.ui.bloodbank;

import bloodlink.dao.BloodRequestDAO;
import bloodlink.dao.BloodUnitDAO;
import bloodlink.dao.ShipmentDAO;
import bloodlink.model.BloodRequest;
import bloodlink.model.BloodUnit;
import bloodlink.model.User;
import bloodlink.model.enums.RequestStatus;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author demi
 * 
 * Panel for the Technician to prepare and ship blood units for approved requests.
 * 
 * Layout (vertical split):
 * ┌─────────────────────────────────────────────────────┐
 * │  "Approved Requests" table                          │
 * │  (click a row to load matching blood units below)   │
 * ├─────────────────────────────────────────────────────┤
 * │  "Available Blood Units" table                      │
 * │  (shows units matching the selected request's type) │
 * │  Multi-select rows to pick which units to ship      │
 * ├─────────────────────────────────────────────────────┤
 * │  [Create Shipment & Ship]                           │
 * └─────────────────────────────────────────────────────┘
 */
public class TechPrepareShipmentPanel extends JPanel {

    private User currentUser;
    private BloodRequestDAO requestDAO;
    private BloodUnitDAO unitDAO;
    private ShipmentDAO shipmentDAO;

    // Top table — approved requests
    private JTable requestTable;
    private DefaultTableModel requestTableModel;

    // Bottom table — available blood units
    private JTable unitTable;
    private DefaultTableModel unitTableModel;
    private JLabel unitTableTitle;

    private JButton shipBtn, refreshBtn;

    // Tracks the currently selected request
    private int selectedRequestId = -1;
    private int selectedUnitsRequested = 0;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public TechPrepareShipmentPanel(User currentUser) {
        this.currentUser = currentUser;
        this.requestDAO = new BloodRequestDAO();
        this.unitDAO = new BloodUnitDAO();
        this.shipmentDAO = new ShipmentDAO();
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        initComponents();
        loadApprovedRequests();
    }

    private void initComponents() {
        // ── Header ───────────────────────────────
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        JLabel title = new JLabel("Prepare Shipment");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        headerPanel.add(title, BorderLayout.WEST);
        refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> {
            loadApprovedRequests();
            unitTableModel.setRowCount(0);
            unitTableTitle.setText("Available Blood Units — select a request above");
        });
        headerPanel.add(refreshBtn, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // ── Split pane: requests on top, units on bottom ──
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.45);
        splitPane.setBackground(Color.WHITE);

        // Top: Approved requests table
        String[] reqCols = {"Request ID", "Patient", "Nurse", "Blood Type", "Units Needed", "Priority", "Date"};
        requestTableModel = new DefaultTableModel(reqCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        requestTable = new JTable(requestTableModel);
        requestTable.setRowHeight(26);
        requestTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        requestTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        requestTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                onRequestSelected();
            }
        });

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createTitledBorder("Approved Requests — Ready for Shipment"));
        topPanel.add(new JScrollPane(requestTable), BorderLayout.CENTER);
        splitPane.setTopComponent(topPanel);

        // Bottom: Available blood units table
        String[] unitCols = {"Unit ID", "Blood Type", "Collection Date", "Expiry Date", "Donor ID"};
        unitTableModel = new DefaultTableModel(unitCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        unitTable = new JTable(unitTableModel);
        unitTable.setRowHeight(26);
        // Allow multiple selection so the technician can pick several units
        unitTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        unitTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Color.WHITE);
        unitTableTitle = new JLabel("Available Blood Units — select a request above");
        unitTableTitle.setFont(new Font("SansSerif", Font.BOLD, 13));
        unitTableTitle.setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 0));
        bottomPanel.add(unitTableTitle, BorderLayout.NORTH);
        bottomPanel.add(new JScrollPane(unitTable), BorderLayout.CENTER);
        splitPane.setBottomComponent(bottomPanel);

        add(splitPane, BorderLayout.CENTER);

        // ── Ship button ──────────────────────────
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.setBackground(Color.WHITE);
        shipBtn = new JButton("Create Shipment & Ship Selected Units");
        shipBtn.setBackground(new Color(21, 101, 192));
        shipBtn.setForeground(Color.WHITE);
        shipBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        shipBtn.setFocusPainted(false);
        shipBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        shipBtn.addActionListener(e -> createShipment());
        btnPanel.add(shipBtn);
        add(btnPanel, BorderLayout.SOUTH);
    }

    /**
     * Loads all APPROVED requests into the top table.
     */
    public void loadApprovedRequests() {
        requestTableModel.setRowCount(0);
        selectedRequestId = -1;
        List<BloodRequest> requests = requestDAO.getRequestsByStatus(RequestStatus.APPROVED);
        for (BloodRequest r : requests) {
            requestTableModel.addRow(new Object[]{
                r.getId(),
                r.getPatientName(),
                r.getNurseName(),
                r.getBloodType().getDisplayName(),
                r.getUnitsRequested(),
                r.getPriority().getDisplayName(),
                DATE_FORMAT.format(r.getRequestDate())
            });
        }
    }

    /**
     * When a request row is clicked, load available blood units
     * matching that request's blood type into the bottom table.
     */
    private void onRequestSelected() {
        int row = requestTable.getSelectedRow();
        if (row < 0) return;

        selectedRequestId = (int) requestTableModel.getValueAt(row, 0);
        selectedUnitsRequested = (int) requestTableModel.getValueAt(row, 4);
        String bloodTypeDisplay = (String) requestTableModel.getValueAt(row, 3);

        unitTableTitle.setText("Available Units — " + bloodTypeDisplay
                + " (need " + selectedUnitsRequested + " units, select from below)");

        // Find the BloodType enum from the display name
        bloodlink.model.enums.BloodType bt =
                bloodlink.model.enums.BloodType.fromDisplayName(bloodTypeDisplay);

        // Load matching available units
        unitTableModel.setRowCount(0);
        List<BloodUnit> units = unitDAO.getAvailableByType(bt);
        for (BloodUnit u : units) {
            unitTableModel.addRow(new Object[]{
                u.getId(),
                u.getBloodType().getDisplayName(),
                DATE_FORMAT.format(u.getCollectionDate()),
                DATE_FORMAT.format(u.getExpiryDate()),
                u.getDonorId()
            });
        }

        if (units.isEmpty()) {
            unitTableTitle.setText("Available Units — " + bloodTypeDisplay
                    + " — ⚠ NO UNITS AVAILABLE. Add inventory first.");
        }
    }

    /**
     * Creates a shipment from the selected request and selected blood units.
     * Steps:
     *   1. Create a shipment record
     *   2. Link selected blood units to the shipment (shipment_items)
     *   3. Mark those blood units as unavailable
     *   4. Update request status: APPROVED → FULFILLED → SHIPPED
     */
    private void createShipment() {
        // Read selection directly from the table instead of relying on the stored variable
        int reqRow = requestTable.getSelectedRow();
        if (reqRow < 0) {
            JOptionPane.showMessageDialog(this, "Select a request first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int requestId = (int) requestTableModel.getValueAt(reqRow, 0);
        int unitsNeeded = (int) requestTableModel.getValueAt(reqRow, 4);

        int[] selectedRows = unitTable.getSelectedRows();
        if (selectedRows.length == 0) {
            JOptionPane.showMessageDialog(this, "Select at least one blood unit to ship.", "No Units Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (selectedRows.length < unitsNeeded) {
            int proceed = JOptionPane.showConfirmDialog(this,
                    "The request needs " + unitsNeeded + " units but you selected "
                    + selectedRows.length + ". Ship partial?",
                    "Partial Shipment", JOptionPane.YES_NO_OPTION);
            if (proceed != JOptionPane.YES_OPTION) return;
        }

        // Collect selected unit IDs
        List<Integer> unitIds = new ArrayList<>();
        for (int row : selectedRows) {
            unitIds.add((int) unitTableModel.getValueAt(row, 0));
        }

        // 1. Create shipment
        int shipmentId = shipmentDAO.createShipment(requestId, currentUser.getId());
        if (shipmentId == -1) {
            JOptionPane.showMessageDialog(this, "Failed to create shipment.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 2. Add units to shipment and mark them unavailable
        for (int unitId : unitIds) {
            shipmentDAO.addShipmentItem(shipmentId, unitId);
            unitDAO.markUnavailable(unitId);
        }

        // 3. Update request status to SHIPPED
        requestDAO.updateStatus(requestId, RequestStatus.SHIPPED);

        JOptionPane.showMessageDialog(this,
                "Shipment #" + shipmentId + " created!\n"
                + unitIds.size() + " unit(s) shipped for Request #" + requestId + ".\n"
                + "Request status is now SHIPPED.",
                "Shipment Created", JOptionPane.INFORMATION_MESSAGE);

        // Refresh both tables
        loadApprovedRequests();
        unitTableModel.setRowCount(0);
        unitTableTitle.setText("Available Blood Units — select a request above");
    }
}