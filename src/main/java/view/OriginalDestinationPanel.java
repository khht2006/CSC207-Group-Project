package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.event.DocumentListener;
import java.util.List;

public class OriginalDestinationPanel extends JPanel {

    private final JTextField originField;
    private final JTextField destinationField;

    private final JButton swapButton;
    private final JButton continueButton;
    private final JButton viewHistoryButton;
    private final JButton deleteHistoryButton;

    // NEW – suggestion list GUI
    private final JList<String> suggestionList;
    private final DefaultListModel<String> suggestionModel;

    public enum ActiveField { ORIGIN, DESTINATION, NONE }
    private ActiveField activeField = ActiveField.NONE;

    public OriginalDestinationPanel() {

        setLayout(new BorderLayout());

        // ---------- Title ----------
        JLabel title = new JLabel("Trip Planner – Enter Locations", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        add(title, BorderLayout.NORTH);

        // ---------- Main Center Layout ----------
        JPanel center = new JPanel(new GridLayout(1, 2));

        // LEFT: suggestions panel
        JPanel left = new JPanel(new BorderLayout());
        left.add(new JLabel("Suggestions"), BorderLayout.NORTH);

        suggestionModel = new DefaultListModel<>();
        suggestionList = new JList<>(suggestionModel);
        left.add(new JScrollPane(suggestionList), BorderLayout.CENTER);

        // RIGHT: input panel
        JPanel right = new JPanel(new GridLayout(4, 1));
        right.add(new JLabel("Current location (origin):"));
        originField = new JTextField();
        right.add(originField);

        right.add(new JLabel("Destination:"));
        destinationField = new JTextField();
        right.add(destinationField);

        center.add(left);
        center.add(right);
        add(center, BorderLayout.CENTER);

        // ---------- Buttons ----------
        swapButton = new JButton("Swap");
        continueButton = new JButton("Continue");
        viewHistoryButton = new JButton("View History");
        deleteHistoryButton = new JButton("Delete History");

        JPanel bottom = new JPanel(new FlowLayout());
        bottom.add(swapButton);
        bottom.add(viewHistoryButton);
        bottom.add(deleteHistoryButton);
        bottom.add(continueButton);

        add(bottom, BorderLayout.SOUTH);

        // Track which field is active (needed for suggestion selection)
        originField.addCaretListener(e -> activeField = ActiveField.ORIGIN);
        destinationField.addCaretListener(e -> activeField = ActiveField.DESTINATION);
    }

    // ---------------- Accessors -----------------

    public String getOriginText() { return originField.getText(); }
    public String getDestinationText() { return destinationField.getText(); }

    public void setOriginText(String text) { originField.setText(text); }
    public void setDestinationText(String text) { destinationField.setText(text); }

    public ActiveField getActiveField() { return activeField; }

    // Suggestion utilities
    public void updateSuggestions(List<String> suggestions) {
        suggestionModel.clear();
        for (String s : suggestions) {
            suggestionModel.addElement(s);
        }
    }

    public String getSelectedSuggestionText() {
        return suggestionList.getSelectedValue();
    }

    // ---------------- Listener Hooks -----------------

    public void addSwapListener(ActionListener listener) {
        swapButton.addActionListener(listener);
    }

    public void addContinueListener(ActionListener listener) {
        continueButton.addActionListener(listener);
    }

    public void addOriginDocumentListener(DocumentListener listener) {
        originField.getDocument().addDocumentListener(listener);
    }

    public void addDestinationDocumentListener(DocumentListener listener) {
        destinationField.getDocument().addDocumentListener(listener);
    }

    public void addSuggestionSelectionListener(Runnable r) {
        suggestionList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                r.run();
            }
        });
    }

    public JButton getViewHistoryButton() { return viewHistoryButton; }
    public JButton getDeleteHistoryButton() { return deleteHistoryButton; }
}
