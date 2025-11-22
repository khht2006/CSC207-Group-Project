package app;

import interface_adapter.GetBikeTimeController;
import interface_adapter.GetBikeTimePresenter;
import interface_adapter.GetBikeTimeViewModel;

import interface_adapter.GetBikeCostController;
import interface_adapter.GetBikeCostPresenter;
import interface_adapter.GetBikeCostViewModel;

import interface_adapter.OriginalDestinationController;
import usecase.BikeRouteInteractor;
import usecase.GeocodeLocationInteractor;
import usecase.get_bike_cost.GetBikeCostInteractor;
import view.GetTimePanel;
import view.GetCostPanel;
import view.OriginalDestinationPanel;

import javax.swing.*;
import java.awt.*;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::createAndShowUI);
    }

    private static double extractMinutes(String text) {
        String num = text.replaceAll("[^0-9.]", "");
        return Double.parseDouble(num);
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

        GetBikeCostViewModel bikeCostViewModel = new GetBikeCostViewModel();
        GetBikeCostPresenter bikeCostPresenter = new GetBikeCostPresenter(bikeCostViewModel);
        GetBikeCostInteractor bikeCostInteractor = new GetBikeCostInteractor(bikeCostPresenter);
        GetBikeCostController bikeCostController = new GetBikeCostController(bikeCostInteractor,bikeTimeViewModel);
        GetCostPanel bikeCostPanel = new GetCostPanel(bikeCostViewModel);

        OriginalDestinationPanel originDestPanel = new OriginalDestinationPanel();

        CardLayout layout = new CardLayout();
        JPanel root = new JPanel(layout);
        root.add(originDestPanel, "origin");
        root.add(bikeTimePanel, "bikeTime");
        root.add(bikeCostPanel, "bikeCost");

        new OriginalDestinationController(originDestPanel, geocodeInteractor, (origin, destination) -> {
            layout.show(root, "bikeTime");
            bikeTimePanel.requestBikeTime(
                    origin.getLatitude(), origin.getLongitude(),
                    destination.getLatitude(), destination.getLongitude());
            bikeTimePanel.updateBikeTimeText();
        });

        // from Time to Cost (See Bike Cost)
        bikeTimePanel.getCostButton().addActionListener(e -> {
            layout.show(root, "bikeCost");
            bikeCostController.calculateCost();
            bikeCostPanel.updateBikeCostText();
        });

        // From Time to Origin (Back)
        bikeTimePanel.getBackButton().addActionListener(e -> {
            layout.show(root, "origin");
        });

        // From Cost to Time (Back)
        bikeCostPanel.getBackButton().addActionListener(e -> {
            layout.show(root, "bikeTime");
        });

        frame.getContentPane().add(root, BorderLayout.CENTER);
        frame.setSize(600, 300);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
