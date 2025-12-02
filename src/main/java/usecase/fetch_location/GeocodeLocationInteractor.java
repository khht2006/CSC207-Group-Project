package usecase.fetch_location;

import api.ApiFetcher;
import entity.Location;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * Use case for geocoding free-text location input into Location entities.
 * <p>
 * This is a minimal implementation that parses the ORS geocode/search JSON
 * in a simple way (not a full JSON parser). It extracts:
 * - properties.housenumber + properties.street + properties.locality (if available)
 * - properties.name (e.g. CN Tower) as fallback
 * - properties.label as last fallback
 * - geometry.coordinates [lon, lat]
 * <p>
 * It returns a list of Location suggestions.
 */
public class GeocodeLocationInteractor implements GeocodeInputBoundary {

    private final ApiFetcher apiFetcher;
    private final GeocodeOutputBoundary outputBoundary;

    // Regex patterns tailored for ORS geocode JSON structure

    // "housenumber":"170"
    private static final Pattern HOUSENUMBER_PATTERN =
            Pattern.compile("\"housenumber\"\\s*:\\s*\"(.*?)\"", Pattern.DOTALL);

    // "street":"Jane Street"
    private static final Pattern STREET_PATTERN =
            Pattern.compile("\"street\"\\s*:\\s*\"(.*?)\"", Pattern.DOTALL);

    // "locality":"Toronto"
    private static final Pattern LOCALITY_PATTERN =
            Pattern.compile("\"locality\"\\s*:\\s*\"(.*?)\"", Pattern.DOTALL);

    // "name":"CN Tower"
    private static final Pattern NAME_PATTERN =
            Pattern.compile("\"name\"\\s*:\\s*\"(.*?)\"", Pattern.DOTALL);

    // "label":"Jane Street, Toronto, Ontario, Canada"
    private static final Pattern LABEL_PATTERN =
            Pattern.compile("\"label\"\\s*:\\s*\"(.*?)\"", Pattern.DOTALL);

    // "coordinates":[-79.3832,43.6536]
    private static final Pattern COORD_PATTERN =
            Pattern.compile("\"coordinates\"\\s*:\\s*\\[\\s*([-0-9.]+)\\s*,\\s*([-0-9.]+)\\s*]");

    public GeocodeLocationInteractor(ApiFetcher apiFetcher, GeocodeOutputBoundary outputBoundary) {
        this.apiFetcher = apiFetcher;
        this.outputBoundary = outputBoundary;
    }

    /**
     * Returns up to maxResults Location suggestions for the given free-text query.
     */
    public List<Location> searchLocations(String text, int maxResults)
            throws IOException, InterruptedException {

        if (maxResults <= 0) {
            maxResults = 5;
        }

        String json = apiFetcher.fetchGeocodeJson(text, maxResults);

        List<Location> locations = new ArrayList<>();

        Matcher coordMatcher = COORD_PATTERN.matcher(json);
        Matcher houseMatcher = HOUSENUMBER_PATTERN.matcher(json);
        Matcher streetMatcher = STREET_PATTERN.matcher(json);
        Matcher localityMatcher = LOCALITY_PATTERN.matcher(json);
        Matcher nameMatcher = NAME_PATTERN.matcher(json);
        Matcher labelMatcher = LABEL_PATTERN.matcher(json);

        int searchStart = 0;

        while (coordMatcher.find(searchStart)) {
            double lon = Double.parseDouble(coordMatcher.group(1));
            double lat = Double.parseDouble(coordMatcher.group(2));

            String house = findNextMatch(houseMatcher, coordMatcher.start());
            String street = findNextMatch(streetMatcher, coordMatcher.start());
            String locality = findNextMatch(localityMatcher, coordMatcher.start());
            String name = findNextMatch(nameMatcher, coordMatcher.start());
            String label = findNextMatch(labelMatcher, coordMatcher.start());

            String display = buildDisplayName(house, street, locality, name, label);
            locations.add(new Location(display, lat, lon));

            searchStart = coordMatcher.end();
        }

        // Rank results based on how well they match the user text, then trim.
        return rankAndTrim(locations, text, maxResults);
    }

