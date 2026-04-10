/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bloodlink.main;

import bloodlink.ui.LoginScreen;
import javax.swing.*;
import bloodlink.db.DBConnection;


/**
 *
 * @author demi
 * 
 * Application entry point for BloodLink.
 * Sets look-and-feel, registers a global exception handler
 * so no error goes unhandled, and closes the DB on shutdown.
 */
public class BloodLinkApp {

    public static void main(String[] args) {

        // ── Global exception handler ─────────────
        // Catches ANY uncaught exception on the Swing EDT and shows
        // a dialog instead of silently crashing. This protects against
        // the -10 mark penalty for an app crash.
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            throwable.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "An unexpected error occurred:\n" + throwable.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        });

        // ── Shutdown hook ────────────────────────
        // Ensures the database connection is closed cleanly
        // when the user closes the window or the JVM exits.
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            DBConnection.closeConnection();
        }));

        // ── Look and Feel ────────────────────────
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("Nimbus L&F not available, using default.");
        }

        // ── Launch ───────────────────────────────
        SwingUtilities.invokeLater(() -> {
            new LoginScreen().setVisible(true);
        });
    }
}