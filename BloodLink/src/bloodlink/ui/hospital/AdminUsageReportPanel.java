/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bloodlink.ui.hospital;

import bloodlink.dao.BloodRequestDAO;
import bloodlink.model.BloodRequest;
import bloodlink.model.enums.BloodType;
import bloodlink.model.enums.RequestStatus;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
/**
 *
 * @author demi
 * 
 * Report panel for the Hospital Admin showing hospital-wide blood usage.
 * 
 * Displays:
 * 1. Summary table — total units requested per blood type and their status breakdown
 * 2. Full request log table
 * 
 * This gives the admin oversight of how much blood the hospital is consuming.
 */
public class AdminUsageReportPanel extends JPanel {

    private BloodRequestDAO requestDAO;
    private JTable summaryTable, logTable;
    private DefaultTableModel summaryModel, logModel;

    public AdminUsageReportPanel() {
        this.requestDAO = new BloodRequestDAO();
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        initComponents();
        loadData();
    }

    private void initComponents() {
        // ── Header ───────────────────────────────
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        JLabel title = new JLabel("Hospital-Wide Blood Usage Report");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        headerPanel.add(title, BorderLayout.WEST);
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadData());
        headerPanel.add(refreshBtn, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // ── Split pane ───────────────────────────
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.35);
        splitPane.setBackground(Color.WHITE);

        // Top: Summary by blood type
        String[] sumCols = {"Blood Type", "Total Requests", "Total Units", "Received", "Pending/In Progress", "Rejected"};
        summaryModel = new DefaultTableModel(sumCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        summaryTable = new JTable(summaryModel);
        summaryTable.setRowHeight(26);
        summaryTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));

        // Center align all columns
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < sumCols.length; i++) {
            summaryTable.getColumnModel().getColumn(i).setCellRenderer(center);
        }

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createTitledBorder("Usage by Blood Type"));
        topPanel.add(new JScrollPane(summaryTable), BorderLayout.CENTER);
        splitPane.setTopComponent(topPanel);

        // Bottom: Full request log
        String[] logCols = {"ID", "Patient", "Nurse", "Blood Type", "Units", "Priority", "Status"};
        logModel = new DefaultTableModel(logCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        logTable = new JTable(logModel);
        logTable.setRowHeight(25);
        logTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createTitledBorder("All Request Log"));
        bottomPanel.add(new JScrollPane(logTable), BorderLayout.CENTER);
        splitPane.setBottomComponent(bottomPanel);

        add(splitPane, BorderLayout.CENTER);
    }

    public void loadData() {
        List<BloodRequest> all = requestDAO.getAllRequests();

        // ── Build summary ────────────────────────
        // Map each blood type to counts
        Map<BloodType, int[]> stats = new LinkedHashMap<>();
        for (BloodType bt : BloodType.values()) {
            stats.put(bt, new int[4]); // [totalRequests, totalUnits, received, rejected]
        }

        for (BloodRequest r : all) {
            int[] s = stats.get(r.getBloodType());
            s[0]++; // total requests
            s[1] += r.getUnitsRequested(); // total units
            if (r.getStatus() == RequestStatus.RECEIVED) s[2] += r.getUnitsRequested();
            if (r.getStatus() == RequestStatus.REJECTED) s[3] += r.getUnitsRequested();
        }

        summaryModel.setRowCount(0);
        for (Map.Entry<BloodType, int[]> entry : stats.entrySet()) {
            int[] s = entry.getValue();
            int inProgress = s[1] - s[2] - s[3]; // units neither received nor rejected
            summaryModel.addRow(new Object[]{
                entry.getKey().getDisplayName(),
                s[0], s[1], s[2], Math.max(0, inProgress), s[3]
            });
        }

        // ── Full log ─────────────────────────────
        logModel.setRowCount(0);
        for (BloodRequest r : all) {
            logModel.addRow(new Object[]{
                r.getId(),
                r.getPatientName(),
                r.getNurseName(),
                r.getBloodType().getDisplayName(),
                r.getUnitsRequested(),
                r.getPriority().getDisplayName(),
                r.getStatus().getDisplayName()
            });
        }
    }
}
