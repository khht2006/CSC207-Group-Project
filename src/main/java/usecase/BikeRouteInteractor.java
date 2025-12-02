package usecase;

import api.ApiFetcher;
import entity.Route;
import entity.Station;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Interactor that handles cycling route duration retrieval and
 * sends the results to the output boundary.
 */
public class BikeRouteInteractor implements BikeRouteInputBoundary {

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

    // Executes the biking route request and pushes the duration (in minutes to the output boundary.)
    public void execute(BikeRouteInputData inputData) {
        try {
            // 1. Fetch bike stations
            String stationInfoJson = apiFetcher.fetchBikeStationInformationJson();

            // 2. Find nearest station to origin
            Station nearestToOrigin = findNearestStation(
                    inputData.getOriginLat(), inputData.getOriginLon(), stationInfoJson);

            // 3. Find nearest station to destination
            Station nearestToDest = findNearestStation(
                    inputData.getDestinationLat(), inputData.getDestinationLon(), stationInfoJson);

            if (nearestToOrigin == null || nearestToDest == null) {
                presenter.present(new BikeRouteOutputData("No bike stations found."));
                return;
            }

            // 4. Walk: Origin -> Station1
            String walk1Json = apiFetcher.fetchWalkingDirectionsJson(
                    inputData.getOriginLon(), inputData.getOriginLat(),
                    nearestToOrigin.getLongitude(), nearestToOrigin.getLatitude());
            Route walk1Route = parseRoute(walk1Json);

            // 5. Bike: Station1 -> Station2
            String bikeJson = apiFetcher.fetchCyclingDirectionsJson(
                    nearestToOrigin.getLongitude(), nearestToOrigin.getLatitude(),
                    nearestToDest.getLongitude(), nearestToDest.getLatitude());
            Route bikeRoute = parseRoute(bikeJson);

            // 6. Walk: Station2 -> Destination
            String walk2Json = apiFetcher.fetchWalkingDirectionsJson(
                    nearestToDest.getLongitude(), nearestToDest.getLatitude(),
                    inputData.getDestinationLon(), inputData.getDestinationLat());
            Route walk2Route = parseRoute(walk2Json);

            presenter.present(new BikeRouteOutputData(
                    walk1Route, nearestToOrigin,
                    bikeRoute, nearestToDest,
                    walk2Route, inputData.getDestinationName()));


        } catch (IOException e) {
            presenter.present(new BikeRouteOutputData("Failed to fetch cycling directions: " + e.getMessage()));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            presenter.present(new BikeRouteOutputData("Directions request interrupted."));
        } catch (IllegalStateException | org.json.JSONException e) {
            presenter.present(new BikeRouteOutputData("Malformed directions response: " + e.getMessage()));
        }
    }

    private Route parseRoute(String jsonString) {
        JSONObject responseJson = new JSONObject(jsonString);

        // ORS can return either "routes" (v2) or GeoJSON "features".
        JSONObject routeJson;
        if (responseJson.has("routes")) {
            routeJson = responseJson.getJSONArray("routes").getJSONObject(0);
        } else if (responseJson.has("features")) {
            routeJson = responseJson.getJSONArray("features")
                    .getJSONObject(0)
                    .getJSONObject("properties");
        } else {
            throw new IllegalStateException("No routes found in response.");
        }

        JSONObject summary = routeJson.getJSONObject("summary");

        double distanceMeters = summary.getDouble("distance");
        double durationSeconds = summary.getDouble("duration");

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

        return new Route(distanceMeters, durationSeconds, instructions);
    }

    private Station findNearestStation(double lat, double lon, String stationJson) {
        JSONObject root = new JSONObject(stationJson);
        JSONArray stations = root.getJSONObject("data").getJSONArray("stations");

        Station nearest = null;
        double minDist = Double.MAX_VALUE;

        for (int i = 0; i < stations.length(); i++) {
            JSONObject s = stations.getJSONObject(i);
            double sLat = s.getDouble("lat");
            double sLon = s.getDouble("lon");

            double dist = haversine(lat, lon, sLat, sLon);
            if (dist < minDist) {
                minDist = dist;
                nearest = new Station(s.getString("name"), sLat, sLon);
            }
        }
        return nearest;
    }

    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radius of the earth
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c * 1000; // meters
    }
}
