/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bloodlink.ui.hospital;

import bloodlink.dao.BloodRequestDAO;
import bloodlink.dao.PatientDAO;
import bloodlink.model.BloodRequest;
import bloodlink.model.User;
import bloodlink.model.enums.RequestStatus;
import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 *
 * @author demi
 * 
 * Dashboard home panel for the Hospital Nurse.
 * Shows at-a-glance metrics: patient count, request counts by status,
 * and alerts for shipped requests awaiting confirmation.
 * 
 * This makes the cross-enterprise pipeline visible:
 * the nurse can see requests that the Blood Bank has shipped back.
 */
public class NurseDashboardPanel extends JPanel {

    private User currentUser;
    private PatientDAO patientDAO;
    private BloodRequestDAO requestDAO;

    // Metric labels
    private JLabel patientCountLabel;
    private JLabel pendingLabel, approvedLabel, shippedLabel, receivedLabel, rejectedLabel;
    private JLabel alertLabel;

    public NurseDashboardPanel(User currentUser) {
        this.currentUser = currentUser;
        this.patientDAO = new PatientDAO();
        this.requestDAO = new BloodRequestDAO();
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
        JPanel cardsPanel = new JPanel(new GridLayout(2, 3, 15, 15));
        cardsPanel.setBackground(Color.WHITE);

        patientCountLabel = new JLabel("0", SwingConstants.CENTER);
        cardsPanel.add(createCard("My Patients", patientCountLabel, new Color(21, 101, 192)));

        pendingLabel = new JLabel("0", SwingConstants.CENTER);
        cardsPanel.add(createCard("Pending", pendingLabel, new Color(200, 150, 0)));

        approvedLabel = new JLabel("0", SwingConstants.CENTER);
        cardsPanel.add(createCard("Approved", approvedLabel, new Color(30, 136, 229)));

        shippedLabel = new JLabel("0", SwingConstants.CENTER);
        cardsPanel.add(createCard("Shipped (Awaiting Receipt)", shippedLabel, new Color(120, 60, 180)));

        receivedLabel = new JLabel("0", SwingConstants.CENTER);
        cardsPanel.add(createCard("Received", receivedLabel, new Color(46, 125, 50)));

        rejectedLabel = new JLabel("0", SwingConstants.CENTER);
        cardsPanel.add(createCard("Rejected", rejectedLabel, new Color(198, 40, 40)));

        add(cardsPanel, BorderLayout.CENTER);

        // ── Alert banner ─────────────────────────
        alertLabel = new JLabel(" ", SwingConstants.CENTER);
        alertLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
        alertLabel.setOpaque(true);
        alertLabel.setBackground(Color.WHITE);
        alertLabel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        add(alertLabel, BorderLayout.SOUTH);
    }

    /**
     * Creates a styled metric card with a title, large number, and colored left border.
     */
    private JPanel createCard(String title, JLabel valueLabel, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout(10, 5));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // Colored accent strip on the left
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
        int patientCount = patientDAO.getPatientsByNurse(currentUser.getId()).size();
        patientCountLabel.setText(String.valueOf(patientCount));

        List<BloodRequest> requests = requestDAO.getRequestsByNurse(currentUser.getId());
        int pending = 0, approved = 0, shipped = 0, received = 0, rejected = 0;
        for (BloodRequest r : requests) {
            switch (r.getStatus()) {
                case PENDING:  pending++;  break;
                case APPROVED: approved++; break;
                case SHIPPED:  shipped++;  break;
                case RECEIVED: received++; break;
                case REJECTED: rejected++; break;
                default: break;
            }
        }
        pendingLabel.setText(String.valueOf(pending));
        approvedLabel.setText(String.valueOf(approved));
        shippedLabel.setText(String.valueOf(shipped));
        receivedLabel.setText(String.valueOf(received));
        rejectedLabel.setText(String.valueOf(rejected));

        // Show alert if there are shipped requests awaiting confirmation
        if (shipped > 0) {
            alertLabel.setText("You have " + shipped + " shipment(s) ready to confirm. Go to \"Confirm Receipt\" to complete.");
            alertLabel.setForeground(new Color(120, 60, 180));
            alertLabel.setBackground(new Color(245, 240, 255));
        } else {
            alertLabel.setText("All caught up — no pending actions.");
            alertLabel.setForeground(new Color(46, 125, 50));
            alertLabel.setBackground(new Color(240, 255, 240));
        }
    }
}
