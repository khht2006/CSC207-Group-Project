package usecase.calculate_route;

public class CalculateRouteOutputData {
    private final double bikeTime;
    private final double walkTime;
    private final double bikeCost;
    private final String destinationName;

    public CalculateRouteOutputData(double bikeTime, double walkTime,
                                    double bikeCost, String destinationName) {
        this.bikeTime = bikeTime;
        this.walkTime = walkTime;
        this.bikeCost = bikeCost;
        this.destinationName = destinationName;
    }

    public double getBikeTime() { return bikeTime; }
    public double getWalkTime() { return walkTime; }
    public double getBikeCost() { return bikeCost; }
    public String getDestinationName() { return destinationName; }
}
