/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bloodlink.ui.bloodbank;

import bloodlink.dao.BloodRequestDAO;
import bloodlink.dao.BloodUnitDAO;
import bloodlink.dao.InventoryThresholdDAO;
import bloodlink.model.BloodRequest;
import bloodlink.model.InventoryThreshold;
import bloodlink.model.User;
import bloodlink.model.enums.BloodType;
import bloodlink.model.enums.RequestStatus;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

/**
 *
 * @author demi
 * 
 * Dashboard home panel for the Blood Bank Technician.
 * Shows: pending request count, approved (ready to ship) count,
 * and low-stock blood type alerts.
 */
public class TechDashboardPanel extends JPanel {

    private User currentUser;
    private BloodRequestDAO requestDAO;
    private BloodUnitDAO unitDAO;
    private InventoryThresholdDAO thresholdDAO;

    private JLabel pendingLabel, approvedLabel, shippedLabel;
    private JLabel alertLabel;

    public TechDashboardPanel(User currentUser) {
        this.currentUser = currentUser;
        this.requestDAO = new BloodRequestDAO();
        this.unitDAO = new BloodUnitDAO();
        this.thresholdDAO = new InventoryThresholdDAO();
        setLayout(new BorderLayout(15, 15));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        initComponents();
        refreshData();
    }

    private void initComponents() {
        // ── Welcome header ───────────────────────
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        JLabel welcome = new JLabel("Welcome, " + currentUser.getFullName());
        welcome.setFont(new Font("SansSerif", Font.BOLD, 22));
        headerPanel.add(welcome, BorderLayout.WEST);
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> refreshData());
        headerPanel.add(refreshBtn, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // ── Metric cards ─────────────────────────
        JPanel cardsPanel = new JPanel(new GridLayout(1, 3, 15, 15));
        cardsPanel.setBackground(Color.WHITE);

        pendingLabel = new JLabel("0", SwingConstants.CENTER);
        cardsPanel.add(createCard("Pending Requests", pendingLabel, new Color(200, 150, 0)));

        approvedLabel = new JLabel("0", SwingConstants.CENTER);
        cardsPanel.add(createCard("Ready to Ship", approvedLabel, new Color(21, 101, 192)));

        shippedLabel = new JLabel("0", SwingConstants.CENTER);
        cardsPanel.add(createCard("Shipped Today", shippedLabel, new Color(46, 125, 50)));

        add(cardsPanel, BorderLayout.CENTER);

        // ── Alert area ───────────────────────────
        alertLabel = new JLabel(" ", SwingConstants.CENTER);
        alertLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
        alertLabel.setOpaque(true);
        alertLabel.setBackground(Color.WHITE);
        alertLabel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        add(alertLabel, BorderLayout.SOUTH);
    }

    private JPanel createCard(String title, JLabel valueLabel, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout(10, 5));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        JPanel accent = new JPanel();
        accent.setPreferredSize(new Dimension(5, 0));
        accent.setBackground(accentColor);
        card.add(accent, BorderLayout.WEST);
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        titleLabel.setForeground(Color.GRAY);
        card.add(titleLabel, BorderLayout.NORTH);
        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 36));
        valueLabel.setForeground(accentColor);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    public void refreshData() {
        List<BloodRequest> allRequests = requestDAO.getAllRequests();
        int pending = 0, approved = 0, shipped = 0;
        for (BloodRequest r : allRequests) {
            switch (r.getStatus()) {
                case PENDING:  pending++;  break;
                case APPROVED: approved++; break;
                case SHIPPED:  shipped++;  break;
                default: break;
            }
        }
        pendingLabel.setText(String.valueOf(pending));
        approvedLabel.setText(String.valueOf(approved));
        shippedLabel.setText(String.valueOf(shipped));

        // Check for low stock
        Map<BloodType, Integer> counts = unitDAO.getAvailableCounts();
        List<InventoryThreshold> thresholds = thresholdDAO.getAllThresholds();
        StringBuilder lowStock = new StringBuilder();
        for (InventoryThreshold t : thresholds) {
            int available = counts.getOrDefault(t.getBloodType(), 0);
            if (t.isLow(available)) {
                if (lowStock.length() > 0) lowStock.append(", ");
                lowStock.append(t.getBloodType().getDisplayName())
                         .append(" (").append(available).append("/").append(t.getMinimumUnits()).append(")");
            }
        }

        if (lowStock.length() > 0) {
            alertLabel.setText("LOW STOCK: " + lowStock.toString());
            alertLabel.setForeground(Color.RED);
            alertLabel.setBackground(new Color(255, 240, 240));
        } else if (pending > 0) {
            alertLabel.setText(pending + " request(s) waiting in queue. Go to \"Request Queue\" to process.");
            alertLabel.setForeground(new Color(200, 150, 0));
            alertLabel.setBackground(new Color(255, 250, 230));
        } else {
            alertLabel.setText("All systems normal — no pending actions.");
            alertLabel.setForeground(new Color(46, 125, 50));
            alertLabel.setBackground(new Color(240, 255, 240));
        }
    }
}