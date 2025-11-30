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
        this.origin = origin;
        this.destination = destination;
        this.bikeTime = bikeTime;
        this.bikeCost = bikeCost;
        this.walkTime = walkTime;
    }

    public String getOrigin() { return origin; }
    public String getDestination() { return destination; }
    public double getBikeTime() { return bikeTime; }
    public double getBikeCost() { return bikeCost; }
    public double getWalkTime() { return walkTime; }
}
