/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bloodlink.ui.bloodbank;

import bloodlink.dao.BloodRequestDAO;
import bloodlink.model.BloodRequest;
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
 * Read-only overview of all blood requests for the Blood Bank Admin.
 * Allows the admin to monitor the full request pipeline across both enterprises.
 */
public class BBAdminRequestOverviewPanel extends JPanel {

    private BloodRequestDAO requestDAO;
    private JTable table;
    private DefaultTableModel tableModel;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public BBAdminRequestOverviewPanel() {
        this.requestDAO = new BloodRequestDAO();
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        initComponents();
        loadRequests();
    }

    private void initComponents() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        JLabel title = new JLabel("All Blood Requests — Overview");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        headerPanel.add(title, BorderLayout.WEST);
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadRequests());
        headerPanel.add(refreshBtn, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        String[] cols = {"ID", "Patient", "Nurse", "Blood Type", "Units", "Priority", "Status", "Date"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(28);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));

        // Color-code status
        table.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    String s = value != null ? value.toString() : "";
                    switch (s) {
                        case "Pending":    c.setForeground(new Color(200, 150, 0)); break;
                        case "Approved":   c.setForeground(new Color(21, 101, 192)); break;
                        case "Rejected":   c.setForeground(Color.RED); break;
                        case "Shipped":    c.setForeground(new Color(120, 60, 180)); break;
                        case "Received":   c.setForeground(new Color(46, 125, 50)); break;
                        default:           c.setForeground(Color.BLACK);
                    }
                }
                setHorizontalAlignment(SwingConstants.CENTER);
                return c;
            }
        });

        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    public void loadRequests() {
        tableModel.setRowCount(0);
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
}
