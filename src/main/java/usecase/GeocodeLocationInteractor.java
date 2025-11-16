package usecase;

import app.ApiFetcher;
import entity.Location;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Use case for geocoding free-text location input into Location entities.
 *
 * This is a minimal implementation that parses the ORS geocode/search JSON
 * in a simple way (not a full JSON parser). It extracts:
 * - properties.label (as name)
 * - geometry.coordinates [lon, lat]
 *
 * It returns a list of Location suggestions.
 */
public class GeocodeLocationInteractor {

    private final ApiFetcher apiFetcher;

    // Regex patterns tailored for ORS geocode JSON structure
    // "label":"Toronto City Hall, ...","id":
    private static final Pattern LABEL_PATTERN =
            Pattern.compile("\"label\"\\s*:\\s*\"(.*?)\"", Pattern.DOTALL);

    // "coordinates":[-79.3832,43.6536]
    private static final Pattern COORD_PATTERN =
            Pattern.compile("\"coordinates\"\\s*:\\s*\\[\\s*([-0-9.]+)\\s*,\\s*([-0-9.]+)\\s*\\]");

    public GeocodeLocationInteractor(ApiFetcher apiFetcher) {
        this.apiFetcher = apiFetcher;
    }

    /**
     * Returns up to maxResults Location suggestions for the given free-text query.
     *
     * @throws IOException          if the HTTP request fails
     * @throws InterruptedException if the HTTP client is interrupted
     */
    public List<Location> searchLocations(String text, int maxResults)
            throws IOException, InterruptedException {

        String json = apiFetcher.fetchGeocodeJson(text, maxResults);

        // Very simple and brittle parsing, but fine for an initial implementation.
        List<Location> locations = new ArrayList<>();

        Matcher coordMatcher = COORD_PATTERN.matcher(json);
        Matcher labelMatcher = LABEL_PATTERN.matcher(json);

        // We assume features appear in order with label then coordinates.
        while (coordMatcher.find() && labelMatcher.find(locations.size() == 0 ? 0 : labelMatcher.end())) {
            double lon = Double.parseDouble(coordMatcher.group(1));
            double lat = Double.parseDouble(coordMatcher.group(2));
            String label = labelMatcher.group(1);

            locations.add(new Location(label, lat, lon));

            if (locations.size() >= maxResults) {
                break;
            }
        }

        return locations;
    }

    /**
     * Convenience: returns the top (best) match, or null if none.
     */
    public Location findBestLocation(String text) throws IOException, InterruptedException {
        List<Location> results = searchLocations(text, 1);
        return results.isEmpty() ? null : results.get(0);
    }
}