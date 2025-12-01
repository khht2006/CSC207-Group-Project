package usecase;

// Coordinates for querying a cycling route.
public class BikeRouteInputData {
    private final double originLat;
    private final double originLon;
    private final double destinationLat;
    private final double destinationLon;
    private final String destinationName;

    public BikeRouteInputData(double originLat, double originLon,
                              double destinationLat, double destinationLon,
                              String destinationName) {
        this.originLat = originLat;
        this.originLon = originLon;
        this.destinationLat = destinationLat;
        this.destinationLon = destinationLon;
        this.destinationName = destinationName;
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

    public String getDestinationName() {
        return destinationName;
    }
}
