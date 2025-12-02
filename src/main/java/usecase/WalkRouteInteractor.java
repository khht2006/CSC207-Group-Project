package usecase;

import api.ApiFetcher;
import entity.Route;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class WalkRouteInteractor {

    private final ApiFetcher apiFetcher;

    /**
     * Constructs the WalkRouteInteractor.
     *
     * @param apiFetcher dependency used to fetch walking directions
     */
    public WalkRouteInteractor(ApiFetcher apiFetcher) {
        this.apiFetcher = apiFetcher;
    }

    public Route execute(double startLat, double startLng,
                                     double endLat, double endLng) throws Exception {

        String jsonString = apiFetcher.fetchWalkingDirectionsJson(
                startLng, startLat, endLng, endLat);

        JSONObject responseJson = new JSONObject(jsonString);
        JSONObject routeJson = responseJson.getJSONArray("routes").getJSONObject(0);
        JSONObject summary = routeJson.getJSONObject("summary");

        double distanceMetres = summary.getDouble("distance");
        double timeSeconds = summary.getDouble("duration");


        List<String> instructions = new ArrayList<>();
        if (routeJson.has("segments")) {
            JSONArray segments = routeJson.getJSONArray("segments");
            for (int i = 0; i < segments.length(); i++) {
                JSONObject segment = segments.getJSONObject(i);
                if (segment.has("steps")) {
                    JSONArray steps = segment.getJSONArray("steps");
                    for (int j = 0; j < steps.length(); j++) {
                        instructions.add(steps.getJSONObject(j).getString("instruction"));
                    }
                }
            }
        }

        return new Route(distanceMetres, timeSeconds, instructions);
    }
}

