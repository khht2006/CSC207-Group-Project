package usecase.walk_route;

import api.ApiFetcher;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Interactor responsible for retrieving walking route data.
 * It requests data from an API and converts it into a response object.
 */
public class WalkRouteInteractor {

    private final ApiFetcher apiFetcher;
    private double timeMinutes;

    /**
     * Constructs the WalkRouteInteractor.
     *
     * @param apiFetcher dependency used to fetch walking directions
     */
    public WalkRouteInteractor(ApiFetcher apiFetcher) {
        this.apiFetcher = apiFetcher;
    }

    /**
     * Executes the walking route use case.
     *
     * @param startLat starting latitude
     * @param startLng starting longitude
     * @param endLat destination latitude
     * @param endLng destination longitude
     * @return response containing distance and estimated travel time
     * @throws IOException if fetching or parsing the API response fails
     * @throws InterruptedException if the request is interrupted
     */
    public WalkRouteResponse execute(
            double startLat,
            double startLng,
            double endLat,
            double endLng) throws IOException, InterruptedException {

        String jsonString = apiFetcher.fetchWalkingDirectionsJson(
                startLng, startLat, endLng, endLat);

        JSONObject summary = new JSONObject(jsonString)
                .getJSONArray("routes")
                .getJSONObject(0)
                .getJSONObject("summary");

        double distanceKm = summary.getDouble("distance") / 1000.00;
        double timeMinutes = summary.getDouble("duration") / 60.00;

        return new WalkRouteResponse(distanceKm, timeMinutes);
    }

    /**
     * Value object representing walking route data.
     * Must be public to be accessible by presentation and framework layers.
     */
    public static class WalkRouteResponse {
        private final double distanceKm;
        public final double timeMinutes;

        /**
         * Creates a new WalkRouteResponse.
         *
         * @param distanceKm  route distance in kilometers
         * @param timeMinutes estimated duration in minutes
         */
        public WalkRouteResponse(double distanceKm, double timeMinutes) {
            this.distanceKm = distanceKm;
            this.timeMinutes = timeMinutes;
        }

        /**
         * Returns the total route distance in kilometers.
         *
         * @return the distance in km
         */
        public double getDistanceKm() {
            return Math.round(distanceKm * 100.0) / 100.0;
        }

        /**
         * Returns the estimated travel time in minutes.
         *
         * @return the time in minutes
         */
        public double getTimeMinutes() {
            return Math.round(timeMinutes * 100.0) / 100.0;
        }
    }
}

