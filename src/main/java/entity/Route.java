package entity;

import java.util.List;
import java.util.Collections;

public class Route {
    private final double distanceMeters;
    private final double durationSeconds;
    private List<String> turnInstructions;

    public Route(double distanceMeters, double durationSeconds, List<String> turnInstructions) {
        this.distanceMeters = distanceMeters;
        this.durationSeconds = durationSeconds;
        this.turnInstructions = turnInstructions == null ? Collections.emptyList() : turnInstructions;
    }

    public double getDistanceMeters() {
        return distanceMeters;
    }

    public double getDurationSeconds() {
        return durationSeconds;
    }

    public List<String> getTurnInstructions() {
        return turnInstructions;
    }
}
