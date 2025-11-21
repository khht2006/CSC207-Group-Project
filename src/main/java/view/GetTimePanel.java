package view;

import interface_adapter.GetBikeTimeController;
import interface_adapter.GetBikeTimeViewModel;

import javax.swing.*;
import java.awt.*;

/**
 * Panel for displaying biking time.
 */
public class GetTimePanel extends JPanel {

    private final GetBikeTimeViewModel viewModel;
    private final GetBikeTimeController controller;

    private final JLabel bikeTimeLabel;
    private final JButton backButton;
    private final JButton compareButton;

    public GetTimePanel(GetBikeTimeViewModel viewModel,
                        GetBikeTimeController controller) {
        this.viewModel = viewModel;
        this.controller = controller;

        setLayout(new BorderLayout());

        bikeTimeLabel = new JLabel(viewModel.getBikeTimeText(), SwingConstants.CENTER);
        bikeTimeLabel.setFont(new Font("SansSerif", Font.BOLD, 16));

        backButton = new JButton("Back");
        compareButton = new JButton("Compare Summary");

        add(bikeTimeLabel, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(backButton);
        bottom.add(compareButton);
        add(bottom, BorderLayout.SOUTH);
    }

    // It will fetching bike time for the given coordinates.
    public void requestBikeTime(double originLat,
                                double originLon,
                                double destinationLat,
                                double destinationLon) {
        controller.execute(originLat, originLon, destinationLat, destinationLon);
    }

    public void updateBikeTimeText() {
        bikeTimeLabel.setText(viewModel.getBikeTimeText());
    }

    public JButton getBackButton() {
        return backButton;
    }

    public JButton getCompareButton() {
        return compareButton;
    }
}
