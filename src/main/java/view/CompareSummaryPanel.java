package view;

import interface_adapter.CompareViewModel;
import javax.swing.*;
import java.awt.*;

// The View for when the user is comparing time and cost of each public transit option.
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

        titleLabel = new JLabel("Comparison Summary", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));

        walkTimeLabel = new JLabel("Walk time: --", SwingConstants.CENTER);
        bikeTimeLabel = new JLabel("Bike time: --", SwingConstants.CENTER);
        bikeCostLabel = new JLabel("Bike cost: -- for -- distance", SwingConstants.CENTER);

        backButton = new JButton("Back");

        JPanel center = new JPanel(new GridLayout(3, 1, 5, 5));

        center.add(walkTimeLabel);
        center.add(bikeTimeLabel);
        center.add(bikeCostLabel);

        add(titleLabel, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);
        add(backButton, BorderLayout.SOUTH);
    }

    public void updateSummary() {
        walkTimeLabel.setText(viewModel.getWalkTime());
        bikeTimeLabel.setText(viewModel.getBikeTime());
        bikeCostLabel.setText(viewModel.getBikeCost());
    }
}
