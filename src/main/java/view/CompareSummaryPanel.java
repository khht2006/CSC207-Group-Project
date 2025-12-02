package view;

import interface_adapter.CompareViewModel;
import javax.swing.*;
import java.awt.*;

/**
 * The View for the Compare Summary use case.
 */
public class CompareSummaryPanel extends JPanel {
    private final transient CompareViewModel viewModel;
    private final JLabel titleLabel;
    private final JLabel walkTimeLabel;
    private final JLabel bikeTimeLabel;
    private final JLabel bikeCostLabel;
    private final JLabel diffInMinutesLabel;
    private final JButton backButton;

    /**
     * Constructs the CompareSummaryPanel with the given view model.
     *
     * @param viewModel the view model providing summary data
     */
    public CompareSummaryPanel(CompareViewModel viewModel) {
        this.viewModel = viewModel;
        setLayout(new BorderLayout());

        final int titleFontSize = 18;
        final int textFontSize = 14;
        final String textFont = "SansSerif";
        titleLabel = new JLabel("Comparison Summary", SwingConstants.CENTER);
        titleLabel.setFont(new Font(textFont, Font.BOLD, titleFontSize));

        walkTimeLabel = new JLabel("Walk Time: -- minutes", SwingConstants.CENTER);
        walkTimeLabel.setFont(new Font(textFont, Font.PLAIN, textFontSize));

        bikeTimeLabel = new JLabel("Bike Time: -- minutes", SwingConstants.CENTER);
        bikeTimeLabel.setFont(new Font(textFont, Font.PLAIN, textFontSize));

        bikeCostLabel = new JLabel("Bike Cost: --", SwingConstants.CENTER);
        bikeCostLabel.setFont(new Font(textFont, Font.PLAIN, textFontSize));

        diffInMinutesLabel = new JLabel("Time saved by taking a bike: --", SwingConstants.CENTER);
        diffInMinutesLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));

        backButton = new JButton("Back");

        final JPanel center = new JPanel(new GridLayout(4, 1, 5, 5));
        center.add(walkTimeLabel);
        center.add(bikeTimeLabel);
        center.add(bikeCostLabel);
        center.add(diffInMinutesLabel);

        add(titleLabel, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);
        add(backButton, BorderLayout.SOUTH);
    }

    /**
     * Update compare summary text.
     */
    /** Updates the summary labels with current values from the view model. */
    public void updateSummary() {
        walkTimeLabel.setText(viewModel.getWalkTime());
        bikeTimeLabel.setText(viewModel.getBikeTime());
        final String cost = viewModel.getBikeCost();
        bikeCostLabel.setText("Bike Cost: " + cost);
        diffInMinutesLabel.setText(viewModel.getDiffInMinutes());
    }

    /** Returns the back button. */
    public JButton getBackButton() {
        return backButton;
    }
}
