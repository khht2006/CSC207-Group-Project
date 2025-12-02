package app;

import api.ApiFetcher;
import interface_adapter.CompareViewModel;
import interface_adapter.GetBikeCostController;
import interface_adapter.GetBikeCostPresenter;
import interface_adapter.GetBikeCostViewModel;
import interface_adapter.GetBikeTimeController;
import interface_adapter.GetBikeTimePresenter;
import interface_adapter.GetBikeTimeViewModel;
import interface_adapter.OriginalDestinationController;
import interface_adapter.SearchHistoryGateway;
import interface_adapter.fetch_location.GeocodeController;
import interface_adapter.fetch_location.GeocodePresenter;
import interface_adapter.fetch_location.GeocodeViewModel;
import usecase.fetch_location.GeocodeLocationInteractor;
import usecase.fetch_location.GeocodeOutputBoundary;


import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.swing.JFrame;
import javax.swing.JPanel;

import usecase.BikeRouteInteractor;
import usecase.fetch_location.GeocodeLocationInteractor;
import usecase.WalkRouteInteractor;
import usecase.get_bike_cost.GetBikeCostInputData;
import usecase.get_bike_cost.GetBikeCostInteractor;
import usecase.search_history.SearchHistoryData;
import entity.SearchRecord;
import view.CompareSummaryPanel;
import view.GetCostPanel;
import view.GetTimePanel;
import view.OriginalDestinationPanel;
import view.SearchHistoryPanel;

/**
 * AppBuilder constructs the entire Clean Architecture engine.
 * It creates:
 * <ul>
 *     <li>API clients</li>
 *     <li>Use case interactors</li>
 *     <li>Presenters and view models</li>
 *     <li>Controllers</li>
 *     <li>All Swing views</li>
 *     <li>Navigation (via CardLayout)</li>
 * </ul>
 *
 * <p>This class contains all dependency wiring and returns a
 * fully constructed {@link JFrame} ready to be displayed.</p>
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

    /** Private constructor — prevents instantiation. */
    public AppBuilder() {
        this.geocodeViewModel = new GeocodeViewModel();
    }



    /**
     * Builds the entire application using Clean Architecture structure.
     *
     * @return a fully configured {@link JFrame} containing all screens
     */
    public static JFrame build() {

        clearHistoryFile();

        ApiFetcher apiFetcher = new ApiFetcher();
        GeocodeLocationInteractor geocode = new GeocodeLocationInteractor(apiFetcher);
        WalkRouteInteractor walkRoute = new WalkRouteInteractor(apiFetcher);

        SearchHistoryData historyGateway = new SearchHistoryGateway();

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
        CompareViewModel compareVM = new CompareViewModel();
        CompareSummaryPanel comparePanel = new CompareSummaryPanel(compareVM);

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
                geocode,
                (origin, dest) -> handleOriginSubmit(
                        origin, dest, layout, root,
                        bikeTimePanel, bikeTimeVM,
                        walkRoute, bikeCostInteractor, bikeCostVM,
                        historyGateway
                )
        );

        // ------- Time → Cost -------
        bikeTimePanel.getCostButton().addActionListener(e -> {
            bikeCostController.calculateCost();
            bikeCostPanel.updateBikeCostText();
            layout.show(root, BIKE_COST);
        });

        // ------- Cost → Compare -------
        bikeCostPanel.getCompareButton().addActionListener(e -> {
            fillCompareSummary(compareVM, bikeTimeVM, bikeTimePanel, bikeCostVM);
            comparePanel.updateSummary();
            layout.show(root, COMPARE);
        });

        // ------- Back Buttons -------
        bikeTimePanel.getBackButton().addActionListener(e -> layout.show(root, ORIGIN));
        bikeCostPanel.getBackButton().addActionListener(e -> layout.show(root, BIKE_TIME));
        comparePanel.getBackButton().addActionListener(e -> layout.show(root, BIKE_COST));

        // ------- Search History -------
        originPanel.getViewHistoryButton().addActionListener(e -> {
            var records = historyGateway.load();
            if (records.isEmpty()) {
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
            SearchHistoryData historyGateway
    ) {
        layout.show(root, BIKE_TIME);

        bikeTimePanel.requestBikeTime(
                origin.getLatitude(), origin.getLongitude(),
                dest.getLatitude(), dest.getLongitude()
        );
        bikeTimePanel.updateBikeTimeText();

        double bikeTime = bikeVM.getBikeTimeValue();

        double walkTime;
        try {
            WalkRouteInteractor.WalkRouteResponse walk =
                    walkRoute.execute(
                            origin.getLatitude(), origin.getLongitude(),
                            dest.getLatitude(), dest.getLongitude()
                    );
            walkTime = walk.timeMinutes;
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
            CompareViewModel compareVM,
            GetBikeTimeViewModel bikeVM,
            GetTimePanel bikeTimePanel,
            GetBikeCostViewModel costVM
    ) {
        compareVM.setWalkTimeText(bikeTimePanel.getWalkTimeValue());
        compareVM.setBikeTimeText(bikeVM.getBikeTimeValue());
        compareVM.setBikeCostText(costVM.getBikeCostText());
    }
}
