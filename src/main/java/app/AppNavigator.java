package app;

import javax.swing.*;
import java.awt.*;

/**
 * The helper class for switching views inside a CardLayout.
 *
 * The application stores panels inside a parent container "root",
 * and AppNavigator provides clean methods to show each screen.
 */
public class AppNavigator {

    private final JPanel root;
    private final CardLayout layout;

    public AppNavigator(JPanel root, CardLayout layout) {
        this.root = root;
        this.layout = layout;
    }

    /** Show the origin/destination entry screen. */
    public void showOrigin() {
        layout.show(root, "origin");
    }

    /** Show the bike time result screen. */
    public void showBikeTime() {
        layout.show(root, "bikeTime");
    }

    /** Show the bike cost screen (for future use). */
    public void showBikeCost() {
        layout.show(root, "bikeCost");
    }

    /**Show the search history screen.
     */
    public void showSearchHistory() {
        layout.show(root, "searchHistory");
    }

    /** Show the comparison summary screen (for future use). */
    public void showCompareSummary() {
        layout.show(root, "compare");
    }
}
