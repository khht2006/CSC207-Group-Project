package usecase.search_history;

public class SearchRecord {
    private final String origin;
    private final String destination;

    private final double bikeTime;
    private final double bikeCost;
    private final double walkTime;

    public SearchRecord(String origin, String destination,
                        double bikeTime, double bikeCost, double walkTime) {
        this.origin = origin;
        this.destination = destination;
        this.bikeTime = bikeTime;
        this.bikeCost = bikeCost;
        this.walkTime = walkTime;
    }

    public String getOrigin() {
        return origin;
    }

    public String getDestination() {
        return destination;
    }

    public double getBikeTime() {
        return bikeTime;
    }

    public double getBikeCost() {
        return bikeCost;
    }

    public double getWalkTime() {
        return walkTime;
    }
}