    /**
     * Convenience: returns the top (best) match, or null if none.
     */
    public Location findBestLocation(String text) throws IOException, InterruptedException {
        List<Location> results = searchLocations(text, 1);
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    public void geocode(GeocodeInputData inputData) {
        try {
            List<Location> locations = searchLocations(inputData.getQuery(), inputData.getMaxResults());
            GeocodeOutputData outputData = new GeocodeOutputData(locations, false);
            outputBoundary.prepareSuccessView(outputData);
        } catch (IOException | InterruptedException e) {
            outputBoundary.prepareFailView("Failed to geocode location: " + e.getMessage());
        }
    }

    // --- helpers -----------------------------------------------------------

    /**
     * Find the first group(1) of matcher whose match region starts at or after
     * fromIndex. Returns null if no further match is found.
     */
    private static String findNextMatch(Matcher matcher, int fromIndex) {
        if (matcher.find(fromIndex)) {
            return matcher.group(1);
        }
        return null;
    }

    /**
     * Prefer full address (house + street + locality) when available.
     * Fallbacks:
     *   - name (e.g. "CN Tower")
     *   - label (generic ORS label)
     */
    private static String buildDisplayName(String house,
                                           String street,
                                           String locality,
                                           String name,
                                           String label) {
        // Full address: "170 Jane Street, Toronto"
        if (street != null && !street.isBlank()) {
            StringBuilder sb = new StringBuilder();
            if (house != null && !house.isBlank()) {
                sb.append(house).append(" ");
            }
            sb.append(street);
            if (locality != null && !locality.isBlank()) {
                sb.append(", ").append(locality);
            }
            return sb.toString();
        }

        // POI / building name (e.g. CN Tower)
        if (name != null && !name.isBlank()) {
            return name;
        }

        // Generic label as last fallback
        if (label != null && !label.isBlank()) {
            return label;
        }

        // Should not usually happen, but keep something non-empty
        return "Unknown location";
    }

    /**
     * Rank suggestions so that better textual matches to the query appear first.
     */
    private static List<Location> rankAndTrim(List<Location> locations,
                                              String query,
                                              int maxResults) {
        if (locations.isEmpty()) {
            return locations;
        }

        String q = query == null ? "" : query.trim().toLowerCase();

        // Keep original index to make ordering stable.
        class Scored {
            final Location loc;
            final int score;
            final int originalIndex;

            Scored(Location loc, int score, int originalIndex) {
                this.loc = loc;
                this.score = score;
                this.originalIndex = originalIndex;
            }
        }

        List<Scored> scored = new ArrayList<>();
        for (int i = 0; i < locations.size(); i++) {
            Location loc = locations.get(i);
            String name = loc.getName().toLowerCase();
            int score;

            if (q.isEmpty()) {
                score = 3;
            } else if (name.equals(q)) {
                score = 0;           // exact match
            } else if (name.startsWith(q)) {
                score = 1;           // prefix match
            } else if (name.contains(q)) {
                score = 2;           // substring match
            } else {
                score = 3;           // other
            }

            scored.add(new Scored(loc, score, i));
        }

        scored.sort((a, b) -> {
            int byScore = Integer.compare(a.score, b.score);
            if (byScore != 0) return byScore;
            // Tie-breaker: shorter name first, then original index.
            int lenDiff = Integer.compare(a.loc.getName().length(), b.loc.getName().length());
            if (lenDiff != 0) return lenDiff;
            return Integer.compare(a.originalIndex, b.originalIndex);
        });

        List<Location> result = new ArrayList<>();
        for (int i = 0; i < scored.size() && i < maxResults; i++) {
            result.add(scored.get(i).loc);
        }
        return result;
    }
}