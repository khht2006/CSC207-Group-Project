package app;

import api.ApiFetcher;
import interface_adapter.calculate_route.CalculateRouteController;
import interface_adapter.calculate_route.CalculateRoutePresenter;
import interface_adapter.compare_summary.CompareSummaryController;
import interface_adapter.compare_summary.CompareSummaryPresenter;
import interface_adapter.compare_summary.CompareSummaryViewModel;
import interface_adapter.bike_cost.GetBikeCostController;
import interface_adapter.bike_cost.GetBikeCostPresenter;
import interface_adapter.bike_cost.GetBikeCostViewModel;
import interface_adapter.bike_time.GetBikeTimeController;
import interface_adapter.bike_time.GetBikeTimePresenter;
import interface_adapter.bike_time.GetBikeTimeViewModel;
import interface_adapter.original_destination.OriginalDestinationController;
import interface_adapter.fetch_location.GeocodeController;
import interface_adapter.search_history.SearchHistoryGateway;
import interface_adapter.fetch_location.GeocodePresenter;
import interface_adapter.fetch_location.GeocodeViewModel;
import interface_adapter.select_route_locations.SelectRouteLocationsController;
import interface_adapter.select_route_locations.SelectRouteLocationsPresenter;
import usecase.calculate_route.CalculateRouteInteractor;
import usecase.fetch_location.GeocodeLocationInteractor;
import interface_adapter.search_history.SearchHistoryViewModel;
import interface_adapter.search_history.SearchHistoryPresenter;
import interface_adapter.search_history.SearchHistoryController;
import interface_adapter.delete_history.DeleteHistoryController;
import interface_adapter.delete_history.DeleteHistoryPresenter;
import interface_adapter.delete_history.DeleteHistoryViewModel;

import usecase.delete_history.DeleteHistoryInteractor;


import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.swing.JFrame;
import javax.swing.JPanel;

import usecase.bike_route.BikeRouteInteractor;
import usecase.walk_route.WalkRouteInteractor;
import usecase.compare_summary.CompareSummaryInteractor;
import usecase.get_bike_cost.GetBikeCostInputData;
import usecase.get_bike_cost.GetBikeCostInteractor;
import usecase.search_history.SearchHistoryInteractor;
import usecase.select_route_locations.SelectRouteLocationsInteractor;
import view.CompareSummaryPanel;
import view.GetCostPanel;
import view.GetTimePanel;
import view.OriginalDestinationPanel;
import view.SearchHistoryPanel;

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
public class AppBuilder {

    /** Screen identifier for origin input view. */
    public static final String ORIGIN = "origin";

    /** Screen identifier for bike time result view. */
    public static final String BIKE_TIME = "bikeTime";

    /** Screen identifier for bike cost result view. */
    public static final String BIKE_COST = "bikeCost";

    /** Screen identifier for comparison summary view. */
    public static final String COMPARE = "compare";

    /** Screen identifier for search history view. */
    public static final String SEARCH_HISTORY = "searchHistory";

    /** File where search history is stored. */
    public static final String HISTORY_FILE = "search_history.txt";

    /** Default frame width. */
    private static final int FRAME_WIDTH = 600;

    /** Default frame height. */
    private static final int FRAME_HEIGHT = 300;


    public AppBuilder() {
        // utility class; no instances
    }

