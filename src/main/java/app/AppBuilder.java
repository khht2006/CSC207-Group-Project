package app;

import api.ApiFetcher;
import interface_adapter.*;
import usecase.*;
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

        ApiFetcher apiFetcher = new ApiFetcher();
        GeocodeLocationInteractor geocode = new GeocodeLocationInteractor(apiFetcher);

        // biketime usecase
        GetBikeTimeViewModel bikeTimeVM = new GetBikeTimeViewModel();
        GetBikeTimePresenter bikeTimePresenter = new GetBikeTimePresenter(bikeTimeVM);
        BikeRouteInteractor bikeRoute = new BikeRouteInteractor(apiFetcher, bikeTimePresenter);
        GetBikeTimeController bikeTimeController = new GetBikeTimeController(bikeRoute);
        GetTimePanel bikeTimePanel = new GetTimePanel(bikeTimeVM, bikeTimeController);

        // Original Destination panel
        OriginalDestinationPanel originPanel = new OriginalDestinationPanel();

        // navigation
        CardLayout layout = new CardLayout();
        JPanel root = new JPanel(layout);
        AppNavigator navigator = new AppNavigator(root, layout);

        root.add(originPanel, "origin");
        root.add(bikeTimePanel, "bikeTime");

        // Back button that switches back to the main screen
        bikeTimePanel.getBackButton().addActionListener(e -> navigator.showOrigin());

        // Continue button that leads to bike time view
        new OriginalDestinationController(
                originPanel,
                geocode,
                (origin, dest) -> {
                    navigator.showBikeTime();
                    bikeTimePanel.requestBikeTime(
                            origin.getLatitude(), origin.getLongitude(),
                            dest.getLatitude(), dest.getLongitude()
                    );
                    bikeTimePanel.updateBikeTimeText();
                }
        );

        JFrame frame = new JFrame("Grapes Trip Planner");
        frame.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        frame.add(root, BorderLayout.CENTER);
        frame.setSize(650, 350);
        frame.setLocationRelativeTo(null);

        return frame;
    }
}
