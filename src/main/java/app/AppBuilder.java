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

import interface_adapter.search_history.SearchHistoryGateway;
import interface_adapter.search_history.SearchHistoryViewModel;
import interface_adapter.search_history.SearchHistoryPresenter;
import interface_adapter.search_history.SearchHistoryController;

import interface_adapter.delete_history.DeleteHistoryController;
import interface_adapter.delete_history.DeleteHistoryPresenter;
import interface_adapter.delete_history.DeleteHistoryViewModel;

import usecase.delete_history.DeleteHistoryInteractor;
import usecase.delete_history.DeleteHistoryInputBoundary;

import interface_adapter.fetch_location.GeocodePresenter;
import interface_adapter.fetch_location.GeocodeViewModel;
import usecase.fetch_location.GeocodeLocationInteractor;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.swing.JFrame;
import javax.swing.JPanel;

import usecase.BikeRouteInteractor;
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

public class AppBuilder {

    public static final String ORIGIN = "origin";
    public static final String BIKE_TIME = "bikeTime";
    public static final String BIKE_COST = "bikeCost";
    public static final String COMPARE = "compare";
    public static final String SEARCH_HISTORY = "searchHistory";

    public static final String HISTORY_FILE = "search_history.txt";

    private static final int FRAME_WIDTH = 600;
    private static final int FRAME_HEIGHT = 300;

    public AppBuilder() {}

    public static JFrame build() {

        ApiFetcher apiFetcher = new ApiFetcher();

        GeocodeViewModel geocodeVM = new GeocodeViewModel();
        GeocodePresenter geocodePresenter = new GeocodePresenter(geocodeVM);
        GeocodeLocationInteractor geocode =
                new GeocodeLocationInteractor(apiFetcher, geocodePresenter);

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
        BikeRouteInteractor bikeRoute =
                new BikeRouteInteractor(apiFetcher, bikeTimePresenter);
        GetBikeTimeController bikeTimeController =
                new GetBikeTimeController(bikeRoute);
        GetTimePanel bikeTimePanel = new GetTimePanel(bikeTimeVM, bikeTimeController);

        // ----------------- BIKE COST -----------------
        GetBikeCostViewModel bikeCostVM = new GetBikeCostViewModel();
        GetBikeCostPresenter bikeCostPresenter =
                new GetBikeCostPresenter(bikeCostVM);
        GetBikeCostInteractor bikeCostInteractor =
                new GetBikeCostInteractor(bikeCostPresenter);
        GetBikeCostController bikeCostController =
                new GetBikeCostController(bikeCostInteractor, bikeTimeVM);
        GetCostPanel bikeCostPanel = new GetCostPanel(bikeCostVM);

        // ----------------- COMPARE -----------------
        CompareViewModel compareVM = new CompareViewModel();
        CompareSummaryPanel comparePanel = new CompareSummaryPanel(compareVM);

        // ----------------- ORIGIN + HISTORY PANELS -----------------
        OriginalDestinationPanel originPanel = new OriginalDestinationPanel();
        SearchHistoryPanel historyPanel = new SearchHistoryPanel();

        // ----------------- NAVIGATION -----------------
        CardLayout layout = new CardLayout();
        JPanel root = new JPanel(layout);

        root.add(originPanel, ORIGIN);
        root.add(bikeTimePanel, BIKE_TIME);
        root.add(bikeCostPanel, BIKE_COST);
        root.add(comparePanel, COMPARE);
        root.add(historyPanel, SEARCH_HISTORY);

        // ----------------- ORIGIN SUBMIT -----------------
        new OriginalDestinationController(
                originPanel,
                geocode,
                (origin, dest) -> {

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
                        walkTime = walkRoute.execute(
                                origin.getLatitude(), origin.getLongitude(),
                                dest.getLatitude(), dest.getLongitude()
                        ).getTimeMinutes();
                    } catch (Exception ex) {
                        walkTime = -1;
                    }

                    bikeTimePanel.setWalkTimeText(walkTime);

                    bikeCostInteractor.execute(new GetBikeCostInputData(bikeTime));
                    final double bikeCost = bikeCostVM.getBikeCostValue();

                    final SearchRecord record = new SearchRecord(
                            originPanel.getOriginText(),
                            originPanel.getDestinationText(),
                            bikeTime,
                            bikeCost,
                            walkTime
                    );

                    historyGateway.save(record);
                }
        );

        // ----------------- BUTTONS -----------------
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
}
