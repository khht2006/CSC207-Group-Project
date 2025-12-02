package usecase.bike_route;

/**
 * Input data containing coordinates for querying a cycling route.
 */
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

    /**
     * Returns the origin latitude.
     *
     * @return origin latitude
     */
    public double getOriginLat() {
        return originLat;
    }

    /**
     * Returns the origin longitude.
     *
     * @return origin longitude
     */
    public double getOriginLon() {
        return originLon;
    }

    /**
     * Returns the destination latitude.
     *
     * @return destination latitude
     */
    public double getDestinationLat() {
        return destinationLat;
    }

    /**
     * Returns the destination longitude.
     *
     * @return destination longitude
     */
    public double getDestinationLon() {
        return destinationLon;
    }

    public String getDestinationName() {
        return destinationName;
    }
}
