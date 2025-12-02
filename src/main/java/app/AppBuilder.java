package app;

import api.ApiFetcher;
import interface_adapter.compare_summary.CompareSummaryController;
import interface_adapter.compare_summary.CompareSummaryPresenter;
import interface_adapter.compare_summary.CompareSummaryViewModel;
import interface_adapter.GetBikeCostController;
import interface_adapter.GetBikeCostPresenter;
import interface_adapter.GetBikeCostViewModel;
import interface_adapter.GetBikeTimeController;
import interface_adapter.GetBikeTimePresenter;
import interface_adapter.GetBikeTimeViewModel;
import interface_adapter.OriginalDestinationController;
import interface_adapter.fetch_location.GeocodeController;
import interface_adapter.search_history.SearchHistoryGateway;
import interface_adapter.fetch_location.GeocodePresenter;
import interface_adapter.fetch_location.GeocodeViewModel;
import usecase.fetch_location.GeocodeLocationInteractor;
import interface_adapter.search_history.SearchHistoryViewModel;
import interface_adapter.search_history.SearchHistoryPresenter;
import interface_adapter.search_history.SearchHistoryController;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.swing.JFrame;
import javax.swing.JPanel;

import usecase.BikeRouteInteractor;
import usecase.WalkRouteInteractor;
import usecase.compare_summary.CompareSummaryInteractor;
import usecase.get_bike_cost.GetBikeCostInputData;
import usecase.get_bike_cost.GetBikeCostInteractor;
import usecase.search_history.SearchHistoryInputData;
import usecase.search_history.SearchHistoryInteractor;
import entity.SearchRecord;
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

    private final ApiFetcher apiFetcher = new ApiFetcher();
    private GeocodeViewModel geocodeViewModel;

    public AppBuilder() {
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

        SearchHistoryInputData historyGateway = new SearchHistoryGateway();

        // ------- Search History Use Case -------
        SearchHistoryViewModel historyViewModel = new SearchHistoryViewModel();
        SearchHistoryPresenter historyPresenter = new SearchHistoryPresenter(historyViewModel);
        SearchHistoryInteractor historyInteractor = new SearchHistoryInteractor(historyGateway, historyPresenter);
        SearchHistoryController historyController = new SearchHistoryController(historyInteractor);

        // ------- Bike Time Use Case -------
        GetBikeTimeViewModel bikeTimeVM = new GetBikeTimeViewModel();
        GetBikeTimePresenter bikeTimePresenter = new GetBikeTimePresenter(bikeTimeVM);
        BikeRouteInteractor bikeRoute = new BikeRouteInteractor(apiFetcher, bikeTimePresenter);
        GetBikeTimeController bikeTimeController = new GetBikeTimeController(bikeRoute);
        GetTimePanel bikeTimePanel = new GetTimePanel(bikeTimeVM, bikeTimeController);

        // ------- Bike Cost Use Case -------
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

        // ------- Origin + Search History -------
        OriginalDestinationPanel originPanel = new OriginalDestinationPanel();
        SearchHistoryPanel historyPanel = new SearchHistoryPanel();

        // ------- Navigation Setup -------
        CardLayout layout = new CardLayout();
        JPanel root = new JPanel(layout);

        root.add(originPanel, ORIGIN);
        root.add(bikeTimePanel, BIKE_TIME);
        root.add(bikeCostPanel, BIKE_COST);
        root.add(comparePanel, COMPARE);
        root.add(historyPanel, SEARCH_HISTORY);

        // ------- Origin → Bike Time -------
        new OriginalDestinationController(
                originPanel,
                geocodeController,
                geocodeVM,
                (origin, dest) -> {
                    if (origin == null || dest == null) return;
                    if (origin.getName() == null || dest.getName() == null) return;
                    if (origin.getName().isBlank() || dest.getName().isBlank()) return;

                    layout.show(root, BIKE_TIME);

                    bikeTimePanel.requestBikeTime(
                            origin.getLatitude(), origin.getLongitude(),
                            dest.getLatitude(), dest.getLongitude(),
                            dest.getName()
                    );
                    bikeTimePanel.updateBikeTimeText();
                    final double bikeTime = bikeTimeVM.getBikeTimeValue();

                    double walkTime;
                    try {
                        final WalkRouteInteractor.WalkRouteResponse walk =
                                walkRoute.execute(
                                        origin.getLatitude(), origin.getLongitude(),
                                        dest.getLatitude(), dest.getLongitude()
                                );
                        walkTime = walk.getTimeMinutes();
                    } catch (Exception ex) {
                        walkTime = -1;  // fallback
                    }

                    bikeTimePanel.setWalkTimeText(walkTime);

                    bikeCostInteractor.execute(new GetBikeCostInputData(bikeTime));
                    bikeCostPanel.updateBikeCostText();
                    final double bikeCost = bikeCostVM.getBikeCostValue();

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
            layout.show(root, BIKE_COST);
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
            } else {
                historyPanel.setHistory(records);
            }
            layout.show(root, SEARCH_HISTORY);
        });

        historyPanel.getBackButton()
                .addActionListener(e -> layout.show(root, ORIGIN));

        // ------- Final Frame -------
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

    /**
     * Handles transition from the origin form to the Bike Time screen.
     */
    private static void handleOriginSubmit(
            entity.Location origin,
            entity.Location dest,
            CardLayout layout,
            JPanel root,
            GetTimePanel bikeTimePanel,
            GetBikeTimeViewModel bikeVM,
            WalkRouteInteractor walkRoute,
            GetBikeCostInteractor costInteractor,
            GetBikeCostViewModel costVM,
            SearchHistoryInputData historyGateway
    ) {
        layout.show(root, BIKE_TIME);

        bikeTimePanel.requestBikeTime(
                origin.getLatitude(), origin.getLongitude(),
                dest.getLatitude(), dest.getLongitude(),
                dest.getName()

        );
        bikeTimePanel.updateBikeTimeText();

        double bikeTime = bikeVM.getBikeTimeValue();

        double walkTime;
        try {
            final WalkRouteInteractor.WalkRouteResponse walk =
                    walkRoute.execute(
                            origin.getLatitude(), origin.getLongitude(),
                            dest.getLatitude(), dest.getLongitude()
                    );
            walkTime = walk.getTimeMinutes();
        } catch (Exception ex) {
            walkTime = -1;
        }

        bikeTimePanel.setWalkTimeText(walkTime);

        costInteractor.execute(new GetBikeCostInputData(bikeTime));

        double bikeCost = costVM.getBikeCostValue();

        historyGateway.save(
                new SearchRecord(
                        origin.getName(),
                        dest.getName(),
                        bikeTime,
                        bikeCost,
                        walkTime
                )
        );
    }

    /**
     * Fills the compare summary view model with values.
     */
    private static void fillCompareSummary(
            CompareSummaryViewModel compareVM,
            GetBikeTimeViewModel bikeVM,
            GetTimePanel bikeTimePanel,
            GetBikeCostViewModel costVM
    ) {
        compareVM.setWalkTimeText(bikeTimePanel.getWalkTimeValue());
        compareVM.setBikeTimeText(bikeVM.getBikeTimeValue());
        compareVM.setBikeCostText(costVM.getBikeCostText());
    }
}
