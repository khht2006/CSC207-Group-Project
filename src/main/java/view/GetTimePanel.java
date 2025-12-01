package view;

import interface_adapter.GetBikeTimeController;
import interface_adapter.GetBikeTimeViewModel;

import javax.swing.*;
import java.awt.*;

/**
 * Panel for displaying biking time and walking time.
 */
public class GetTimePanel extends JPanel {

    private final GetBikeTimeViewModel viewModel;
    private final GetBikeTimeController controller;

    private final JLabel bikeTimeLabel;
    private final JLabel walkTimeLabel;

    private final JButton backButton;
    private final JButton costButton;

    // Store walking time so CompareSummary can use it
    private double walkTimeValue = -1;

    public GetTimePanel(GetBikeTimeViewModel viewModel,
                        GetBikeTimeController controller) {
        this.viewModel = viewModel;
        this.controller = controller;

        setLayout(new BorderLayout());

        Font timeFont = new Font("SansSerif", Font.BOLD, 16);

        bikeTimeLabel = new JLabel(viewModel.getBikeTimeText(), SwingConstants.CENTER);
        bikeTimeLabel.setFont(timeFont);

        walkTimeLabel = new JLabel("Walk Time: -- minutes", SwingConstants.CENTER);
        walkTimeLabel.setFont(timeFont);

        backButton = new JButton("Back");
        costButton = new JButton("See Bike Cost");

        JPanel center = new JPanel(new GridLayout(2, 1));
        center.add(bikeTimeLabel);
        center.add(walkTimeLabel);
        add(center, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(backButton);
        bottom.add(costButton);
        add(bottom, BorderLayout.SOUTH);
    }

    /** Fetches bike time for the given coordinates. */
    public void requestBikeTime(double originLat, double originLon,
                                double destinationLat, double destinationLon,
                                String destinationName) {
        controller.execute(originLat, originLon, destinationLat, destinationLon, destinationName);
    }

    /** Updates the displayed bike time. */
    public void updateBikeTimeText() {
        bikeTimeLabel.setText(viewModel.getBikeTimeText());
    }

    public void setWalkTimeText(double minutes) {
        walkTimeValue = minutes;
        walkTimeLabel.setText(String.format("Walk Time: %.1f minutes", minutes));
    }

    public double getWalkTimeValue() {
        return walkTimeValue;
    }

    public JButton getBackButton() {
        return backButton;
    }

    public JButton getCostButton() {
        return costButton;
    }

    public double getBikeTimeMinutes() {
        return viewModel.getBikeTimeValue();
    }
}
