package app;

import interface_adapter.GetBikeTimeController;
import interface_adapter.GetBikeTimePresenter;
import interface_adapter.GetBikeTimeViewModel;
import interface_adapter.OriginalDestinationController;
import usecase.BikeRouteInteractor;
import usecase.GeocodeLocationInteractor;
import view.GetTimePanel;
import view.OriginalDestinationPanel;

import javax.swing.*;
import java.awt.*;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::createAndShowUI);
    }

    private static void createAndShowUI() {
        JFrame frame = new JFrame("Grapes Trip Planner");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        ApiFetcher apiFetcher = new ApiFetcher();
        GeocodeLocationInteractor geocodeInteractor = new GeocodeLocationInteractor(apiFetcher);

        GetBikeTimeViewModel bikeTimeViewModel = new GetBikeTimeViewModel();
        GetBikeTimePresenter bikeTimePresenter = new GetBikeTimePresenter(bikeTimeViewModel);
        BikeRouteInteractor bikeRouteInteractor = new BikeRouteInteractor(apiFetcher, bikeTimePresenter);
        GetBikeTimeController bikeTimeController = new GetBikeTimeController(bikeRouteInteractor);
        GetTimePanel bikeTimePanel = new GetTimePanel(bikeTimeViewModel, bikeTimeController);

        OriginalDestinationPanel originDestPanel = new OriginalDestinationPanel();

        CardLayout layout = new CardLayout();
        JPanel root = new JPanel(layout);
        root.add(originDestPanel, "origin");
        root.add(bikeTimePanel, "bikeTime");

        // Back button → return to origin/destination screen
        bikeTimePanel.getBackButton().addActionListener(e -> {
            layout.show(root, "origin");
        });

        new OriginalDestinationController(originDestPanel, geocodeInteractor, (origin, destination) -> {
            layout.show(root, "bikeTime");
            bikeTimePanel.requestBikeTime(
                    origin.getLatitude(), origin.getLongitude(),
                    destination.getLatitude(), destination.getLongitude());
            bikeTimePanel.updateBikeTimeText();
        });

        frame.getContentPane().add(root, BorderLayout.CENTER);
        frame.setSize(600, 300);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
