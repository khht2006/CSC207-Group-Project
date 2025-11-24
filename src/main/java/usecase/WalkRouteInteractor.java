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
        Location start = currentLocationUseCase.getLocation();
        Location end = destinationUseCase.getDestinationLocation();

        String url = "https://api.openrouteservice.org/v2/directions/foot-walking/json";

        JSONObject body = new JSONObject();
        JSONArray coords = new JSONArray();
        coords.put(new JSONArray().put(start.getLongitude()).put(start.getLatitude()));
        coords.put(new JSONArray().put(end.getLongitude()).put(end.getLatitude()));
        body.put("coordinates", coords);

        JSONObject responseJson = apiFetcher.postJson(url, body.toString(), orsApiKey);

        JSONObject summary = responseJson
                .getJSONArray("routes")
                .getJSONObject(0)
                .getJSONObject("summary");

        double distanceMeters = summary.getDouble("distance");
        double durationSeconds = summary.getDouble("duration");

        double distanceKm = distanceMeters / 1000.0;
        double timeMinutes = durationSeconds / 60.0;

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
