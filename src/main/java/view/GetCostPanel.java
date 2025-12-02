package view;

import java.awt.*;

import javax.swing.*;

import interface_adapter.bike_cost.GetBikeCostViewModel;

/**
 * Panel for displaying the computed biking cost.
 * This view shows the formatted bike cost to the user. It also provides
 * navigation buttons for going back to bike and walk time or viewing the comparison summary.
 */
public class GetCostPanel extends JPanel {

    private final GetBikeCostViewModel viewModel;
    private final JLabel bikeCostLabel;
    private final JButton backButton;
    private final JButton compareButton;

    /**
     * Creates a new {@code GetCostPanel} bound to the given view model.
     *
     * @param viewModel the view model containing the displayed bike cost text
     */
    public GetCostPanel(GetBikeCostViewModel viewModel) {
        this.viewModel = viewModel;
        setLayout(new BorderLayout());

        final int textFontSize = 16;
        bikeCostLabel = new JLabel("Biking cost:", SwingConstants.CENTER);
        bikeCostLabel.setFont(new Font("SansSerif", Font.BOLD, textFontSize));
        add(bikeCostLabel, BorderLayout.CENTER);

        backButton = new JButton("Back");
        compareButton = new JButton("Compare Summary");

        final JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(backButton);
        bottom.add(compareButton);
        add(bottom, BorderLayout.SOUTH);
    }

    /**
     * Updates the displayed biking cost based on the view model's text.
     */
    public void updateBikeCostText() {
        bikeCostLabel.setText("Biking cost: " + viewModel.getBikeCostText());
    }

    /**
     * Returns the back button for navigation.
     *
     * @return the back button
     */
    public JButton getBackButton() {
        return backButton;
    }

    /**
     * Returns the button that navigates to the comparison summary view.
     *
     * @return the compare summary button
     */
    public JButton getCompareButton() {
        return compareButton;
    }
}
