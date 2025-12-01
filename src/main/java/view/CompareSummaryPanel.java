package view;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

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

        titleLabel = new JLabel("Comparison Summary", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));

        walkTimeLabel = new JLabel("Walk Time: -- minutes", SwingConstants.CENTER);
        walkTimeLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));

        bikeTimeLabel = new JLabel("Bike Time: -- minutes", SwingConstants.CENTER);
        bikeTimeLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));

        bikeCostLabel = new JLabel("Bike Cost: --", SwingConstants.CENTER);
        bikeCostLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));

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
