package usecase.calculate_route;

import entity.Location;

public class CalculateRouteInputData {
    private final Location origin;
    private final Location destination;

    public CalculateRouteInputData(Location origin, Location destination) {
        this.origin = origin;
        this.destination = destination;
    }

    public Location getOrigin() { return origin; }
    public Location getDestination() { return destination; }
}
