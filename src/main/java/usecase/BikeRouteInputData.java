package usecase;

/**
 * Coordinates for querying a cycling route.
 */
public class BikeRouteInputData {
    private final double originLat;
    private final double originLon;
    private final double destinationLat;
    private final double destinationLon;

    public BikeRouteInputData(double originLat, double originLon,
                              double destinationLat, double destinationLon) {
        this.originLat = originLat;
        this.originLon = originLon;
        this.destinationLat = destinationLat;
        this.destinationLon = destinationLon;
    }

    public double getOriginLat() {
        return originLat;
    }

    public double getOriginLon() {
        return originLon;
    }

    public double getDestinationLat() {
        return destinationLat;
    }

    public double getDestinationLon() {
        return destinationLon;
    }
}
