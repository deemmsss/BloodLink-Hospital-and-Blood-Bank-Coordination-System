/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bloodlink.ui.bloodbank;

import bloodlink.dao.BloodUnitDAO;
import bloodlink.dao.InventoryThresholdDAO;
import bloodlink.model.BloodUnit;
import bloodlink.model.InventoryThreshold;
import bloodlink.model.enums.BloodType;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

/**
 *
 * @author demi
 * 
 * Read-only inventory panel showing:
 * 1. A summary table — count of available units per blood type vs threshold
 * 2. A detail table — every blood unit in inventory
 * 
 * Low-stock blood types are highlighted in red.
 * Used by both Technician (read-only) and Blood Bank Admin.
 */
public class TechInventoryPanel extends JPanel {

    private BloodUnitDAO unitDAO;
    private InventoryThresholdDAO thresholdDAO;

    private JTable summaryTable, detailTable;
    private DefaultTableModel summaryModel, detailModel;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public TechInventoryPanel() {
        this.unitDAO = new BloodUnitDAO();
        this.thresholdDAO = new InventoryThresholdDAO();
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
        JLabel title = new JLabel("Blood Inventory");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        headerPanel.add(title, BorderLayout.WEST);
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadData());
        headerPanel.add(refreshBtn, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // ── Split: summary on top, detail on bottom ──
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.35);
        splitPane.setBackground(Color.WHITE);

        // Top: Summary table
        String[] sumCols = {"Blood Type", "Available Units", "Minimum Threshold", "Status"};
        summaryModel = new DefaultTableModel(sumCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        summaryTable = new JTable(summaryModel);
        summaryTable.setRowHeight(28);
        summaryTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));

        // Color the Status column
        summaryTable.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    String status = value != null ? value.toString() : "";
                    if ("LOW STOCK".equals(status)) {
                        c.setForeground(Color.RED);
                        c.setFont(c.getFont().deriveFont(Font.BOLD));
                    } else {
                        c.setForeground(new Color(46, 125, 50));
                        c.setFont(c.getFont().deriveFont(Font.PLAIN));
                    }
                }
                setHorizontalAlignment(SwingConstants.CENTER);
                return c;
            }
        });

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createTitledBorder("Inventory Summary"));
        topPanel.add(new JScrollPane(summaryTable), BorderLayout.CENTER);
        splitPane.setTopComponent(topPanel);

        // Bottom: Detail table
        String[] detCols = {"Unit ID", "Blood Type", "Collection Date", "Expiry Date", "Donor ID", "Available"};
        detailModel = new DefaultTableModel(detCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        detailTable = new JTable(detailModel);
        detailTable.setRowHeight(25);
        detailTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createTitledBorder("All Blood Units"));
        bottomPanel.add(new JScrollPane(detailTable), BorderLayout.CENTER);
        splitPane.setBottomComponent(bottomPanel);

        add(splitPane, BorderLayout.CENTER);
    }

    /**
     * Loads both the summary and detail data.
     */
    public void loadData() {
        // Summary
        summaryModel.setRowCount(0);
        Map<BloodType, Integer> counts = unitDAO.getAvailableCounts();
        List<InventoryThreshold> thresholds = thresholdDAO.getAllThresholds();

        // Build a map for quick lookup
        Map<BloodType, Integer> minMap = new java.util.LinkedHashMap<>();
        for (InventoryThreshold t : thresholds) {
            minMap.put(t.getBloodType(), t.getMinimumUnits());
        }

        for (BloodType bt : BloodType.values()) {
            int available = counts.getOrDefault(bt, 0);
            int minimum = minMap.getOrDefault(bt, 0);
            String status = available < minimum ? "LOW STOCK" : "OK";
            summaryModel.addRow(new Object[]{
                bt.getDisplayName(), available, minimum, status
            });
        }

        // Detail
        detailModel.setRowCount(0);
        List<BloodUnit> units = unitDAO.getAllBloodUnits();
        for (BloodUnit u : units) {
            detailModel.addRow(new Object[]{
                u.getId(),
                u.getBloodType().getDisplayName(),
                DATE_FORMAT.format(u.getCollectionDate()),
                DATE_FORMAT.format(u.getExpiryDate()),
                u.getDonorId(),
                u.isAvailable() ? "Yes" : "No"
            });
        }
    }
}
