package interface_adapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import entity.Location;
import usecase.GeocodeLocationInteractor;
import view.OriginalDestinationPanel;

/**
 * Controller for the "enter origin and destination" view.
 * Now supports:
 * - live suggestion lists for origin/destination (list of possible addresses)
 * - selecting a suggestion to fill the corresponding text field
 */
public class OriginalDestinationController {

    private final OriginalDestinationPanel panel;
    private final GeocodeLocationInteractor geocodeInteractor;
    private final BiConsumer<Location, Location> onLocationsResolved;

    // latest suggestions for each field
    private List<Location> originSuggestions = new ArrayList<>();
    private List<Location> destinationSuggestions = new ArrayList<>();

    public OriginalDestinationController(OriginalDestinationPanel panel,
                                         GeocodeLocationInteractor geocodeInteractor,
                                         BiConsumer<Location, Location> onLocationsResolved) {
        this.panel = panel;
        this.geocodeInteractor = geocodeInteractor;
        this.onLocationsResolved = onLocationsResolved;

        wireEvents();
    }

    private void wireEvents() {
        panel.addSwapListener(actionEvent -> {
            final String origin = panel.getOriginText();
            final String dest = panel.getDestinationText();
            panel.setOriginText(dest);
            panel.setDestinationText(origin);
        });

        panel.addContinueListener(actionEvent -> handleContinueAsync());

        panel.addOriginDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                triggerSuggestionsAsync(true);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                triggerSuggestionsAsync(true);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                triggerSuggestionsAsync(true);
            }
        });

        panel.addDestinationDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                triggerSuggestionsAsync(false);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                triggerSuggestionsAsync(false);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                triggerSuggestionsAsync(false);
            }
        });

        panel.addSuggestionSelectionListener(this::applySelectedSuggestionToActiveField);
    }

    // Fetch suggestions for origin or destination on a background thread.
    private void triggerSuggestionsAsync(boolean forOrigin) {
        final String text = forOrigin ? panel.getOriginText().trim() : panel.getDestinationText().trim();
        if (text.isBlank()) {
            panel.updateSuggestions(List.of());
            if (forOrigin) {
                originSuggestions = new ArrayList<>();
            }
            else {
                destinationSuggestions = new ArrayList<>();
            }
            return;
        }

        final SwingWorker<List<Location>, Void> worker = new SwingWorker<>() {

            @Override
            protected List<Location> doInBackground() throws Exception {
                try {
                    final int random = 5;
                    return geocodeInteractor.searchLocations(text, random);
                }
                catch (IOException | InterruptedException ex) {
                    return new ArrayList<>();
                }
            }

            @Override
            protected void done() {
                try {
                    final List<Location> results = get();
                    if (forOrigin) {
                        originSuggestions = results;
                    }
                    else {
                        destinationSuggestions = results;
                    }

                    final List<String> labels = new ArrayList<>();
                    for (Location loc : results) {
                        labels.add(loc.getName());
                    }
                    panel.updateSuggestions(labels);

                }
                catch (Exception ignored) {
                    panel.updateSuggestions(List.of());
                }
            }
        };
        worker.execute();
    }

    private void applySelectedSuggestionToActiveField() {
        final String selectedText = panel.getSelectedSuggestionText();
        if (selectedText == null || selectedText.isBlank()) {
            return;
        }

        switch (panel.getActiveField()) {
            case ORIGIN -> {
                panel.setOriginText(selectedText);
            }
            case DESTINATION -> {
                panel.setDestinationText(selectedText);
            }
            case NONE -> {
            }
        }
    }

    /**
     * Continue: still uses the best location for each field, but user has had the chance
     * to pick a suggestion first.
     */
    private void handleContinueAsync() {
        final String originText = panel.getOriginText().trim();
        final String destText = panel.getDestinationText().trim();

        if (originText.equalsIgnoreCase(destText)) {
            JOptionPane.showMessageDialog(
                    panel,
                    "Starting Location and Destination are the same.\nPlease enter a different destination.",
                    "Invalid Destination",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        panel.setEnabled(false);

        final SwingWorker<GeocodeResult, Void> worker = new SwingWorker<>() {

            @Override
            protected GeocodeResult doInBackground() {
                try {
                    // You could try to match originText/destText against the suggestion lists first;
                    // for now we just use findBestLocation again.
                    final Location originLoc = geocodeInteractor.findBestLocation(originText);
                    final Location destLoc = geocodeInteractor.findBestLocation(destText);
                    return GeocodeResult.success(originLoc, destLoc);
                }
                catch (Exception ex) {
                    return GeocodeResult.failure(ex);
                }
            }

            @Override
            protected void done() {
                panel.setEnabled(true);
                try {
                    final GeocodeResult result = get();

                    if (result.error != null) {
                        handleError(result.error);
                        return;
                    }

                    if (result.origin == null) {
                        JOptionPane.showMessageDialog(
                                panel,
                                "Could not find a valid location for origin: \"" + originText + "\"",
                                "Invalid Location",
                                JOptionPane.WARNING_MESSAGE
                        );
                        return;
                    }

                    if (result.destination == null) {
                        JOptionPane.showMessageDialog(
                                panel,
                                "Could not find a valid location for destination: \"" + destText + "\"",
                                "Invalid Destination",
                                JOptionPane.WARNING_MESSAGE
                        );
                        return;
                    }

                    if (onLocationsResolved != null) {
                        onLocationsResolved.accept(result.origin, result.destination);
                    }

                }
                catch (Exception ex) {
                    handleError(ex);
                }
            }
        };

        worker.execute();
    }

    private void handleError(Exception exception) {
        if (exception instanceof IOException) {
            JOptionPane.showMessageDialog(
                    panel,
                    "Error contacting geocoding service.\nPlease try again.\n\n" + exception.getMessage(),
                    "Geocoding Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
        else if (exception instanceof InterruptedException) {
            Thread.currentThread().interrupt();
            JOptionPane.showMessageDialog(
                    panel,
                    "Geocoding interrupted. Please try again.",
                    "Geocoding Interrupted",
                    JOptionPane.ERROR_MESSAGE
            );
        }
        else {
            JOptionPane.showMessageDialog(
                    panel,
                    "Unexpected error: " + exception,
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private static final class GeocodeResult {
        private final Location origin;
        private final Location destination;
        private final Exception error;

        private GeocodeResult(Location origin, Location destination, Exception error) {
            this.origin = origin;
            this.destination = destination;
            this.error = error;
        }

        static GeocodeResult success(Location origin, Location destination) {
            return new GeocodeResult(origin, destination, null);
        }

        static GeocodeResult failure(Exception error) {
            return new GeocodeResult(null, null, error);
        }
    }
}
