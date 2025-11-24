package interactor;

import entity.Location;
import use_case.GetCurrentLocationUseCase;
import use_case.GetDestinationUseCase;
import api.fetcher.APIFetcher;
import org.json.JSONArray;
import org.json.JSONObject;

public class WalkRouteInteractor {

    private final GetCurrentLocationUseCase currentLocationUseCase;
    private final GetDestinationUseCase destinationUseCase;
    private final APIFetcher apiFetcher;
    private final String orsApiKey;

    public WalkRouteInteractor(GetCurrentLocationUseCase currentLocationUseCase,
                               GetDestinationUseCase destinationUseCase,
                               APIFetcher apiFetcher,
                               String orsApiKey) {
        this.currentLocationUseCase = currentLocationUseCase;
        this.destinationUseCase = destinationUseCase;
        this.apiFetcher = apiFetcher;
        this.orsApiKey = orsApiKey;
    }

    public WalkRouteResponse execute() throws Exception {

        // Retrieve coordinates
        Location start = currentLocationUseCase.getLocation();
        Location end = destinationUseCase.getDestinationLocation();

        // ORS Directions Endpoint
        String url = "https://api.openrouteservice.org/v2/directions/foot-walking/json";

        // JSON request body
        JSONObject body = new JSONObject();
        JSONArray coords = new JSONArray();
        coords.put(new JSONArray().put(start.getLongitude()).put(start.getLatitude()));
        coords.put(new JSONArray().put(end.getLongitude()).put(end.getLatitude()));
        body.put("coordinates", coords);



        // Make request
        JSONObject responseJson = apiFetcher.postJson(url, body.toString(), orsApiKey);

        // Parse response
        JSONObject summary = responseJson
                .getJSONArray("routes")
                .getJSONObject(0)
                .getJSONObject("summary");

        double distanceMeters = summary.getDouble("distance"); // meters
        double durationSeconds = summary.getDouble("duration"); // seconds

        double distanceKm = distanceMeters / 1000.0;
        double timeMinutes = durationSeconds / 60.0;

        return new WalkRouteResponse(distanceKm, timeMinutes);
    }

    // DTO response
    public static class WalkRouteResponse {
        public final double distanceKm;
        public final double timeMinutes;

        public WalkRouteResponse(double distanceKm, double timeMinutes) {
            this.distanceKm = distanceKm;
            this.timeMinutes = timeMinutes;
        }
    }
}
