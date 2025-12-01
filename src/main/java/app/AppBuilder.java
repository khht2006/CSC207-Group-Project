package app;

import java.awt.*;
import javax.swing.*;

import api.ApiFetcher;
import entity.*;
import interface_adapter.*;
import usecase.*;
import usecase.geocode_location.GeocodeLocationInteractor;
import usecase.get_bike_cost.*;
import usecase.search_history.*;
import view.*;

/**
 * AppBuilder constructs the entire application.
 * - API fetcher
 * - Interactors (use cases)
 * - Presenters + view models
 * - Controllers
 * - Swing panels
 * - Navigation (CardLayout)
 * Returns JFrame.
 */
public final class AppBuilder {
    static final String ORIGIN = "origin";
    static final String BIKE_TIME = "bikeTime";
    static final String BIKE_COST = "bikeCost";
    static final String SEARCH_HISTORY = "searchHistory";
    static final String COMPARE = "compare";
    static final String SEARCH_HISTORY_FILE = "search_history.txt";

    private AppBuilder() {
    }

    /**
     * Builds the main application JFrame.
     * @return the main application JFrame
     */
    public static JFrame build() {

        // Delete history at app startup
        clearSearchHistory();

        final ApiFetcher apiFetcher = new ApiFetcher();
        final GeocodeLocationInteractor geocode = new GeocodeLocationInteractor(apiFetcher);
        final SearchHistoryData historyGateway = new SearchHistoryGateway();

        // Walking time use case
        final WalkRouteInteractor walkRoute = new WalkRouteInteractor(apiFetcher);

        // Bike time use case
        final GetBikeTimeViewModel bikeTimeViewModel = new GetBikeTimeViewModel();
        final GetBikeTimePresenter bikeTimePresenter = new GetBikeTimePresenter(bikeTimeViewModel);
        final BikeRouteInteractor bikeRoute = new BikeRouteInteractor(apiFetcher, bikeTimePresenter);
        final GetBikeTimeController bikeTimeController = new GetBikeTimeController(bikeRoute);
        final GetTimePanel bikeTimePanel = new GetTimePanel(bikeTimeViewModel, bikeTimeController);

        // Bike cost use case
        final GetBikeCostViewModel bikeCostViewModel = new GetBikeCostViewModel();
        final GetBikeCostPresenter bikeCostPresenter = new GetBikeCostPresenter(bikeCostViewModel);
        final GetBikeCostInteractor bikeCostInteractor = new GetBikeCostInteractor(bikeCostPresenter);
        final GetBikeCostController bikeCostController =
                new GetBikeCostController(bikeCostInteractor, bikeTimeViewModel);
        final GetCostPanel bikeCostPanel = new GetCostPanel(bikeCostViewModel);

        // Compare summary
        final CompareViewModel compareViewModel = new CompareViewModel();
        final CompareSummaryPanel comparePanel = new CompareSummaryPanel(compareViewModel);

        // Origin + Search History
        final OriginalDestinationPanel originPanel = new OriginalDestinationPanel();
        final SearchHistoryPanel historyPanel = new SearchHistoryPanel();

        final CardLayout layout = new CardLayout();
        final JPanel root = new JPanel(layout);
        // Search History CA use case wiring
        final SearchHistoryViewModel historyViewModel = new SearchHistoryViewModel();
        final SearchHistoryPresenter historyPresenter = new SearchHistoryPresenter(historyViewModel);
        final SearchHistoryInteractor historyInteractor =
                new SearchHistoryInteractor(historyGateway, historyPresenter);
        final SearchHistoryController historyController =
                new SearchHistoryController(historyInteractor);

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
                    final double bikeTime = bikeTimeViewModel.getBikeTimeValue();

                    double walkTime;
                    try {
                        final WalkRouteInteractor.WalkRouteResponse walk =
                                walkRoute.execute(
                                        origin.getLatitude(), origin.getLongitude(),
                                        dest.getLatitude(), dest.getLongitude()
                                );
                        walkTime = walk.timeMinutes;
                    }
                    catch (Exception ex) {
                        // Safe to ignore: if walking route lookup fails, use -1 to indicate unavailable
                        walkTime = -1;
                    }

                    bikeTimePanel.setWalkTimeText(walkTime);

                    bikeCostInteractor.execute(new GetBikeCostInputData(bikeTime));
                    final double bikeCost = bikeCostViewModel.getBikeCostValue();

                    final SearchRecord searchRecord = new SearchRecord(
                            originPanel.getOriginText(),
                            originPanel.getDestinationText(),
                            bikeTime,
                            bikeCost,
                            walkTime
                    );

                    historyGateway.save(searchRecord);
                }
        );

        // Navigation buttons
        bikeTimePanel.getCostButton().addActionListener(actionEvent -> {
            layout.show(root, BIKE_COST);
            bikeCostController.calculateCost();
            bikeCostPanel.updateBikeCostText();
        });

        // Navigation: bikeCost → compare summary
        bikeCostPanel.getCompareButton().addActionListener(actionEvent -> {

            compareViewModel.setWalkTimeText(bikeTimePanel.getWalkTimeValue());
            compareViewModel.setBikeTimeText(bikeTimeViewModel.getBikeTimeValue());
            compareViewModel.setBikeCostText(bikeCostViewModel.getBikeCostText());

            comparePanel.updateSummary();
            layout.show(root, COMPARE);
        });

        // Back buttons
        bikeTimePanel.getBackButton().addActionListener(actionEvent -> layout.show(root, ORIGIN));
        bikeCostPanel.getBackButton().addActionListener(actionEvent -> layout.show(root, BIKE_TIME));
        comparePanel.getBackButton().addActionListener(actionEvent -> layout.show(root, BIKE_COST));

        // Search History button
        originPanel.getViewHistoryButton().addActionListener(actionEvent -> {
            historyController.execute();
            final var records = historyViewModel.getHistory();
            if (records == null || records.isEmpty()) {
                historyPanel.setNoHistoryMessage();
            }
            else {
                historyPanel.setHistory(records);
            }
            layout.show(root, SEARCH_HISTORY);
        });

        historyPanel.getBackButton().addActionListener(actionEvent -> layout.show(root, ORIGIN));

        final JFrame frame = new JFrame("Grapes Trip Planner");
        final int frameWidth = 600;
        final int frameHeight = 300;

        frame.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        frame.add(root, BorderLayout.CENTER);
        frame.setSize(frameWidth, frameHeight);
        frame.setLocationRelativeTo(null);

        return frame;
    }

    private static void clearSearchHistory() {
        try {
            java.nio.file.Files.deleteIfExists(java.nio.file.Path.of(AppBuilder.SEARCH_HISTORY_FILE));
        }
        catch (java.io.IOException ignored) {
            // Ignore errors if file cannot be deleted
        }
    }
}
