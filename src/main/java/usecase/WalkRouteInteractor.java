package usecase;

import app.ApiFetcher;
import org.json.JSONArray;
import org.json.JSONObject;

public class WalkRouteInteractor {

    private final ApiFetcher apiFetcher;

    public WalkRouteInteractor(ApiFetcher apiFetcher) {
        this.apiFetcher = apiFetcher;
    }

    public WalkRouteResponse execute(double startLat, double startLng,
                                     double endLat, double endLng) throws Exception {

        // Call existing API method
        String jsonString = apiFetcher.fetchWalkingDirectionsJson(
                startLng, startLat, endLng, endLat);

        JSONObject responseJson = new JSONObject(jsonString);

        JSONObject summary = responseJson
                .getJSONArray("routes")
                .getJSONObject(0)
                .getJSONObject("summary");

        double distanceKm = summary.getDouble("distance") / 1000.0;
        double timeMinutes = summary.getDouble("duration") / 60.0;

        return new WalkRouteResponse(distanceKm, timeMinutes);
    }

    public static class WalkRouteResponse {
        public final double distanceKm;
        public final double timeMinutes;

        public WalkRouteResponse(double distanceKm, double timeMinutes) {
            this.distanceKm = distanceKm;
            this.timeMinutes = timeMinutes;
        }
    }
}
