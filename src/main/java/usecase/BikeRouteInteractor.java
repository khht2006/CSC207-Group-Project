package usecase;

import app.ApiFetcher;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BikeRouteInteractor implements BikeRouteInputBoundary {

    private static final Pattern DURATION_PATTERN =
            Pattern.compile("\"duration\"\\s*:\\s*([0-9]+(?:\\.[0-9]+)?)", Pattern.DOTALL);

    private final ApiFetcher apiFetcher;
    private final BikeRouteOutputBoundary presenter;

    public BikeRouteInteractor(ApiFetcher apiFetcher,
                               BikeRouteOutputBoundary presenter) {
        this.apiFetcher = apiFetcher;
        this.presenter = presenter;
    }

    // Executes the biking route request and pushes the duration (in minutes to the output boundary.
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
            presenter.present(new BikeRouteOutputData(-1, "Failed to fetch cycling directions: " + e.getMessage()));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            presenter.present(new BikeRouteOutputData(-1, "Directions request interrupted."));
        } catch (IllegalStateException e) {
            presenter.present(new BikeRouteOutputData(-1, e.getMessage()));
        }
    }

    private double parseDurationMinutes(String directionsJson) {
        Matcher matcher = DURATION_PATTERN.matcher(directionsJson);
        if (matcher.find()) {
            double seconds = Double.parseDouble(matcher.group(1));
            return seconds / 60.0;
        }
        throw new IllegalStateException("Duration not found in ORS response.");
    }
}
