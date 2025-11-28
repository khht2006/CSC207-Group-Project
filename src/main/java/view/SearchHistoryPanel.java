package view;

import usecase.search_history.SearchRecord;

import javax.swing.*;
import java.awt.*;
import java.util.List;


/**
 * A panel that displays saved search history records.
 */
public class SearchHistoryPanel extends JPanel {

    private final JTextArea area;
    private final JButton backButton;

    public SearchHistoryPanel() {
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Search History", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 18));

        area = new JTextArea();
        area.setEditable(false);

        backButton = new JButton("Back");

        add(title, BorderLayout.NORTH);
        add(new JScrollPane(area), BorderLayout.CENTER);
        add(backButton, BorderLayout.SOUTH);
    }

    /** Shows a message when no history exists. */
    public void setNoHistoryMessage() {
        area.setText("No history yet.");
    }

    /** Displays all saved search history records in a formatted list. */
    public void setHistory(List<SearchRecord> records) {
        StringBuilder sb = new StringBuilder();

        for (SearchRecord r : records) {
            sb.append(
                            r.getOrigin())
                    .append(" → ")
                    .append(r.getDestination())
                    .append("\n")
                    .append("Walk: ").append(r.getWalkTime()).append(" min")
                    .append(" | Bike: ").append(r.getBikeTime()).append(" min")
                    .append(" | Cost: $").append(r.getBikeCost())
                    .append("\n")
                    .append("------------------------------------------------\n");
        }

        area.setText(sb.toString());
    }

    public JButton getBackButton() {
        return backButton;
    }
}
