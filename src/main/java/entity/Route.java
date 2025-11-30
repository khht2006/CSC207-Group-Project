package entity;

public class Route {
    private final double distanceMeters;
    private final double durationSeconds;

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
