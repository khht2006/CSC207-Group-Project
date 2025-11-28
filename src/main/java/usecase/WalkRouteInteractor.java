package usecase;

import api.ApiFetcher;
import org.json.JSONObject;

public class WalkRouteInteractor {

    private final ApiFetcher apiFetcher;

    public WalkRouteInteractor(ApiFetcher apiFetcher) {
        this.apiFetcher = apiFetcher;
    }

    public WalkRouteResponse execute(double startLat, double startLng,
                                     double endLat, double endLng) throws Exception {

        // Call the existing method from ApiFetcher
        String jsonString = apiFetcher.fetchWalkingDirectionsJson(
                startLng, startLat, endLng, endLat);

        JSONObject responseJson = new JSONObject(jsonString);

        // Parse JSON
        JSONObject summary = responseJson
                .getJSONArray("routes")
                .getJSONObject(0)
                .getJSONObject("summary");

        double distanceKm = summary.getDouble("distance") / 1000.0;
        double timeMinutes = summary.getDouble("duration") / 60.0;

        return new WalkRouteResponse(distanceKm, timeMinutes);
    }

    // Object to store the data that we will return
    public static class WalkRouteResponse {
        public final double distanceKm;
        public final double timeMinutes;

        public WalkRouteResponse(double distanceKm, double timeMinutes) {
            this.distanceKm = distanceKm;
            this.timeMinutes = timeMinutes;
        }
    }
}
