package interface_adapter;

import entity.Location;
import usecase.GeocodeLocationInteractor;
import view.OriginalDestinationPanel;

import javax.swing.*;

/**
 * Controller for the "enter origin and destination" view.
 *
 * Responsible for:
 * - reading user input from OriginalDestinationPanel
 * - using GeocodeLocationInteractor to resolve text to Location entities
 * - basic validation and error reporting
 */
public class OriginalDestinationController {

    private final OriginalDestinationPanel panel;
    private final GeocodeLocationInteractor geocodeInteractor;

    public OriginalDestinationController(OriginalDestinationPanel panel,
                                         GeocodeLocationInteractor geocodeInteractor) {
        this.panel = panel;
        this.geocodeInteractor = geocodeInteractor;

        wireEvents();
    }

    private void wireEvents() {
        panel.addSwapListener(e -> {
            String origin = panel.getOriginText();
            String dest = panel.getDestinationText();
            panel.setOriginText(dest);
            panel.setDestinationText(origin);
        });

        panel.addContinueListener(e -> handleContinueAsync());
    }

    /**
     * Runs geocoding on a background thread to avoid freezing the UI.
     */
    private void handleContinueAsync() {
        String originText = panel.getOriginText().trim();
        String destText = panel.getDestinationText().trim();

        if (originText.equalsIgnoreCase(destText)) {
            JOptionPane.showMessageDialog(
                    panel,
                    "Starting Location and Destination are the same.\nPlease enter a different destination.",
                    "Invalid Destination",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // Optional: disable the panel while we work
        panel.setEnabled(false);

        SwingWorker<GeocodeResult, Void> worker = new SwingWorker<>() {
            @Override
            protected GeocodeResult doInBackground() {
                try {
                    Location originLoc = geocodeInteractor.findBestLocation(originText);
                    Location destLoc = geocodeInteractor.findBestLocation(destText);
                    return GeocodeResult.success(originLoc, destLoc);
                } catch (Exception ex) {
                    return GeocodeResult.failure(ex);
                }
            }

            @Override
            protected void done() {
                panel.setEnabled(true);
                try {
                    GeocodeResult result = get();

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

                    // For now, just show the resolved entities.
                    String message = "Origin resolved to:\n  " + result.origin
                            + "\n\nDestination resolved to:\n  " + result.destination;
                    JOptionPane.showMessageDialog(
                            panel,
                            message,
                            "Locations Found",
                            JOptionPane.INFORMATION_MESSAGE
                    );

                    // TODO: hand off origin/destination Location to next use case / view.

                } catch (Exception ex) {
                    handleError(ex);
                }
            }
        };

        worker.execute();
    }

    private void handleError(Exception ex) {
        if (ex instanceof java.io.IOException) {
            JOptionPane.showMessageDialog(
                    panel,
                    "Error contacting geocoding service.\nPlease try again.\n\n" + ex.getMessage(),
                    "Geocoding Error",
                    JOptionPane.ERROR_MESSAGE
            );
        } else if (ex instanceof InterruptedException) {
            Thread.currentThread().interrupt();
            JOptionPane.showMessageDialog(
                    panel,
                    "Geocoding interrupted. Please try again.",
                    "Geocoding Interrupted",
                    JOptionPane.ERROR_MESSAGE
            );
        } else {
            JOptionPane.showMessageDialog(
                    panel,
                    "Unexpected error: " + ex,
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Simple holder for geocoding result or error.
     */
    private static class GeocodeResult {
        final Location origin;
        final Location destination;
        final Exception error;

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