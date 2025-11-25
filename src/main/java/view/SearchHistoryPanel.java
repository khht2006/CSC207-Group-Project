package view;

import usecase.search_history.SearchRecord;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class SearchHistoryPanel extends JPanel {

    private final DefaultListModel<String> listModel = new DefaultListModel<>();
    private final JList<String> historyList = new JList<>(listModel);
    private final JButton backButton = new JButton("Back");

    public SearchHistoryPanel() {
        super(new BorderLayout(10, 10));

        JLabel title = new JLabel("Search History", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);

        historyList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {

                JLabel label = (JLabel) super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);

                label.setVerticalAlignment(SwingConstants.TOP);
                label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                label.setFont(new Font("SansSerif", Font.PLAIN, 14));

                if (!isSelected) {
                    label.setBackground(new Color(250, 250, 250));
                }
                return label;
            }
        });

        add(new JScrollPane(historyList), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(backButton);
        add(bottom, BorderLayout.SOUTH);
    }

    /**
     * Loads the search history.
     */
    public void setHistory(List<SearchRecord> records) {
        listModel.clear();
        for (SearchRecord r : records) {

            String item =
                    "<html>"
                            + "<b>Origin:</b> " + r.getOrigin() + "<br>"
                            + "<b>Destination:</b> " + r.getDestination() + "<br>"
                            + "<b>Bike Time:</b> " + r.getBikeTime() + " min<br>"
                            + "<b>Bike Cost:</b> $" + r.getBikeCost() + "<br>"
                            + "<b>Walk Time:</b> " + r.getWalkTime() + " min<br>"
                            + "<hr style='border:0;border-top:1px solid #ccc;margin:8px 0;'>"
                            + "</html>";

            listModel.addElement(item);
        }
    }

    public void setNoHistoryMessage() {
        listModel.clear();
        listModel.addElement("<html><i>No history yet.</i></html>");
    }

    public JButton getBackButton() {
        return backButton;
    }
}
