package view;

import interface_adapter.GetBikeTimeController;
import interface_adapter.GetBikeTimeViewModel;

import javax.swing.*;
import java.awt.*;

/**
 * Panel for displaying biking time and walking time.
 */
public class GetTimePanel extends JPanel {

    private final transient GetBikeTimeViewModel viewModel;
    private final transient GetBikeTimeController controller;

    private final JLabel bikeTimeLabel;
    private final JLabel walkTimeLabel;

    private final JButton backButton;
    private final JButton costButton;

    // Store walking time so CompareSummary can use it
    private double walkTimeValue = -1;

    /**
     * Constructs a GetTimePanel with the given view model and controller.
     *
     * @param viewModel the view model for bike time
     * @param controller the controller for fetching bike time
     */
    public GetTimePanel(GetBikeTimeViewModel viewModel,
                        GetBikeTimeController controller) {
        this.viewModel = viewModel;
        this.controller = controller;

        setLayout(new BorderLayout());

        final Font timeFont = new Font("SansSerif", Font.BOLD, 16);

        bikeTimeLabel = new JLabel(viewModel.getBikeTimeText(), SwingConstants.CENTER);
        bikeTimeLabel.setFont(timeFont);

        walkTimeLabel = new JLabel("Walk Time: -- minutes", SwingConstants.CENTER);
        walkTimeLabel.setFont(timeFont);

        backButton = new JButton("Back");
        costButton = new JButton("See Bike Cost");

        final JPanel center = new JPanel(new GridLayout(2, 1));
        center.add(bikeTimeLabel);
        center.add(walkTimeLabel);
        add(center, BorderLayout.CENTER);

        final JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(backButton);
        bottom.add(costButton);
        add(bottom, BorderLayout.SOUTH);
    }

    /**
     * Fetches bike time for the given coordinates.
     * @param originLat origin lat coord
     * @param originLon origin lon coord
     * @param destinationLat destination lat coord
     * @param destinationLon destination lon coord
     */
    public void requestBikeTime(double originLat,
                                double originLon,
                                double destinationLat,
                                double destinationLon) {
        controller.execute(originLat, originLon, destinationLat, destinationLon);
    }

    /** Updates the displayed bike time. */
    public void updateBikeTimeText() {
        bikeTimeLabel.setText(viewModel.getBikeTimeText());
    }

    /**
     * Set display text for walk time.
     * @param minutes walk time in minutes
     */
    public void setWalkTimeText(double minutes) {
        walkTimeValue = minutes;
        walkTimeLabel.setText(String.format("Walk Time: %.2f minutes", minutes));
    }

    /** Returns the stored walking time. */
    public double getWalkTimeValue() {
        return walkTimeValue;
    }

    /** Returns the back button. */
    public JButton getBackButton() {
        return backButton;
    }

    /** Returns the bike cost button. */
    public JButton getCostButton() {
        return costButton;
    }

    /** Returns the bike time in minutes from the view model. */
    public double getBikeTimeMinutes() {
        return viewModel.getBikeTimeValue();
    }
}
