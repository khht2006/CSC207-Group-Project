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

        JLabel header = new JLabel("Route Time Summary", SwingConstants.CENTER);
        header.setFont(new Font("SansSerif", Font.BOLD, 22));
        header.setBorder(BorderFactory.createEmptyBorder(20, 10, 30, 10));
        add(header, BorderLayout.NORTH);

        Font timeFont = new Font("SansSerif", Font.BOLD, 16);

        bikeTimeLabel = new JLabel(viewModel.getBikeTimeText());
        bikeTimeLabel.setFont(timeFont);

        walkTimeLabel = new JLabel("Walk Time: -- minutes");
        walkTimeLabel.setFont(timeFont);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 30, 40, 30));

        bikeTimeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        walkTimeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        centerPanel.add(bikeTimeLabel);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(walkTimeLabel);

        add(centerPanel, BorderLayout.CENTER);

        backButton = new JButton("Back");
        costButton = new JButton("See Bike Cost");

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
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