    /**
     * Builds the main application JFrame.
     * @return the main application JFrame
     */
    public static JFrame build() {

        clearHistoryFile();

        ApiFetcher apiFetcher = new ApiFetcher();

        GeocodeViewModel geocodeVM = new GeocodeViewModel();
        GeocodePresenter geocodePresenter = new GeocodePresenter(geocodeVM);
        GeocodeLocationInteractor geocodeInteractor = new GeocodeLocationInteractor(apiFetcher, geocodePresenter);
        GeocodeController geocodeController = new GeocodeController(geocodeInteractor);
        WalkRouteInteractor walkRoute = new WalkRouteInteractor(apiFetcher);

        // ----------------- GATEWAY -----------------
        SearchHistoryGateway historyGateway = new SearchHistoryGateway();

        // ----------------- SEARCH HISTORY -----------------
        SearchHistoryViewModel historyVM = new SearchHistoryViewModel();
        SearchHistoryPresenter historyPresenter = new SearchHistoryPresenter(historyVM);
        SearchHistoryInteractor historyInteractor =
                new SearchHistoryInteractor(historyGateway, historyPresenter);
        SearchHistoryController historyController =
                new SearchHistoryController(historyInteractor);

        // ----------------- DELETE HISTORY -----------------
        DeleteHistoryViewModel deleteVM = new DeleteHistoryViewModel();
        DeleteHistoryPresenter deletePresenter = new DeleteHistoryPresenter(deleteVM);
        DeleteHistoryInteractor deleteInteractor =
                new DeleteHistoryInteractor(historyGateway, deletePresenter);
        DeleteHistoryController deleteController =
                new DeleteHistoryController(deleteInteractor);

        // ----------------- BIKE TIME -----------------
        GetBikeTimeViewModel bikeTimeVM = new GetBikeTimeViewModel();
        GetBikeTimePresenter bikeTimePresenter = new GetBikeTimePresenter(bikeTimeVM);
        BikeRouteInteractor bikeRoute = new BikeRouteInteractor(apiFetcher, bikeTimePresenter);
        GetBikeTimeController bikeTimeController = new GetBikeTimeController(bikeRoute);
        GetTimePanel bikeTimePanel = new GetTimePanel(bikeTimeVM, bikeTimeController);

        // ----------------- BIKE COST -----------------
        GetBikeCostViewModel bikeCostVM = new GetBikeCostViewModel();
        GetBikeCostPresenter bikeCostPresenter = new GetBikeCostPresenter(bikeCostVM);
        GetBikeCostInteractor bikeCostInteractor = new GetBikeCostInteractor(bikeCostPresenter);
        GetBikeCostController bikeCostController =
                new GetBikeCostController(bikeCostInteractor, bikeTimeVM);
        GetCostPanel bikeCostPanel = new GetCostPanel(bikeCostVM);

        // ------- Compare Summary -------
        final CompareSummaryViewModel compareSummaryViewModel = new CompareSummaryViewModel();
        final CompareSummaryPresenter comparePresenter = new CompareSummaryPresenter(compareSummaryViewModel);
        final CompareSummaryInteractor compareInteractor = new CompareSummaryInteractor(comparePresenter);
        final CompareSummaryController compareController = new CompareSummaryController(compareInteractor);
        final CompareSummaryPanel comparePanel = new CompareSummaryPanel(compareSummaryViewModel);

        // ----------------- ORIGIN + HISTORY PANELS -----------------
        OriginalDestinationPanel originPanel = new OriginalDestinationPanel();
        SearchHistoryPanel historyPanel = new SearchHistoryPanel();

        // ----------------- NAVIGATION -----------------
        final CardLayout layout = new CardLayout();
        final JPanel root = new JPanel(layout);
        root.add(originPanel, ORIGIN);
        root.add(bikeTimePanel, BIKE_TIME);
        root.add(bikeCostPanel, BIKE_COST);
        root.add(comparePanel, COMPARE);
        root.add(historyPanel, SEARCH_HISTORY);


        // ----------------- CALCULATE ROUTE -----------------
        CalculateRoutePresenter calculatePresenter = new CalculateRoutePresenter(
                bikeTimePanel,
                bikeTimeVM,
                bikeCostVM,
                () -> layout.show(root, BIKE_TIME)
        );

        CalculateRouteInteractor calculateInteractor = new CalculateRouteInteractor(
                bikeRoute,
                walkRoute,
                historyGateway,
                calculatePresenter,
                bikeTimeVM
        );

        CalculateRouteController calculateController =
                new CalculateRouteController(calculateInteractor);

        // ----------------- SELECT ROUTE LOCATIONS -----------------
        SelectRouteLocationsPresenter selectRoutePresenter = new SelectRouteLocationsPresenter(
                calculateController::execute
        );

        SelectRouteLocationsInteractor selectRouteInteractor =
                new SelectRouteLocationsInteractor(selectRoutePresenter);
        SelectRouteLocationsController selectRouteController =
                new SelectRouteLocationsController(selectRouteInteractor);



        // ----------------- ORIGIN SUBMIT -----------------
        new OriginalDestinationController(
                originPanel,
                geocodeController,
                geocodeVM,
                selectRouteController,
                (origin, dest) -> {} // Empty callback since presenter handles it now
        );

        // ----------------- BUTTONS -----------------
        bikeTimePanel.getCostButton().addActionListener(e -> {
            layout.show(root, BIKE_COST);
            bikeCostController.calculateCost();
            bikeCostPanel.updateBikeCostText();
        });

        // Navigation: bikeCost → compare summary
        bikeCostPanel.getCompareButton().addActionListener(actionEvent -> {

            final double walkTime = bikeTimePanel.getWalkTimeValue();
            final double bikeTime = bikeTimeVM.getBikeTimeValue();
            final double bikeCost = bikeCostVM.getBikeCostValue();

            compareController.execute(walkTime, bikeTime, bikeCost);
            comparePanel.updateSummary();
            layout.show(root, COMPARE);
        });

        bikeTimePanel.getBackButton().addActionListener(e ->
                layout.show(root, ORIGIN));
        bikeCostPanel.getBackButton().addActionListener(e ->
                layout.show(root, BIKE_TIME));
        comparePanel.getBackButton().addActionListener(e ->
                layout.show(root, BIKE_COST));

        // ----------------- VIEW HISTORY BUTTON -----------------
        originPanel.getViewHistoryButton().addActionListener(e -> {
            historyController.execute();
            var records = historyVM.getHistory();
            if (records == null || records.isEmpty()) {
                historyPanel.setNoHistoryMessage();
            } else {
                historyPanel.setHistory(records);
            }
            layout.show(root, SEARCH_HISTORY);
        });

        historyPanel.getBackButton().addActionListener(e ->
                layout.show(root, ORIGIN));

        // ----------------- DELETE HISTORY BUTTON -----------------
        originPanel.getDeleteHistoryButton().addActionListener(e -> {
            deleteController.execute();
        });

        JFrame frame = new JFrame("Grapes Trip Planner");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(root, BorderLayout.CENTER);
        frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        frame.setLocationRelativeTo(null);

        return frame;
    }

    /**
     * Clears the search history file on startup.
     */
    private static void clearHistoryFile() {
        try {
            Files.deleteIfExists(Path.of(HISTORY_FILE));
        } catch (IOException ignored) {
            // file may not exist — ignore errors
        }
    }

}
