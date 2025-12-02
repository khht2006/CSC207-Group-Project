package view;

import interface_adapter.geocode.GeocodeController; // Import the controller
import interface_adapter.geocode.GeocodeViewModel;
import interface_adapter.original_destination.OriginalDestinationViewModel;

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

    private final JList<String> suggestionList;
    private final DefaultListModel<String> suggestionModel;

    public enum ActiveField { ORIGIN, DESTINATION, NONE }
    private ActiveField activeField = ActiveField.NONE;

    public OriginalDestinationPanel(GeocodeController geocodeController) { // Updated constructor

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

        // Setup debounced listeners
        addDebouncedSuggestionListeners(geocodeController);
    }

    //Encapsulates debouncing logic
    private void addDebouncedSuggestionListeners(GeocodeController geocodeController) {
        Runnable fetchSuggestions = () -> {
            String text;
            if (getActiveField() == ActiveField.ORIGIN) {
                text = getOriginText();
            } else {
                text = getDestinationText();
            }
            if (!text.isBlank()) {
                geocodeController.search(text, 5);
            }
        };

        // Timer to delay geocode requests until the user stops typing
        final Timer suggestionTimer = new Timer(300, e -> fetchSuggestions.run());
        suggestionTimer.setRepeats(false);

        DocumentListener suggestionListener = new DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                suggestionTimer.restart();
            }
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                suggestionTimer.restart();
            }
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                suggestionTimer.restart();
            }
        };

        originField.getDocument().addDocumentListener(suggestionListener);
        destinationField.getDocument().addDocumentListener(suggestionListener);
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

    public void addSuggestionSelectionListener(Runnable r) {
        suggestionList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                r.run();
            }
        });
    }

    public void observeViewModel(OriginalDestinationViewModel viewModel) {
        viewModel.addPropertyChangeListener(evt -> {
            switch (evt.getPropertyName()) {
                case "originText" -> setOriginText((String) evt.getNewValue());
                case "destinationText" -> setDestinationText((String) evt.getNewValue());
                case "errorMessage" -> {
                    String error = (String) evt.getNewValue();
                    if (error != null) {
                        JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
    }

    public void observeGeocodeViewModel(GeocodeViewModel viewModel) {
        viewModel.addPropertyChangeListener(evt -> {
            if ("suggestions".equals(evt.getPropertyName())) {
                @SuppressWarnings("unchecked")
                List<String> newSuggestions = (List<String>) evt.getNewValue();
                updateSuggestions(newSuggestions);
            }
        });
    }


    public JButton getViewHistoryButton() { return viewHistoryButton; }
    public JButton getDeleteHistoryButton() { return deleteHistoryButton; }
}
