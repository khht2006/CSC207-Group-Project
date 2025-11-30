package view;

import java.awt.*;

import javax.swing.*;

import interface_adapter.CompareViewModel;

/**
 * The View for the Compare Summary use case.
 */
public class CompareSummaryPanel extends JPanel {
    private final CompareViewModel viewModel;
    private final JLabel titleLabel;
    private final JLabel walkTimeLabel;
    private final JLabel bikeTimeLabel;
    private final JLabel bikeCostLabel;

    private final JButton backButton;

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

        backButton = new JButton("Back");

        final int centerRows = 3;
        final int centerCols = 1;
        final int centerGap = 5;

        final JPanel center = new JPanel(new GridLayout(centerRows, centerCols, centerGap, centerGap));
        center.add(walkTimeLabel);
        center.add(bikeTimeLabel);
        center.add(bikeCostLabel);

        add(titleLabel, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);
        add(backButton, BorderLayout.SOUTH);
    }

    /**
     * Update compare summary text.
     */
    public void updateSummary() {
        walkTimeLabel.setText(viewModel.getWalkTime());
        bikeTimeLabel.setText(viewModel.getBikeTime());
        final String cost = viewModel.getBikeCost();
        bikeCostLabel.setText("Bike Cost: " + cost);
    }

    public JButton getBackButton() {
        return backButton;
    }
}
