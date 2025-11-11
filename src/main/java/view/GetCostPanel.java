package main.java.view;

import javax.swing.*;
import java.awt.*;

/**
 * Panel for displaying the cost comparison between Walking and Biking routes.
 */
public class GetCostPanel extends JPanel {

    private final JLabel titleLabel;
    //    private final JLabel walkCostLabel;
    private final JLabel bikeCostLabel;
    private final JButton backButton;

    public GetCostPanel() {
        titleLabel = new JLabel("Cost Comparison", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));

//        walkCostLabel = new JLabel("Walking cost: $ 0", SwingConstants.CENTER);
        bikeCostLabel = new JLabel("Biking cost: --", SwingConstants.CENTER);

        backButton = new JButton("Back to Compare Summary");

        setLayout(new BorderLayout(10, 10));
        JPanel costPanel = new JPanel(new GridLayout(3, 1, 5, 5));
//        costPanel.add(walkCostLabel);
        costPanel.add(bikeCostLabel);

        add(titleLabel, BorderLayout.NORTH);
        add(costPanel, BorderLayout.CENTER);
        add(backButton, BorderLayout.SOUTH);
    }
    /**
     * Update displayed costs (will be called by presenter or controller)
     *
     * @return
     */
    public JButton updateCosts(double walkCost, double bikeCost) {
        bikeCostLabel.setText(String.format("Biking cost: $%.2f", bikeCost));
        return null;
    }
}
