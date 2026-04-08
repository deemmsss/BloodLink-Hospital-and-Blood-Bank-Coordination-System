/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bloodlink.main;

import bloodlink.ui.LoginScreen;
import javax.swing.*;

/**
 *
 * @author demi
 * 
 * Application entry point for BloodLink.
 * Sets a modern look-and-feel and launches the login screen.
 */
public class BloodLinkApp {

    public static void main(String[] args) {
        // Set the Nimbus look-and-feel for a cleaner, modern appearance.
        // Nimbus is built into Java — no external dependencies needed.
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // If Nimbus isn't available, fall back to the default look-and-feel
            System.out.println("Nimbus L&F not available, using default.");
        }

        // Launch the login screen on the Swing Event Dispatch Thread (EDT).
        // All Swing UI work must happen on the EDT to avoid threading issues.
        SwingUtilities.invokeLater(() -> {
            new LoginScreen().setVisible(true);
        });
    }
}
