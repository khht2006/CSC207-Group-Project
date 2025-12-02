package interface_adapter.calculate_route;

public class CalculateRouteViewModel {
    private double bikeTime;
    private double walkTime;
    private double bikeCost;
    private String destinationName;
    private String error;

    public double getBikeTime() { return bikeTime; }
    public void setBikeTime(double bikeTime) { this.bikeTime = bikeTime; }

    public double getWalkTime() { return walkTime; }
    public void setWalkTime(double walkTime) { this.walkTime = walkTime; }

    public double getBikeCost() { return bikeCost; }
    public void setBikeCost(double bikeCost) { this.bikeCost = bikeCost; }

    public String getDestinationName() { return destinationName; }
    public void setDestinationName(String destinationName) { this.destinationName = destinationName; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
}
