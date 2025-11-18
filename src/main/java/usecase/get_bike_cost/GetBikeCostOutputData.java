package usecase.get_bike_cost;

public class GetBikeCostOutputData {
    private final double bikeCost;

    public GetBikeCostOutputData(double bikeCost) {
        this.bikeCost = bikeCost;
    }
    public double getBikeCost() {
        return bikeCost;
    }
}
