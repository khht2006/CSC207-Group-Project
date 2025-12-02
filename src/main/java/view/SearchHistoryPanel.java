package view;

import java.awt.*;
import java.util.List;

import javax.swing.*;

import entity.SearchRecord;

/**
 * Panel for displaying previously saved search history records.
 */
public class SearchHistoryPanel extends JPanel {

    private final JTextArea area;
    private final JButton backButton;

    /**
     * Creates a new {@code SearchHistoryPanel}.
     */
    public SearchHistoryPanel() {
        setLayout(new BorderLayout());

        final JLabel title = new JLabel("Search History", SwingConstants.CENTER);
        final int titleFontSize = 18;
        title.setFont(new Font("SansSerif", Font.BOLD, titleFontSize));

        area = new JTextArea();
        area.setEditable(false);

        backButton = new JButton("Back");

        add(title, BorderLayout.NORTH);
        add(new JScrollPane(area), BorderLayout.CENTER);
        add(backButton, BorderLayout.SOUTH);
    }

    /**
     * Displays a message indicating that no history is available.
     */
    public void setNoHistoryMessage() {
        area.setText("No history yet.");
    }

    /**
     * Displays all saved search history records in a formatted list.
     *
     * @param records the list of records to show
     */
    public void setHistory(List<SearchRecord> records) {
        final StringBuilder sb = new StringBuilder();

        for (SearchRecord r : records) {

            final String walk = String.format("%.1f", r.getWalkTime());
            final String bike = String.format("%.1f", r.getBikeTime());
            final String cost = String.format("%.2f", r.getBikeCost());

            sb.append(r.getOrigin())
                    .append(" → ")
                    .append(r.getDestination())
                    .append("\n")
                    .append("Walk: ").append(walk).append(" min")
                    .append(" | Bike: ").append(bike).append(" min")
                    .append(" | Cost: $").append(cost)
                    .append("\n")
                    .append("------------------------------------------------\n");
        }

        area.setText(sb.toString());
    }

    /**
     * Returns the back button for navigation.
     *
     * @return the back button
     */
    public JButton getBackButton() {
        return backButton;
    }
}
