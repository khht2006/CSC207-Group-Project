package usecase.select_route_locations;

import entity.Location;

public class SelectRouteLocationsOutputData {
    private final Location origin;
    private final Location destination;

    public SelectRouteLocationsOutputData(Location origin, Location destination) {
        this.origin = origin;
        this.destination = destination;
    }

    public Location getOrigin() { return origin; }
    public Location getDestination() { return destination; }
}
