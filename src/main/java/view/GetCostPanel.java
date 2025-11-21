package view;

import interface_adapter.GetBikeCostViewModel;

import javax.swing.*;
import java.awt.*;

/**
 * Panel for displaying biking cost.
 */
public class GetCostPanel extends JPanel {

    private final GetBikeCostViewModel viewModel;
    private final JLabel bikeCostLabel;
    private final JButton backButton;
    private final JButton compareButton;

    public GetCostPanel(GetBikeCostViewModel viewModel) {
        this.viewModel = viewModel;
        setLayout(new BorderLayout());

        setLayout(new BorderLayout());

        bikeCostLabel = new JLabel("Biking cost:", SwingConstants.CENTER);
        bikeCostLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        add(bikeCostLabel, BorderLayout.CENTER);

        backButton = new JButton("Back");
        compareButton = new JButton("Compare Summary");

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(backButton);
        bottom.add(compareButton);
        add(bottom, BorderLayout.SOUTH);
    }
    public void updateBikeCostText() {
        bikeCostLabel.setText("Biking cost: " + viewModel.getBikeCostText());
    }

    public JButton getBackButton() {
        return backButton;
    }
    public JButton getCompareButton() {
        return compareButton;
    }
}
