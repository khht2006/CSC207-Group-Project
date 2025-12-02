package app;

import java.awt.*;
import javax.swing.*;

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
import interface_adapter.SearchHistoryViewModel;
import interface_adapter.SearchHistoryPresenter;
import interface_adapter.SearchHistoryController;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.swing.JFrame;
import javax.swing.JPanel;

import usecase.BikeRouteInteractor;
import usecase.GeocodeLocationInteractor;
import usecase.WalkRouteInteractor;
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

        clearHistoryFile();

        ApiFetcher apiFetcher = new ApiFetcher();
        GeocodeLocationInteractor geocode = new GeocodeLocationInteractor(apiFetcher);
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
    }
}
