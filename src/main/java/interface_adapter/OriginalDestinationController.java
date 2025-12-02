package interface_adapter;

import entity.Location;
import interface_adapter.fetch_location.GeocodeController;
import interface_adapter.fetch_location.GeocodeViewModel;
import interface_adapter.select_route_locations.SelectRouteLocationsController;
import view.OriginalDestinationPanel;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * Controller for the origin/destination view.
 * Delegates geocoding to GeocodeController (proper CA).
 */
public class OriginalDestinationController {

    private final OriginalDestinationPanel panel;
    private final GeocodeController geocodeController;
    private final GeocodeViewModel geocodeViewModel;
    private final SelectRouteLocationsController selectRouteController;
    private final BiConsumer<Location, Location> onValidLocations;

    private Location selectedOrigin;
    private Location selectedDestination;

    public OriginalDestinationController(
            OriginalDestinationPanel panel,
            GeocodeController geocodeController,
            GeocodeViewModel geocodeViewModel,
            SelectRouteLocationsController selectRouteController,
            BiConsumer<Location, Location> onValidLocations) {
        this.panel = panel;
        this.geocodeController = geocodeController;
        this.geocodeViewModel = geocodeViewModel;
        this.selectRouteController = selectRouteController;
        this.onValidLocations = onValidLocations;

        wireEvents();
        observeViewModel();
    }

    private void wireEvents() {
        panel.addSwapListener(actionEvent -> {
            String origin = panel.getOriginText();
            String dest = panel.getDestinationText();
            panel.setOriginText(dest);
            panel.setDestinationText(origin);
        });

        panel.addContinueListener(actionEvent -> handleContinue());

        panel.addOriginDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                selectedOrigin = null;
                triggerSuggestions(true);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                selectedOrigin = null;
                triggerSuggestions(true);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                selectedOrigin = null;
                triggerSuggestions(true);
            }
        });

        panel.addDestinationDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                selectedDestination = null;
                triggerSuggestions(false);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                selectedDestination = null;
                triggerSuggestions(false);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                selectedDestination = null;
                triggerSuggestions(false);
            }
        });

        panel.addSuggestionSelectionListener(this::applySelectedSuggestion);
    }

    private void observeViewModel() {
        geocodeViewModel.addPropertyChangeListener(evt -> {
            if ("locations".equals(evt.getPropertyName())) {
                List<Location> locations = geocodeViewModel.getLocations();
                List<String> labels = new ArrayList<>();
                for (Location loc : locations) {
                    labels.add(loc.getName());
                }
                SwingUtilities.invokeLater(() -> panel.updateSuggestions(labels));
            } else if ("errorMessage".equals(evt.getPropertyName())) {
                String error = geocodeViewModel.getErrorMessage();
                if (error != null) {
                    SwingUtilities.invokeLater(() ->
                            JOptionPane.showMessageDialog(panel, error, "Error", JOptionPane.ERROR_MESSAGE));
                }
            }
        });
    }

    private void triggerSuggestions(boolean forOrigin) {
        String text = forOrigin ? panel.getOriginText().trim() : panel.getDestinationText().trim();

        if (text.isBlank()) {
            panel.updateSuggestions(List.of());
            return;
        }

        geocodeController.search(text, 5);
    }

    private void applySelectedSuggestion() {
        String selectedText = panel.getSelectedSuggestionText();
        if (selectedText == null || selectedText.isBlank()) {
            return;
        }

        Location selectedLocation = findLocationByName(selectedText);

        switch (panel.getActiveField()) {
            case ORIGIN -> {
                    panel.setOriginText(selectedText);
                    selectedOrigin = selectedLocation;
            }
            case DESTINATION -> {
                    panel.setDestinationText(selectedText);
                    selectedDestination = selectedLocation;
            }
            case NONE -> { // Do nothing
            }
        }
    }

    private void handleContinue() {
        selectRouteController.execute(selectedOrigin, selectedDestination);
    }

    private Location findLocationByName(String name) {
        List<Location> locations = geocodeViewModel.getLocations();
        if (locations == null) return null;

        return locations.stream()
                .filter(loc -> loc.getName().equals(name))
                .findFirst()
                .orElse(null);
    }
}
//