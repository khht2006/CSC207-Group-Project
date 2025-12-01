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
import interface_adapter.SearchHistoryPresenter;
import interface_adapter.SearchHistoryViewModel;
import interface_adapter.SearchHistoryController;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import usecase.BikeRouteInteractor;
import usecase.GeocodeLocationInteractor;
import usecase.WalkRouteInteractor;
import usecase.get_bike_cost.GetBikeCostInputData;
import usecase.get_bike_cost.GetBikeCostInteractor;
import usecase.search_history.SearchRecord;
import usecase.search_history.SearchHistoryInteractor;
import usecase.search_history.SearchHistoryData;

import view.CompareSummaryPanel;
import view.GetCostPanel;
import view.GetTimePanel;
import view.OriginalDestinationPanel;
import view.SearchHistoryPanel;

/**
 * AppBuilder constructs the entire Clean Architecture engine.
 * It wires:
 *  - API clients
 *  - Use Case interactors
 *  - Presenters + ViewModels
 *  - Controllers
 *  - Swing views
 *  - Navigation
 */
public final class AppBuilder {

    public static final String ORIGIN = "origin";
    public static final String BIKE_TIME = "bikeTime";
    public static final String BIKE_COST = "bikeCost";
    public static final String COMPARE = "compare";
    public static final String SEARCH_HISTORY = "searchHistory";

    public static final String HISTORY_FILE = "search_history.txt";

    private static final int FRAME_WIDTH = 600;
    private static final int FRAME_HEIGHT = 300;

    private AppBuilder() { }

    /** Build entire UI following Clean Architecture. */
    public static JFrame build() {

        clearHistoryFile();

        ApiFetcher apiFetcher = new ApiFetcher();
        GeocodeLocationInteractor geocode = new GeocodeLocationInteractor(apiFetcher);
        WalkRouteInteractor walkRoute = new WalkRouteInteractor(apiFetcher);

        SearchHistoryData historyGateway = new SearchHistoryGateway();

        // ==== Bike Time Use Case ====
        GetBikeTimeViewModel bikeTimeVM = new GetBikeTimeViewModel();
        GetBikeTimePresenter bikeTimePresenter = new GetBikeTimePresenter(bikeTimeVM);
        BikeRouteInteractor bikeRoute = new BikeRouteInteractor(apiFetcher, bikeTimePresenter);
        GetBikeTimeController bikeTimeController = new GetBikeTimeController(bikeRoute);
        GetTimePanel bikeTimePanel = new GetTimePanel(bikeTimeVM, bikeTimeController);

        // ==== Bike Cost Use Case ====
        GetBikeCostViewModel bikeCostVM = new GetBikeCostViewModel();
        GetBikeCostPresenter bikeCostPresenter = new GetBikeCostPresenter(bikeCostVM);
        GetBikeCostInteractor bikeCostInteractor = new GetBikeCostInteractor(bikeCostPresenter);
        GetBikeCostController bikeCostController =
                new GetBikeCostController(bikeCostInteractor, bikeTimeVM);
        GetCostPanel bikeCostPanel = new GetCostPanel(bikeCostVM);

        // ==== Compare Summary ====
        CompareViewModel compareVM = new CompareViewModel();
        CompareSummaryPanel comparePanel = new CompareSummaryPanel(compareVM);

        // ==== Origin + Search History ====
        OriginalDestinationPanel originPanel = new OriginalDestinationPanel();
        SearchHistoryPanel historyPanel = new SearchHistoryPanel();

        // Search History Use Case
        SearchHistoryViewModel historyVM = new SearchHistoryViewModel();
        SearchHistoryPresenter historyPresenter = new SearchHistoryPresenter(historyVM);
        SearchHistoryInteractor historyInteractor =
                new SearchHistoryInteractor(historyGateway, historyPresenter);
        SearchHistoryController historyController =
                new SearchHistoryController(historyInteractor);

        // ==== Layout & Root ====
        CardLayout layout = new CardLayout();
        JPanel root = new JPanel(layout);

        root.add(originPanel, ORIGIN);
        root.add(bikeTimePanel, BIKE_TIME);
        root.add(bikeCostPanel, BIKE_COST);
        root.add(comparePanel, COMPARE);
        root.add(historyPanel, SEARCH_HISTORY);

        // ==== Origin → Bike Time ====
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

        // ==== Time → Cost ====
        bikeTimePanel.getCostButton().addActionListener(e -> {
            bikeCostController.calculateCost();
            bikeCostPanel.updateBikeCostText();
            layout.show(root, BIKE_COST);
        });

        // ==== Cost → Compare ====
        bikeCostPanel.getCompareButton().addActionListener(e -> {
            compareVM.setWalkTimeText(bikeTimePanel.getWalkTimeValue());
            compareVM.setBikeTimeText(bikeTimeVM.getBikeTimeValue());
            compareVM.setBikeCostText(bikeCostVM.getBikeCostText());
            comparePanel.updateSummary();
            layout.show(root, COMPARE);
        });

        // ==== Back Buttons ====
        bikeTimePanel.getBackButton().addActionListener(e -> layout.show(root, ORIGIN));
        bikeCostPanel.getBackButton().addActionListener(e -> layout.show(root, BIKE_TIME));
        comparePanel.getBackButton().addActionListener(e -> layout.show(root, BIKE_COST));

        // ==== View Search History ====
        originPanel.getViewHistoryButton().addActionListener(e -> {
            historyController.execute();  // run use case
            var records = historyVM.getHistory();

            if (records == null || records.isEmpty()) {
                historyPanel.setNoHistoryMessage();
            } else {
                historyPanel.setHistory(records);
            }
            layout.show(root, SEARCH_HISTORY);
        });

        historyPanel.getBackButton().addActionListener(e -> layout.show(root, ORIGIN));

        // ==== Frame ====
        JFrame frame = new JFrame("Grapes Trip Planner");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.add(root, BorderLayout.CENTER);
        frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        frame.setLocationRelativeTo(null);

        return frame;
    }

    /** Delete search history file at startup. */
    private static void clearHistoryFile() {
        try {
            Files.deleteIfExists(Path.of(HISTORY_FILE));
        } catch (IOException ignored) { }
    }

    /** Logic for Origin → Time, Cost, Save History. */
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

        // Bike route
        bikeTimePanel.requestBikeTime(
                origin.getLatitude(), origin.getLongitude(),
                dest.getLatitude(), dest.getLongitude()
        );
        bikeTimePanel.updateBikeTimeText();

        double bikeTime = bikeVM.getBikeTimeValue();

        // Walk time
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

        // Bike Cost
        costInteractor.execute(new GetBikeCostInputData(bikeTime));
        double bikeCost = costVM.getBikeCostValue();

        // Save history
        historyGateway.save(new SearchRecord(
                origin.getName(),
                dest.getName(),
                bikeTime,
                bikeCost,
                walkTime
        ));
    }
}

