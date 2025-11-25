package app;

import usecase.get_bike_cost.GetBikeCostInputData;
import usecase.search_history.SearchHistoryData;
import usecase.search_history.SearchRecord;
import interface_adapter.SearchHistoryGateway;
import view.SearchHistoryPanel;

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
/**
* Following clean architecture principles
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::createAndShowUI);
    }

    private static void createAndShowUI() {
        // Delete the search history when exit the app
        new java.io.File("search_history.txt").delete();

        JFrame frame = new JFrame("Grapes Trip Planner");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        ApiFetcher apiFetcher = new ApiFetcher();
        GeocodeLocationInteractor geocodeInteractor = new GeocodeLocationInteractor(apiFetcher);

        SearchHistoryData historyGateway = new SearchHistoryGateway();

        // Bike time
        GetBikeTimeViewModel bikeTimeViewModel = new GetBikeTimeViewModel();
        GetBikeTimePresenter bikeTimePresenter = new GetBikeTimePresenter(bikeTimeViewModel);
        BikeRouteInteractor bikeRouteInteractor = new BikeRouteInteractor(apiFetcher, bikeTimePresenter);
        GetBikeTimeController bikeTimeController = new GetBikeTimeController(bikeRouteInteractor);
        GetTimePanel bikeTimePanel = new GetTimePanel(bikeTimeViewModel, bikeTimeController);

        // Bike cost
        GetBikeCostViewModel bikeCostViewModel = new GetBikeCostViewModel();
        GetBikeCostPresenter bikeCostPresenter = new GetBikeCostPresenter(bikeCostViewModel);
        GetBikeCostInteractor bikeCostInteractor = new GetBikeCostInteractor(bikeCostPresenter);
        GetBikeCostController bikeCostController =
                new GetBikeCostController(bikeCostInteractor, bikeTimeViewModel);
        GetCostPanel bikeCostPanel = new GetCostPanel(bikeCostViewModel);

        // Origin + history panels
        OriginalDestinationPanel originDestPanel = new OriginalDestinationPanel();
        SearchHistoryPanel historyPanel = new SearchHistoryPanel();

        CardLayout layout = new CardLayout();
        JPanel root = new JPanel(layout);
        root.add(originDestPanel, "origin");
        root.add(bikeTimePanel, "bikeTime");
        root.add(bikeCostPanel, "bikeCost");
        root.add(historyPanel, "searchHistory");

        new OriginalDestinationController(originDestPanel, geocodeInteractor, (origin, destination) -> {
            layout.show(root, "bikeTime");
            bikeTimePanel.requestBikeTime(
                    origin.getLatitude(), origin.getLongitude(),
                    destination.getLatitude(), destination.getLongitude()
            );
            bikeTimePanel.updateBikeTimeText();
            // (you can add saving history here later)
            // Save History
            double bikeTime = bikeTimeViewModel.getBikeTimeValue();

            GetBikeCostInputData costInput = new GetBikeCostInputData(bikeTime);
            bikeCostInteractor.execute(costInput);
            double bikeCost = bikeCostViewModel.getBikeCostValue();

            //Need to change after walking usecase is done
            double walkTime = bikeTime;

            SearchRecord record = new SearchRecord(
                    origin.getName(),
                    destination.getName(),
                    bikeTime,
                    bikeCost,
                    walkTime
            );

            historyGateway.save(record);

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

        // View Search History (button on origin panel)
        originDestPanel.getViewHistoryButton().addActionListener(e -> {
            var records = historyGateway.load();
            if (records.isEmpty()) {
                historyPanel.setNoHistoryMessage();
            } else {
                historyPanel.setHistory(records);
            }
            layout.show(root, "searchHistory");
        });

        // From Search History to Original (Back)
        historyPanel.getBackButton().addActionListener(e -> {
            layout.show(root, "origin");
        });

        frame.getContentPane().add(root, BorderLayout.CENTER);
        frame.setSize(600, 300);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        SwingUtilities.invokeLater(() -> {
            JFrame app = AppBuilder.build();  // Build everything using AppBuilder
            app.setVisible(true);            // Show UI
        });
    }
}