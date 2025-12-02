package usecase;

import api.ApiFetcher;
import java.io.IOException;
import org.json.JSONObject;

/**
 * Interactor responsible for retrieving walking route data.
 * Follows Clean Architecture: depends on input/output boundaries and external API abstraction.
 */
public class WalkRouteInteractor implements WalkRouteInputBoundary {

    private final ApiFetcher apiFetcher;

    public WalkRouteInteractor(ApiFetcher apiFetcher) {
        this.apiFetcher = apiFetcher;
    }

    @Override
    public void execute(WalkRouteInputData inputData, WalkRouteOutputBoundary outputBoundary) {
        try {
            String jsonString = apiFetcher.fetchWalkingDirectionsJson(
                    inputData.startLng, inputData.startLat,
                    inputData.endLng, inputData.endLat);

            JSONObject summary = new JSONObject(jsonString)
                    .getJSONArray("routes")
                    .getJSONObject(0)
                    .getJSONObject("summary");

            double distanceKm = summary.getDouble("distance") / 1000.0;
            double timeMinutes = summary.getDouble("duration") / 60.0;

            WalkRouteOutputData response = new WalkRouteOutputData(
                    Math.round(distanceKm * 100.0) / 100.0,
                    Math.round(timeMinutes * 100.0) / 100.0
            );

            outputBoundary.present(response);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to fetch walking route", e);
        }
    }
}
