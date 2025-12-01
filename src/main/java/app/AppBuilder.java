package app;

import api.ApiFetcher;
import entity.*;
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

    static final String ORIGIN = "origin";
    static final String BIKE_TIME = "bikeTime";
    static final String BIKE_COST = "bikeCost";
    static final String SEARCH_HISTORY = "searchHistory";
    static final String COMPARE = "compare";
    static final String SEARCH_HISTORY_FILE = "search_history.txt";

    private AppBuilder() {}

    public static JFrame build() {

        // Delete history at app startup
        clearSearchHistory();

        ApiFetcher apiFetcher = new ApiFetcher();
        GeocodeLocationInteractor geocode = new GeocodeLocationInteractor(apiFetcher);
        SearchHistoryData historyGateway = new SearchHistoryGateway();

        // Walking time use case
        WalkRouteInteractor walkRoute = new WalkRouteInteractor(apiFetcher);

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

        // Compare summary
        CompareViewModel compareVM = new CompareViewModel();
        CompareSummaryPanel comparePanel = new CompareSummaryPanel(compareVM);

        // Origin and Search History
        OriginalDestinationPanel originPanel = new OriginalDestinationPanel();
        SearchHistoryPanel historyPanel = new SearchHistoryPanel();

        // Search History CA use case wiring
        SearchHistoryViewModel historyVM = new SearchHistoryViewModel();
        SearchHistoryPresenter historyPresenter = new SearchHistoryPresenter(historyVM);
        SearchHistoryInteractor historyInteractor =
                new SearchHistoryInteractor(historyGateway, historyPresenter);
        SearchHistoryController historyController =
                new SearchHistoryController(historyInteractor);

        CardLayout layout = new CardLayout();
        JPanel root = new JPanel(layout);

        root.add(originPanel, ORIGIN);
        root.add(bikeTimePanel, BIKE_TIME);
        root.add(bikeCostPanel, BIKE_COST);
        root.add(comparePanel, COMPARE);
        root.add(historyPanel, SEARCH_HISTORY);

        new OriginalDestinationController(
                originPanel,
                geocode,
                (origin, dest) -> {

                    layout.show(root, BIKE_TIME);

                    bikeTimePanel.requestBikeTime(
                            origin.getLatitude(), origin.getLongitude(),
                            dest.getLatitude(), dest.getLongitude()
                    );
                    bikeTimePanel.updateBikeTimeText();
                    double bikeTime = bikeTimeVM.getBikeTimeValue();

                    double walkTime;
                    try {
                        WalkRouteInteractor.WalkRouteResponse walk =
                                walkRoute.execute(
                                        origin.getLatitude(), origin.getLongitude(),
                                        dest.getLatitude(), dest.getLongitude()
                                );
                        walkTime = walk.getTimeMinutes();
                    } catch (Exception ex) {
                        walkTime = -1;
                    }

                    bikeTimePanel.setWalkTimeText(walkTime);

                    bikeCostInteractor.execute(new GetBikeCostInputData(bikeTime));
                    double bikeCost = bikeCostVM.getBikeCostValue();

                    SearchRecord record = new SearchRecord(
                            originPanel.getOriginText(),
                            originPanel.getDestinationText(),
                            bikeTime,
                            bikeCost,
                            walkTime
                    );

                    historyGateway.save(record);
                }
        );

        // Navigation buttons
        bikeTimePanel.getCostButton().addActionListener(e -> {
            layout.show(root, BIKE_COST);
            bikeCostController.calculateCost();
            bikeCostPanel.updateBikeCostText();
        });

        bikeCostPanel.getCompareButton().addActionListener(e -> {
            compareVM.setWalkTimeText(bikeTimePanel.getWalkTimeValue());
            compareVM.setBikeTimeText(bikeTimeVM.getBikeTimeValue());
            compareVM.setBikeCostText(bikeCostVM.getBikeCostText());
            comparePanel.updateSummary();
            layout.show(root, COMPARE);
        });

        bikeTimePanel.getBackButton().addActionListener(e -> layout.show(root, ORIGIN));
        bikeCostPanel.getBackButton().addActionListener(e -> layout.show(root, BIKE_TIME));
        comparePanel.getBackButton().addActionListener(e -> layout.show(root, BIKE_COST));

        // Search History button
        originPanel.getViewHistoryButton().addActionListener(e -> {
            historyController.execute();
            var records = historyVM.getHistory();
            if (records == null || records.isEmpty()) historyPanel.setNoHistoryMessage();
            else historyPanel.setHistory(records);
            layout.show(root, SEARCH_HISTORY);
        });

        historyPanel.getBackButton().addActionListener(e -> layout.show(root, ORIGIN));

        JFrame frame = new JFrame("Grapes Trip Planner");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.add(root, BorderLayout.CENTER);
        frame.setSize(600, 300);
        frame.setLocationRelativeTo(null);

        return frame;
    }

    private static void clearSearchHistory() {
        try {
            java.nio.file.Files.deleteIfExists(java.nio.file.Path.of(SEARCH_HISTORY_FILE));
        } catch (java.io.IOException ignored) {}
    }
}
