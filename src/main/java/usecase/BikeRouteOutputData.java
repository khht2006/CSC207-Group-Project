package usecase;

import entity.Route;
import entity.Station;

// Output data carrying the cycling duration in minutes.
public class BikeRouteOutputData {
    private final Route walkToStation;
    private final Station startStation;
    private final Route bikeSegment;
    private final Station endStation;
    private final Route walkFromStation;
    private final String destinationName;
    private final String errorMessage;

    public BikeRouteOutputData(Route walkToStation, Station startStation,
                               Route bikeSegment, Station endStation,
                               Route walkFromStation, String destinationName) {
        this.walkToStation = walkToStation;
        this.startStation = startStation;
        this.bikeSegment = bikeSegment;
        this.endStation = endStation;
        this.walkFromStation = walkFromStation;
        this.errorMessage = null;
        this.destinationName = destinationName;
    }

    public BikeRouteOutputData(String errorMessage) {
        this.walkToStation = null;
        this.startStation = null;
        this.bikeSegment = null;
        this.endStation = null;
        this.walkFromStation = null;
        this.destinationName = null;
        this.errorMessage = errorMessage;
    }

    public Route getWalkToStation() {
        return walkToStation;
    }

    public Station getStartStation() {
        return startStation;
    }

    public Route getBikeSegment() {
        return bikeSegment;
    }

    public Station getEndStation() {
        return endStation;
    }

    public Route getWalkFromStation() {
        return walkFromStation;
    }

    public String getDestinationName() {
        return destinationName;
    }

    public double getTotalDurationMinutes() {
        if (walkToStation == null || bikeSegment == null || walkFromStation == null) {
            return 0.0;
        }
        double totalSeconds = walkToStation.getDurationSeconds()
                + bikeSegment.getDurationSeconds()
                + walkFromStation.getDurationSeconds();
        return totalSeconds / 60.0;
    }

    public double getCyclingDurationMinutes() {
        return bikeSegment != null ? bikeSegment.getDurationSeconds() / 60.0 : 0.0;
    }

    /**
     * Returns the error message, if any.
     *
     * @return error message or {@code null}
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Returns whether an error occurred.
     *
     * @return {@code true} if an error is present, {@code false} otherwise
     */
    public boolean hasError() {
        return errorMessage != null && !errorMessage.isBlank();
    }
}
