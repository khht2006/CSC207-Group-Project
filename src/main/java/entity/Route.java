package entity;

/**
 * Route represents a travel path returned by OpenRouteService.
 * It contains distance (meters) and duration (seconds).
 */
public class Route {
    private final double distanceMeters;
    private final double durationSeconds;

    /**
     * Creates a new route.
     *
     * @param distanceMeters total distance in meters
     * @param durationSeconds total duration in seconds
     */
    public Route(double distanceMeters, double durationSeconds) {
        this.distanceMeters = distanceMeters;
        this.durationSeconds = durationSeconds;
    }

    public double getDistanceMeters() {
        return distanceMeters;
    }

    public double getDurationSeconds() {
        return durationSeconds;
    }
}
