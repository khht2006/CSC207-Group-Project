package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import interface_adapter.GetBikeTimeController;
import interface_adapter.GetBikeTimeViewModel;

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

        Font timeFont = new Font("SansSerif", Font.BOLD, 16);

        bikeTimeLabel = new JLabel(viewModel.getBikeTimeText(), JLabel.CENTER);
        bikeTimeLabel.setFont(timeFont);

        walkTimeLabel = new JLabel("Walk Time: -- minutes", JLabel.CENTER);
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

    /**
     * Fetches bike time for the given coordinates.
     *
     * @param originLat origin latitude
     * @param originLon origin longitude
     * @param destinationLat destination latitude
     * @param destinationLon destination longitude
     */
    public void requestBikeTime(double originLat, double originLon,
                                double destinationLat, double destinationLon) {
        controller.execute(originLat, originLon, destinationLat, destinationLon);
    }

    /** Updates the displayed bike time. */
    public void updateBikeTimeText() {
        bikeTimeLabel.setText(viewModel.getBikeTimeText());
    }

    /**
     * Updates the displayed walking time.
     *
     * @param minutes walking time in minutes
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
