package app;

import javax.swing.*;

public class Main {
    /**
     * Main app entry point.
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Build everything using AppBuilder
            final JFrame app = AppBuilder.build();
            // Show UI
            app.setVisible(true);
        });
    }
}
