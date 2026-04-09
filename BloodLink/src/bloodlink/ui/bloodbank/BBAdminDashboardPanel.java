/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bloodlink.ui.bloodbank;

import bloodlink.dao.*;
import bloodlink.model.BloodRequest;
import bloodlink.model.InventoryThreshold;
import bloodlink.model.User;
import bloodlink.model.enums.BloodType;
import bloodlink.model.enums.UserRole;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

/**
 *
 * @author demi
 * 
 * Dashboard home panel for the Blood Bank Admin.
 * Shows: technician count, donor count, total inventory, low-stock alerts.
 */
public class BBAdminDashboardPanel extends JPanel {

    private User currentUser;
    private UserDAO userDAO;
    private DonorDAO donorDAO;
    private BloodUnitDAO unitDAO;
    private BloodRequestDAO requestDAO;
    private InventoryThresholdDAO thresholdDAO;

    private JLabel techCountLabel, donorCountLabel, inventoryLabel, pendingLabel;
    private JLabel alertLabel;

    public BBAdminDashboardPanel(User currentUser) {
        this.currentUser = currentUser;
        this.userDAO = new UserDAO();
        this.donorDAO = new DonorDAO();
        this.unitDAO = new BloodUnitDAO();
        this.requestDAO = new BloodRequestDAO();
        this.thresholdDAO = new InventoryThresholdDAO();
        setLayout(new BorderLayout(15, 15));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        initComponents();
        refreshData();
    }

    private void initComponents() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        JLabel welcome = new JLabel("Welcome, " + currentUser.getFullName());
        welcome.setFont(new Font("SansSerif", Font.BOLD, 22));
        headerPanel.add(welcome, BorderLayout.WEST);
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> refreshData());
        headerPanel.add(refreshBtn, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        JPanel cardsPanel = new JPanel(new GridLayout(1, 4, 15, 15));
        cardsPanel.setBackground(Color.WHITE);

        techCountLabel = new JLabel("0", SwingConstants.CENTER);
        cardsPanel.add(createCard("Active Technicians", techCountLabel, new Color(21, 101, 192)));

        donorCountLabel = new JLabel("0", SwingConstants.CENTER);
        cardsPanel.add(createCard("Registered Donors", donorCountLabel, new Color(120, 60, 180)));

        inventoryLabel = new JLabel("0", SwingConstants.CENTER);
        cardsPanel.add(createCard("Available Units", inventoryLabel, new Color(46, 125, 50)));

        pendingLabel = new JLabel("0", SwingConstants.CENTER);
        cardsPanel.add(createCard("Pending Requests", pendingLabel, new Color(200, 150, 0)));

        add(cardsPanel, BorderLayout.CENTER);

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
        List<User> techs = userDAO.getUsersByRole(UserRole.BLOOD_BANK_TECHNICIAN);
        long activeTechs = techs.stream().filter(User::isActive).count();
        techCountLabel.setText(String.valueOf(activeTechs));

        donorCountLabel.setText(String.valueOf(donorDAO.getAllDonors().size()));

        Map<BloodType, Integer> counts = unitDAO.getAvailableCounts();
        int totalAvailable = counts.values().stream().mapToInt(Integer::intValue).sum();
        inventoryLabel.setText(String.valueOf(totalAvailable));

        List<BloodRequest> allRequests = requestDAO.getAllRequests();
        long pending = allRequests.stream()
                .filter(r -> r.getStatus() == bloodlink.model.enums.RequestStatus.PENDING)
                .count();
        pendingLabel.setText(String.valueOf(pending));

        // Low stock check
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
            alertLabel.setText("LOW STOCK: " + lowStock.toString() + " — Add donors/units or adjust thresholds.");
            alertLabel.setForeground(Color.RED);
            alertLabel.setBackground(new Color(255, 240, 240));
        } else {
            alertLabel.setText("Inventory levels healthy. All systems operational.");
            alertLabel.setForeground(new Color(46, 125, 50));
            alertLabel.setBackground(new Color(240, 255, 240));
        }
    }
}
