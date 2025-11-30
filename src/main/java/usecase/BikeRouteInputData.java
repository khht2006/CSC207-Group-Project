package usecase;

/**
 * Input data containing coordinates for querying a cycling route.
 */
public class BikeRouteInputData {

    private final double originLat;
    private final double originLon;
    private final double destinationLat;
    private final double destinationLon;

    /**
     * Constructs a BikeRouteInputData instance.
     *
     * @param originLat origin latitude
     * @param originLon origin longitude
     * @param destinationLat destination latitude
     * @param destinationLon destination longitude
     */
    public BikeRouteInputData(
            double originLat,
            double originLon,
            double destinationLat,
            double destinationLon) {
        this.originLat = originLat;
        this.originLon = originLon;
        this.destinationLat = destinationLat;
        this.destinationLon = destinationLon;
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
}
