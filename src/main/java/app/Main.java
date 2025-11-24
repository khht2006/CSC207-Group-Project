package app;

import javax.swing.*;
/**
* Following clean architecture principles
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame app = AppBuilder.build();  // Build everything using AppBuilder
            app.setVisible(true);            // Show UI
        });
    }
}