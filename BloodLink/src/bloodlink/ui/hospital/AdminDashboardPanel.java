/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bloodlink.ui.hospital;

import bloodlink.dao.BloodRequestDAO;
import bloodlink.dao.UserDAO;
import bloodlink.model.BloodRequest;
import bloodlink.model.User;
import bloodlink.model.enums.RequestPriority;
import bloodlink.model.enums.UserRole;
import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 *
 * @author demi
 * 
 * Dashboard home panel for the Hospital Admin.
 * Shows: active nurse count, total requests, emergency count, and fulfillment rate.
 */
public class AdminDashboardPanel extends JPanel {

    private User currentUser;
    private UserDAO userDAO;
    private BloodRequestDAO requestDAO;

    private JLabel nurseCountLabel, totalRequestsLabel, emergencyLabel, fulfilledLabel;
    private JLabel alertLabel;

    public AdminDashboardPanel(User currentUser) {
        this.currentUser = currentUser;
        this.userDAO = new UserDAO();
        this.requestDAO = new BloodRequestDAO();
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

        nurseCountLabel = new JLabel("0", SwingConstants.CENTER);
        cardsPanel.add(createCard("Active Nurses", nurseCountLabel, new Color(21, 101, 192)));

        totalRequestsLabel = new JLabel("0", SwingConstants.CENTER);
        cardsPanel.add(createCard("Total Requests", totalRequestsLabel, new Color(100, 100, 100)));

        emergencyLabel = new JLabel("0", SwingConstants.CENTER);
        cardsPanel.add(createCard("Emergency Active", emergencyLabel, new Color(198, 40, 40)));

        fulfilledLabel = new JLabel("0%", SwingConstants.CENTER);
        cardsPanel.add(createCard("Fulfillment Rate", fulfilledLabel, new Color(46, 125, 50)));

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
        // Count active nurses
        List<User> nurses = userDAO.getUsersByRole(UserRole.HOSPITAL_NURSE);
        long activeNurses = nurses.stream().filter(User::isActive).count();
        nurseCountLabel.setText(String.valueOf(activeNurses));

        // Request stats
        List<BloodRequest> allRequests = requestDAO.getAllRequests();
        totalRequestsLabel.setText(String.valueOf(allRequests.size()));

        int emergencyActive = 0, received = 0;
        for (BloodRequest r : allRequests) {
            if (r.getPriority() == RequestPriority.EMERGENCY
                    && r.getStatus() != bloodlink.model.enums.RequestStatus.RECEIVED
                    && r.getStatus() != bloodlink.model.enums.RequestStatus.REJECTED) {
                emergencyActive++;
            }
            if (r.getStatus() == bloodlink.model.enums.RequestStatus.RECEIVED) {
                received++;
            }
        }
        emergencyLabel.setText(String.valueOf(emergencyActive));

        int rate = allRequests.isEmpty() ? 100 : (received * 100) / allRequests.size();
        fulfilledLabel.setText(rate + "%");

        if (emergencyActive > 0) {
            alertLabel.setText(emergencyActive + " EMERGENCY request(s) still active. Review in \"All Requests / Escalate\".");
            alertLabel.setForeground(Color.RED);
            alertLabel.setBackground(new Color(255, 240, 240));
        } else {
            alertLabel.setText("No active emergencies. Hospital operations normal.");
            alertLabel.setForeground(new Color(46, 125, 50));
            alertLabel.setBackground(new Color(240, 255, 240));
        }
    }
}
