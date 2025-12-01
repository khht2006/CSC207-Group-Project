package usecase;

import api.ApiFetcher;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Interactor that handles cycling route duration retrieval and
 * sends the results to the output boundary.
 */
public class BikeRouteInteractor implements BikeRouteInputBoundary {

    /** Pattern to extract duration in seconds from JSON response. */
    private static final Pattern DURATION_PATTERN =
            Pattern.compile("\"duration\"\\s*:\\s*(\\d+(?:\\.\\d+)?)", Pattern.DOTALL);

    private final ApiFetcher apiFetcher;
    private final BikeRouteOutputBoundary presenter;

    /**
     * Constructs a BikeRouteInteractor with required dependencies.
     *
     * @param apiFetcher API fetcher used to request cycling directions
     * @param presenter output boundary for presenting results
     */
    public BikeRouteInteractor(
            ApiFetcher apiFetcher,
            BikeRouteOutputBoundary presenter) {
        this.apiFetcher = apiFetcher;
        this.presenter = presenter;
    }

    /**
     * Executes the cycling route request and forwards duration (in minutes)
     * to the output boundary.
     *
     * @param inputData input containing origin and destination coordinates
     */
    @Override
    public void execute(BikeRouteInputData inputData) {
        try {
            String json = apiFetcher.fetchCyclingDirectionsJson(
                    inputData.getOriginLon(),
                    inputData.getOriginLat(),
                    inputData.getDestinationLon(),
                    inputData.getDestinationLat());

            double minutes = parseDurationMinutes(json);
            presenter.present(new BikeRouteOutputData(minutes));
        } catch (IOException e) {
            presenter.present(new BikeRouteOutputData(
                    -1, "Failed to fetch cycling directions: " + e.getMessage()));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            presenter.present(new BikeRouteOutputData(
                    -1, "Directions request interrupted."));
        } catch (IllegalStateException e) {
            presenter.present(new BikeRouteOutputData(
                    -1, e.getMessage()));
        }
    }

    /**
     * Parses cycling duration (in minutes) from the raw JSON response.
     *
     * @param directionsJson JSON containing duration field
     * @return duration in minutes
     * @throws IllegalStateException if duration is missing
     */
    private double parseDurationMinutes(String directionsJson) {
        Matcher matcher = DURATION_PATTERN.matcher(directionsJson);
        if (matcher.find()) {
            double seconds = Double.parseDouble(matcher.group(1));
            return seconds / 60.0;
        }
        throw new IllegalStateException("Duration not found in ORS response.");
    }
}
