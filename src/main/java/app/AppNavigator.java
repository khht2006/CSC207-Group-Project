package app;

import java.awt.CardLayout;
import javax.swing.JPanel;

/**
 * AppNavigator provides clean UI navigation between screens
 * managed inside a {@link CardLayout}.
 */
public class AppNavigator {

    private final JPanel root;
    private final CardLayout layout;

    /**
     * Creates a new navigator.
     *
     * @param root   root panel that holds all screens
     * @param layout card layout managing the screens
     */
    public AppNavigator(JPanel root, CardLayout layout) {
        this.root = root;
        this.layout = layout;
    }

    /** Switches to the origin screen. */
    public void showOrigin() {
        layout.show(root, AppBuilder.ORIGIN);
    }

    /** Switches to the bike time screen. */
    public void showBikeTime() {
        layout.show(root, AppBuilder.BIKE_TIME);
    }

    /** Switches to the bike cost screen. */
    public void showBikeCost() {
        layout.show(root, AppBuilder.BIKE_COST);
    }

    /** Switches to the search history screen. */
    public void showSearchHistory() {
        layout.show(root, AppBuilder.SEARCH_HISTORY);
    }

    /** Switches to the comparison summary screen. */
    public void showCompareSummary() {
        layout.show(root, AppBuilder.COMPARE);
    }
}
