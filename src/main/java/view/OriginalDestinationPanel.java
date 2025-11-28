package view;

import javax.swing.*;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Objects;

/**
 * Panel where user enters:
 * - current location (origin)
 * - destination
 *
 * Also displays a list of text suggestions (addresses/place names).
 * The panel is "dumb": it does not call APIs itself.
 */
public class OriginalDestinationPanel extends JPanel {

    public enum ActiveField {
        ORIGIN,
        DESTINATION,
        NONE
    }

    private final JTextField originField;
    private final JTextField destinationField;
    private final JButton continueButton;
    private final JButton swapButton;
    private final JButton viewHistoryButton;

    private final DefaultListModel<String> suggestionListModel;
    private final JList<String> suggestionList;

    private ActiveField activeField = ActiveField.NONE;

    public OriginalDestinationPanel() {
        super(new BorderLayout(10, 10));

        JLabel title = new JLabel("Trip Planner - Enter Locations", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        this.add(title, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JLabel originLabel = new JLabel("Current location (origin):");
        originField = new JTextField();

        JLabel destinationLabel = new JLabel("Destination:");
        destinationField = new JTextField();

        // Row 0: origin label
        gbc.gridx = 0;
        gbc.gridy = 0;
        centerPanel.add(originLabel, gbc);

        // Row 1: origin field
        gbc.gridy = 1;
        centerPanel.add(originField, gbc);

        // Row 2: destination label
        gbc.gridy = 2;
        centerPanel.add(destinationLabel, gbc);

        // Row 3: destination field
        gbc.gridy = 3;
        centerPanel.add(destinationField, gbc);

        this.add(centerPanel, BorderLayout.CENTER);

        // Suggestion list on the left
        suggestionListModel = new DefaultListModel<>();
        suggestionList = new JList<>(suggestionListModel);
        suggestionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane suggestionScroll = new JScrollPane(suggestionList);
        suggestionScroll.setBorder(BorderFactory.createTitledBorder("Suggestions"));

        this.add(suggestionScroll, BorderLayout.WEST);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        swapButton = new JButton("Swap");
        continueButton = new JButton("Continue");
        continueButton.setEnabled(false);

        viewHistoryButton = new JButton("View Search History");

        bottomPanel.add(swapButton);
        bottomPanel.add(continueButton);
        bottomPanel.add(viewHistoryButton);
        this.add(bottomPanel, BorderLayout.SOUTH);

        // Track which field is active (for applying suggestions)
        originField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                activeField = ActiveField.ORIGIN;
            }
        });
        destinationField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                activeField = ActiveField.DESTINATION;
            }
        });

        // Enable "Continue" only when both fields are non-empty
        DocumentListener docListener = new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                updateContinueEnabled();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                updateContinueEnabled();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                updateContinueEnabled();
            }
        };
        originField.getDocument().addDocumentListener(docListener);
        destinationField.getDocument().addDocumentListener(docListener);
    }

    // --- Public API for controller -----------------------------------------

    public String getOriginText() {
        return originField.getText();
    }

    public void setOriginText(String text) {
        originField.setText(text == null ? "" : text);
    }

    public String getDestinationText() {
        return destinationField.getText();
    }

    public void setDestinationText(String text) {
        destinationField.setText(text == null ? "" : text);
    }

    public ActiveField getActiveField() {
        return activeField;
    }

    public void addContinueListener(ActionListener listener) {
        continueButton.addActionListener(listener);
    }

    public void addSwapListener(ActionListener listener) {
        swapButton.addActionListener(listener);
    }

    public void addOriginDocumentListener(DocumentListener listener) {
        originField.getDocument().addDocumentListener(Objects.requireNonNull(listener));
    }

    public void addDestinationDocumentListener(DocumentListener listener) {
        destinationField.getDocument().addDocumentListener(Objects.requireNonNull(listener));
    }

    /**
     * Update suggestion list with new text entries (e.g., addresses).
     */
    public void updateSuggestions(List<String> suggestions) {
        suggestionListModel.clear();
        if (suggestions != null) {
            for (String s : suggestions) {
                suggestionListModel.addElement(s);
            }
        }
    }

    /**
     * Register a listener when user clicks/changes selection in suggestion list.
     */
    public void addSuggestionSelectionListener(Runnable handler) {
        suggestionList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && handler != null) {
                handler.run();
            }
        });
    }

    public String getSelectedSuggestionText() {
        return suggestionList.getSelectedValue();
    }

    public JButton getViewHistoryButton() {
        return viewHistoryButton;
    }

    // --- Helpers -----------------------------------------------------------

    private void updateContinueEnabled() {
        boolean hasOrigin = !originField.getText().isBlank();
        boolean hasDestination = !destinationField.getText().isBlank();
        continueButton.setEnabled(hasOrigin && hasDestination);
    }
}