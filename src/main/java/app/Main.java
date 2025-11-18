package app;

import interface_adapter.OriginalDestinationController;
import usecase.GeocodeLocationInteractor;
import view.OriginalDestinationPanel;

import javax.swing.*;
import java.awt.*;

/**
 * Entry point to show the initial "enter origin and destination" window.
 */
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::createAndShowUI);
    }

    private static void createAndShowUI() {
        JFrame frame = new JFrame("Grapes Trip Planner");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        OriginalDestinationPanel panel = new OriginalDestinationPanel();

        ApiFetcher apiFetcher = new ApiFetcher();
        GeocodeLocationInteractor geocodeInteractor = new GeocodeLocationInteractor(apiFetcher);

        // Controller wires the panel and use case
        new OriginalDestinationController(panel, geocodeInteractor);

        frame.getContentPane().add(panel, BorderLayout.CENTER);
        frame.setSize(600, 300);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
