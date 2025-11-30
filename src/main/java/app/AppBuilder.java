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

    private AppBuilder() {}

    public static JFrame build() {

        new java.io.File("search_history.txt").delete();

        ApiFetcher apiFetcher = new ApiFetcher();
        GeocodeLocationInteractor geocode = new GeocodeLocationInteractor(apiFetcher);
        SearchHistoryData historyGateway = new SearchHistoryGateway();

        WalkRouteInteractor walkRoute = new WalkRouteInteractor(apiFetcher);

        GetBikeTimeViewModel bikeTimeVM = new GetBikeTimeViewModel();
        GetBikeTimePresenter bikeTimePresenter = new GetBikeTimePresenter(bikeTimeVM);
        BikeRouteInteractor bikeRoute = new BikeRouteInteractor(apiFetcher, bikeTimePresenter);
        GetBikeTimeController bikeTimeController = new GetBikeTimeController(bikeRoute);
        GetTimePanel bikeTimePanel = new GetTimePanel(bikeTimeVM, bikeTimeController);

        GetBikeCostViewModel bikeCostVM = new GetBikeCostViewModel();
        GetBikeCostPresenter bikeCostPresenter = new GetBikeCostPresenter(bikeCostVM);
        GetBikeCostInteractor bikeCostInteractor = new GetBikeCostInteractor(bikeCostPresenter);
        GetBikeCostController bikeCostController =
                new GetBikeCostController(bikeCostInteractor, bikeTimeVM);
        GetCostPanel bikeCostPanel = new GetCostPanel(bikeCostVM);

        CompareViewModel compareVM = new CompareViewModel();
        CompareSummaryPanel comparePanel = new CompareSummaryPanel(compareVM);

        OriginalDestinationPanel originPanel = new OriginalDestinationPanel();
        SearchHistoryPanel historyPanel = new SearchHistoryPanel();

        SearchHistoryViewModel historyVM = new SearchHistoryViewModel();
        SearchHistoryPresenter historyPresenter = new SearchHistoryPresenter(historyVM);
        SearchHistoryInteractor historyInteractor =
                new SearchHistoryInteractor(historyGateway, historyPresenter);
        SearchHistoryController historyController =
                new SearchHistoryController(historyInteractor);

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

        bikeTimePanel.getCostButton().addActionListener(e -> {
            layout.show(root, "bikeCost");
            bikeCostController.calculateCost();
            bikeCostPanel.updateBikeCostText();
        });

        bikeCostPanel.getCompareButton().addActionListener(e -> {

            double bikeT = bikeTimeVM.getBikeTimeValue();
            double walkT = bikeTimePanel.getWalkTimeValue();

            compareVM.setWalkTimeText(walkT);
            compareVM.setBikeTimeText(bikeT);
            compareVM.setBikeCostText(bikeCostVM.getBikeCostText());

            comparePanel.updateSummary();
            layout.show(root, "compare");
        });

        bikeTimePanel.getBackButton().addActionListener(e -> layout.show(root, "origin"));
        bikeCostPanel.getBackButton().addActionListener(e -> layout.show(root, "bikeTime"));
        comparePanel.getBackButton().addActionListener(e -> layout.show(root, "bikeCost"));

        originPanel.getViewHistoryButton().addActionListener(e -> {
            historyController.execute();
            var records = historyVM.getHistory();
            if (records == null || records.isEmpty()) historyPanel.setNoHistoryMessage();
            else historyPanel.setHistory(records);
            layout.show(root, "searchHistory");
        });

        historyPanel.getBackButton().addActionListener(e -> layout.show(root, "origin"));

        JFrame frame = new JFrame("Grapes Trip Planner");
        frame.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        frame.add(root, BorderLayout.CENTER);
        frame.setSize(600, 300);
        frame.setLocationRelativeTo(null);

        return frame;
    }
}
