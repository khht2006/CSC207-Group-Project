package app;

import interface_adapter.*;
import usecase.*;
import usecase.get_bike_cost.*;
import usecase.search_history.*;
import view.*;

import javax.swing.*;
import java.awt.*;

/**
 * AppBuilder constructs the entire application:
 * - API fetcher
 * - Interactors (use cases)
 * - Presenters + view models
 * - Controllers
 * - Swing panels
 * - Navigation (CardLayout)
 *
 * Returns JFrame.
 */
public class AppBuilder {

    public static JFrame build() {

        // Delete history at app startup
        new java.io.File("search_history.txt").delete();

        ApiFetcher apiFetcher = new ApiFetcher();
        GeocodeLocationInteractor geocode = new GeocodeLocationInteractor(apiFetcher);
        SearchHistoryData historyGateway = new SearchHistoryGateway();

        // Bike time use case
        GetBikeTimeViewModel bikeTimeVM = new GetBikeTimeViewModel();
        GetBikeTimePresenter bikeTimePresenter = new GetBikeTimePresenter(bikeTimeVM);
        BikeRouteInteractor bikeRoute = new BikeRouteInteractor(apiFetcher, bikeTimePresenter);
        GetBikeTimeController bikeTimeController = new GetBikeTimeController(bikeRoute);
        GetTimePanel bikeTimePanel = new GetTimePanel(bikeTimeVM, bikeTimeController);

        // Bike cost use case
        GetBikeCostViewModel bikeCostVM = new GetBikeCostViewModel();
        GetBikeCostPresenter bikeCostPresenter = new GetBikeCostPresenter(bikeCostVM);
        GetBikeCostInteractor bikeCostInteractor = new GetBikeCostInteractor(bikeCostPresenter);
        GetBikeCostController bikeCostController =
                new GetBikeCostController(bikeCostInteractor, bikeTimeVM);
        GetCostPanel bikeCostPanel = new GetCostPanel(bikeCostVM);

        //  Compare
        CompareViewModel compareVM = new CompareViewModel();
        CompareSummaryPanel comparePanel = new CompareSummaryPanel(compareVM);

        // Origin + Search History
        OriginalDestinationPanel originPanel = new OriginalDestinationPanel();
        SearchHistoryPanel historyPanel = new SearchHistoryPanel();

        CardLayout layout = new CardLayout();
        JPanel root = new JPanel(layout);

        root.add(originPanel, "origin");
        root.add(bikeTimePanel, "bikeTime");
        root.add(bikeCostPanel, "bikeCost");
        root.add(comparePanel, "compare");
        root.add(historyPanel, "searchHistory");

        new OriginalDestinationController(
                originPanel,
                geocode,
                (origin, dest) -> {
                    layout.show(root, "bikeTime");

                    bikeTimePanel.requestBikeTime(
                            origin.getLatitude(), origin.getLongitude(),
                            dest.getLatitude(), dest.getLongitude()
                    );
                    bikeTimePanel.updateBikeTimeText();

                    double bikeTime = bikeTimeVM.getBikeTimeValue();

                    // Cost calculation
                    bikeCostInteractor.execute(new GetBikeCostInputData(bikeTime));
                    double bikeCost = bikeCostVM.getBikeCostValue();

                    // need to change after walking usecase is finished
                    double walkTime = bikeTime;

                    // Save record
                    historyGateway.save(new SearchRecord(
                            origin.getName(),
                            dest.getName(),
                            bikeTime,
                            bikeCost,
                            walkTime
                    ));
                }
        );

        //  Navigation buttons
        bikeTimePanel.getCostButton().addActionListener(e -> {
            layout.show(root, "bikeCost");
            bikeCostController.calculateCost();
            bikeCostPanel.updateBikeCostText();
        });

        bikeCostPanel.getCompareButton().addActionListener(e -> {

            double t = bikeTimeVM.getBikeTimeValue();

            // FIX: separate lines so it does NOT repeat
            compareVM.setWalkTimeText("Walk Time: " + t + " min");
            compareVM.setBikeTimeText("Bike Time: " + t + " min");
            compareVM.setBikeCostText(bikeCostVM.getBikeCostText());

            comparePanel.updateSummary();
            layout.show(root, "compare");
        });

        bikeTimePanel.getBackButton().addActionListener(e -> layout.show(root, "origin"));
        bikeCostPanel.getBackButton().addActionListener(e -> layout.show(root, "bikeTime"));
        comparePanel.getBackButton().addActionListener(e -> layout.show(root, "bikeCost"));

        // Search History
        originPanel.getViewHistoryButton().addActionListener(e -> {
            var records = historyGateway.load();
            if (records.isEmpty()) historyPanel.setNoHistoryMessage();
            else historyPanel.setHistory(records);
            layout.show(root, "searchHistory");
        });

        historyPanel.getBackButton().addActionListener(e -> layout.show(root, "origin"));

        JFrame frame = new JFrame("Grapes Trip Planner");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(root, BorderLayout.CENTER);
        frame.setSize(600, 300);
        frame.setLocationRelativeTo(null);

        return frame;
    }
}
