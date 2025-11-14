package main.java.view;

import main.java.interface_adapter.GetBikeCostViewModel;

import javax.swing.*;
import java.awt.*;

/**
 * Panel for displaying the cost comparison between Walking and Biking routes.
 */
public class GetCostPanel extends JPanel {

    private final GetBikeCostViewModel viewModel;
    private final JLabel bikeCostLabel;
    private final JButton backButton;
    private final JButton compareButton;

    public GetCostPanel(GetBikeCostViewModel viewModel) {
        this.viewModel = viewModel;
        setLayout(new BorderLayout());

        bikeCostLabel = new JLabel("Biking cost:", SwingConstants.CENTER);

        backButton = new JButton("Back");

        compareButton = new JButton("Compare Summary");

        add(bikeCostLabel, BorderLayout.CENTER);
        add(backButton, BorderLayout.SOUTH);
        add(compareButton, BorderLayout.EAST);
    }
    public JButton getBackButton() {
        return backButton;
    }
    public JButton getCompareButton() {
        return compareButton;
    }
}
