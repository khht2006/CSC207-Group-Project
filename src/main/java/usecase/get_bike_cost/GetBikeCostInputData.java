package main.java.usecase.get_bike_cost;

public class GetBikeCostInputData {
    private final double bikeTimeMinutes;

    public GetBikeCostInputData(double bikeTimeMinutes) {
        this.bikeTimeMinutes = bikeTimeMinutes;
    }

    public double getBikeTimeMinutes() {
        return bikeTimeMinutes;
    }
}
