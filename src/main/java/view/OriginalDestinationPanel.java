package view;

import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Objects;

import javax.swing.*;
import javax.swing.event.DocumentListener;

/**
 * Panel where user enters:
 * - current location (origin)
 * - destination
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

        final int titleFontSize = 18;
        final JLabel title = new JLabel("Trip Planner - Enter Locations", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, titleFontSize));
        this.add(title, BorderLayout.NORTH);

        final int insets = 4;
        final JPanel centerPanel = new JPanel(new GridBagLayout());
        final GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(insets, insets, insets, insets);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        final JLabel originLabel = new JLabel("Current location (origin):");
        originField = new JTextField();

        final JLabel destinationLabel = new JLabel("Destination:");
        destinationField = new JTextField();

        // Row 0: origin label
        gbc.gridx = 0;
        gbc.gridy = 0;
        centerPanel.add(originLabel, gbc);

        // Row 1: origin field
        final int originFieldY = 1;
        gbc.gridy = originFieldY;
        centerPanel.add(originField, gbc);

        // Row 2: destination label
        final int destLabelY = 2;
        gbc.gridy = destLabelY;
        centerPanel.add(destinationLabel, gbc);

        // Row 3: destination field
        final int destFieldY = 3;
        gbc.gridy = destFieldY;
        centerPanel.add(destinationField, gbc);

        this.add(centerPanel, BorderLayout.CENTER);

        // Suggestion list on the left
        suggestionListModel = new DefaultListModel<>();
        suggestionList = new JList<>(suggestionListModel);
        suggestionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        final JScrollPane suggestionScroll = new JScrollPane(suggestionList);
        suggestionScroll.setBorder(BorderFactory.createTitledBorder("Suggestions"));

        this.add(suggestionScroll, BorderLayout.WEST);

        final JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
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
        final DocumentListener docListener = new javax.swing.event.DocumentListener() {
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

    /**
     * Set origin display text.
     * @param text display text
     */
    public void setOriginText(String text) {
        originField.setText(text == null ? "" : text);
    }

    public String getDestinationText() {
        return destinationField.getText();
    }

    /**
     * Set destination display text.
     * @param text display text
     */
    public void setDestinationText(String text) {
        destinationField.setText(text == null ? "" : text);
    }

    public ActiveField getActiveField() {
        return activeField;
    }

    /**
     * Add continue listener.
     * @param listener lister
     */
    public void addContinueListener(ActionListener listener) {
        continueButton.addActionListener(listener);
    }

    /**
     * Add swap listener.
     * @param listener listener
     */
    public void addSwapListener(ActionListener listener) {
        swapButton.addActionListener(listener);
    }

    /**
     * Add origin document listener.
     * @param listener listener
     */
    public void addOriginDocumentListener(DocumentListener listener) {
        originField.getDocument().addDocumentListener(Objects.requireNonNull(listener));
    }

    /**
     * Add destination document listener.
     * @param listener listener
     */
    public void addDestinationDocumentListener(DocumentListener listener) {
        destinationField.getDocument().addDocumentListener(Objects.requireNonNull(listener));
    }

    /**
     * Update suggestion list with new text entries (e.g., addresses).
     * @param suggestions suggestion list
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
     * @param handler runnable handler
     */
    public void addSuggestionSelectionListener(Runnable handler) {
        suggestionList.addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting() && handler != null) {
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
        final boolean hasOrigin = !originField.getText().isBlank();
        final boolean hasDestination = !destinationField.getText().isBlank();
        continueButton.setEnabled(hasOrigin && hasDestination);
    }
}
