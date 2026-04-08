/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bloodlink.ui;

import bloodlink.model.User;
import bloodlink.model.enums.UserRole;
import bloodlink.ui.hospital.*;
import bloodlink.ui.bloodbank.*;
import javax.swing.*;
import java.awt.*;

/**
 *
 * @author demi
 * 
 * Main application frame shown after successful login.
 * 
 * Layout structure:
 * ┌──────────┬─────────────────────────────────────────┐
 * │          │  Header (role title + user name)         │
 * │ Sidebar  ├─────────────────────────────────────────┤
 * │ (nav     │                                         │
 * │  buttons)│  Content Area (CardLayout)              │
 * │          │  — swaps panels based on sidebar click  │
 * │          │                                         │
 * │ [Logout] │                                         │
 * └──────────┴─────────────────────────────────────────┘
 * 
 * The sidebar buttons and content panels are populated
 * based on the logged-in user's role.
 */
public class MainFrame extends JFrame {

    private User currentUser;

    // Content area uses CardLayout to swap panels
    private JPanel contentPanel;
    private CardLayout cardLayout;

    // Sidebar panel (holds navigation buttons)
    private JPanel sidebarPanel;

    // ── Color constants for consistent styling ───
    private static final Color SIDEBAR_BG = new Color(45, 45, 60);
    private static final Color SIDEBAR_BTN_BG = new Color(60, 60, 80);
    private static final Color SIDEBAR_BTN_HOVER = new Color(80, 80, 110);
    private static final Color HEADER_BG = new Color(180, 30, 30);
    private static final Color ACCENT = new Color(180, 30, 30);

    public MainFrame(User user) {
        this.currentUser = user;
        initComponents();
        loadRolePanels();
    }

    private void initComponents() {
        // ── Window settings ──────────────────────
        setTitle("BloodLink — " + currentUser.getDashboardTitle());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(900, 600));
        setLayout(new BorderLayout());

        // ── Header bar ───────────────────────────
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(HEADER_BG);
        headerPanel.setPreferredSize(new Dimension(0, 50));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel dashboardLabel = new JLabel(currentUser.getDashboardTitle());
        dashboardLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        dashboardLabel.setForeground(Color.WHITE);
        headerPanel.add(dashboardLabel, BorderLayout.WEST);

        JLabel userLabel = new JLabel("Logged in as: " + currentUser.getFullName());
        userLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        userLabel.setForeground(new Color(255, 220, 220));
        headerPanel.add(userLabel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // ── Sidebar ──────────────────────────────
        sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(SIDEBAR_BG);
        sidebarPanel.setPreferredSize(new Dimension(200, 0));
        sidebarPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(sidebarPanel, BorderLayout.WEST);

        // ── Content area (CardLayout) ────────────
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(Color.WHITE);
        add(contentPanel, BorderLayout.CENTER);
    }

    /**
     * Populates the sidebar buttons and content panels
     * based on which role is logged in.
     * 
     * Each sidebar button name matches a card name in the CardLayout,
     * so clicking a button flips to the corresponding panel.
     */
    private void loadRolePanels() {
        UserRole role = currentUser.getRole();

        switch (role) {
            case HOSPITAL_NURSE:
                addNavButton("My Patients", "patients");
                addNavButton("Create Blood Request", "createRequest");
                addNavButton("My Requests", "myRequests");
                addNavButton("Confirm Receipt", "confirmReceipt");

                contentPanel.add(new NursePatientPanel(currentUser), "patients");
                contentPanel.add(new NurseCreateRequestPanel(currentUser), "createRequest");
                contentPanel.add(new NurseMyRequestsPanel(currentUser), "myRequests");
                contentPanel.add(new NurseConfirmReceiptPanel(currentUser), "confirmReceipt");
                break;

            case HOSPITAL_ADMIN:
                addNavButton("Manage Nurses", "manageNurses");
                addNavButton("All Requests / Escalate", "escalate");
                addNavButton("Blood Usage Report", "usageReport");

                contentPanel.add(new AdminManageNursesPanel(), "manageNurses");
                contentPanel.add(new AdminEscalatePanel(), "escalate");
                contentPanel.add(new AdminUsageReportPanel(), "usageReport");
                break;

            case BLOOD_BANK_TECHNICIAN:
                addNavButton("Request Queue", "requestQueue");
                addNavButton("Prepare Shipment", "prepareShipment");
                addNavButton("Inventory", "inventory");

                contentPanel.add(new TechRequestQueuePanel(currentUser), "requestQueue");
                contentPanel.add(new TechPrepareShipmentPanel(currentUser), "prepareShipment");
                contentPanel.add(new TechInventoryPanel(), "inventory");
                break;

            case BLOOD_BANK_ADMIN:
                addNavButton("Manage Technicians", "manageTechs");
                addNavButton("Manage Donors", "manageDonors");
                addNavButton("Inventory Thresholds", "thresholds");
                addNavButton("Request Overview", "requestOverview");
                addNavButton("Inventory", "inventory");

                contentPanel.add(new BBAdminManageTechsPanel(), "manageTechs");
                contentPanel.add(new BBAdminManageDonorsPanel(), "manageDonors");
                contentPanel.add(new BBAdminThresholdsPanel(), "thresholds");
                contentPanel.add(new BBAdminRequestOverviewPanel(), "requestOverview");
                contentPanel.add(new TechInventoryPanel(), "inventory");
                break;
        }

        // ── Logout button at the bottom of the sidebar ──
        sidebarPanel.add(Box.createVerticalGlue()); // Pushes logout to the bottom
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setMaximumSize(new Dimension(200, 40));
        logoutBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        logoutBtn.setBackground(ACCENT);
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setBorderPainted(false);
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoutBtn.addActionListener(e -> handleLogout());
        sidebarPanel.add(logoutBtn);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10)));
    }

    /**
     * Adds a styled navigation button to the sidebar.
     * Clicking it shows the matching card in the content area.
     * 
     * @param label    text displayed on the button
     * @param cardName the name of the panel in the CardLayout to show
     */
    private void addNavButton(String label, String cardName) {
        JButton btn = new JButton(label);
        btn.setMaximumSize(new Dimension(200, 45));
        btn.setFont(new Font("SansSerif", Font.PLAIN, 14));
        btn.setBackground(SIDEBAR_BTN_BG);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Hover effect
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(SIDEBAR_BTN_HOVER);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(SIDEBAR_BTN_BG);
            }
        });

        // Click handler — switch the content panel
        btn.addActionListener(e -> cardLayout.show(contentPanel, cardName));

        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        sidebarPanel.add(btn);
    }

    /**
     * Creates a temporary placeholder panel.
     * Each placeholder will be replaced with a real screen in Phase 5.
     */
    private JPanel createPlaceholder(String text) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("SansSerif", Font.BOLD, 22));
        label.setForeground(new Color(180, 180, 180));
        panel.add(label, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Logs out the current user and returns to the login screen.
     */
    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to log out?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION
        );
        if (confirm == JOptionPane.YES_OPTION) {
            dispose(); // Close main frame
            new LoginScreen().setVisible(true); // Show login screen
        }
    }

    /**
     * Returns the currently logged-in user.
     * Child panels will call this to know who is logged in
     * (e.g., the nurse's ID when creating a blood request).
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Returns the content panel so child panels can be swapped
     * programmatically (e.g., after creating a request, switch to "My Requests").
     */
    public JPanel getContentPanel() {
        return contentPanel;
    }

    /**
     * Returns the CardLayout so child panels can trigger navigation.
     */
    public CardLayout getCardLayout() {
        return cardLayout;
    }
}