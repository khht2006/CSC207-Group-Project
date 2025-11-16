package app;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import app.Config;

/**
 * Low-level client for external APIs:
 * - OpenRouteService directions (walking, cycling)
 * - Toronto Bike Share GBFS endpoints
 *
 * This class returns raw JSON strings.
 * Higher-level code (gateways / services) should parse these.
 */
public class ApiFetcher {

    // --- ORS configuration -------------------------------------------------

    private static final String ORS_BASE_URL = "https://api.openrouteservice.org";
    private static final String ORS_DIRECTIONS_PATH = "/v2/directions";

    // --- Bike Share endpoints ----------------------------------------------

    private static final String BIKE_STATION_INFO_URL =
            "https://tor.publicbikesystem.net/ube/gbfs/v1/en/station_information";

    private static final String BIKE_STATION_STATUS_URL =
            "https://tor.publicbikesystem.net/ube/gbfs/v1/en/station_status";

    // --- Fields ------------------------------------------------------------

    private final String orsApiKey;
    private final HttpClient httpClient;

    // --- Constructor -------------------------------------------------------

    public ApiFetcher() {
        this.orsApiKey = Config.getOrsApiKey();
        this.httpClient = HttpClient.newHttpClient();
    }

    // --- Public methods: ORS directions -----------------------------------

    /**
     * Generic directions call for a given profile.
     *
     * @param profile  ORS profile, e.g. "foot-walking", "cycling-regular"
     */
    public String fetchDirectionsJson(String profile,
                                      double startLon, double startLat,
                                      double endLon, double endLat)
            throws IOException, InterruptedException {

        String url = ORS_BASE_URL + ORS_DIRECTIONS_PATH + "/" + encode(profile);

        // ORS expects coordinates as [lon,lat] pairs in JSON
        String body = """
                {
                  "coordinates": [
                    [%f, %f],
                    [%f, %f]
                  ]
                }
                """.formatted(startLon, startLat, endLon, endLat);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", orsApiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response =
                httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        int status = response.statusCode();
        if (status < 200 || status >= 300) {
            throw new IOException("ORS directions request failed (" + status + "): "
                    + response.body());
        }

        return response.body();
    }

    /**
     * Convenience method for walking directions.
     */
    public String fetchWalkingDirectionsJson(double startLon, double startLat,
                                             double endLon, double endLat)
            throws IOException, InterruptedException {
        return fetchDirectionsJson("foot-walking", startLon, startLat, endLon, endLat);
    }

    /**
     * Convenience method for regular cycling directions.
     */
    public String fetchCyclingDirectionsJson(double startLon, double startLat,
                                             double endLon, double endLat)
            throws IOException, InterruptedException {
        return fetchDirectionsJson("cycling-regular", startLon, startLat, endLon, endLat);
    }

    // --- Public methods: Bike Share GBFS ----------------------------------

    /**
     * Returns Toronto Bike Share station information JSON.
     */
    public String fetchBikeStationInformationJson()
            throws IOException, InterruptedException {
        return getJson(BIKE_STATION_INFO_URL);
    }

    /**
     * Returns Toronto Bike Share station status JSON.
     */
    public String fetchBikeStationStatusJson()
            throws IOException, InterruptedException {
        return getJson(BIKE_STATION_STATUS_URL);
    }

    // --- Internal helpers --------------------------------------------------

    private String getJson(String url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response =
                httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        int status = response.statusCode();
        if (status < 200 || status >= 300) {
            throw new IOException("GET " + url + " failed (" + status + "): "
                    + response.body());
        }
        return response.body();
    }

    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}