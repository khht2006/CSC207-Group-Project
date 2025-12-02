package entity;

/**
 * A search history record containing the user's origin, destination,
 * and the computed trip results.
 */
public class SearchRecord {
    private final String origin;
    private final String destination;
    private final double bikeTime;
    private final double bikeCost;
    private final double walkTime;
    private final double timeSavedMinutes;

    /**
     * Creates a new {@code SearchRecord} with the given trip information.
     *
     * @param origin      the starting location
     * @param destination the ending location
     * @param bikeTime    the biking time in minutes
     * @param bikeCost    the biking cost
     * @param walkTime    the walking time in minutes
     */
    public SearchRecord(String origin, String destination,
                        double bikeTime, double bikeCost, double walkTime) {
        this(origin, destination, bikeTime, bikeCost, walkTime, walkTime - bikeTime);
    }

    /**
     * Creates a new {@code SearchRecord} with the given trip information, including time saved.
     *
     * @param origin           the starting location
     * @param destination      the ending location
     * @param bikeTime         the biking time in minutes
     * @param bikeCost         the biking cost
     * @param walkTime         the walking time in minutes
     * @param timeSavedMinutes the time saved by biking (walk - bike) in minutes
     */
    public SearchRecord(String origin, String destination,
                        double bikeTime, double bikeCost, double walkTime,
                        double timeSavedMinutes) {
        this.origin = origin;
        this.destination = destination;
        this.bikeTime = bikeTime;
        this.bikeCost = bikeCost;
        this.walkTime = walkTime;
        this.timeSavedMinutes = timeSavedMinutes;
    }

    /**
     * @return the origin location
     */
    public String getOrigin() {
        return origin;
    }

    /**
     * @return the destination location
     */
    public String getDestination() {
        return destination;
    }

    /**
     * @return the biking time in minutes
     */
    public double getBikeTime() {
        return bikeTime;
    }

    /**
     * @return the biking cost
     */
    public double getBikeCost() {
        return bikeCost;
    }

    /**
     * @return the walking time in minutes
     */
    public double getWalkTime() {
        return walkTime;
    }

    /**
     * @return the time saved by biking in minutes
     */
    public double getTimeSavedMinutes() {
        return timeSavedMinutes;
    }
}
