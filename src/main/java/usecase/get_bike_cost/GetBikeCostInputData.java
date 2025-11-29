package usecase.get_bike_cost;

/**
 * Input data for the GetBikeCost use case.
 * <p>
 */
public class GetBikeCostInputData {

    private final double bikeTimeMinutes;

    /**
     * Creates a new {@code GetBikeCostInputData} with the given biking time.
     *
     * @param bikeTimeMinutes the biking time in minutes
     */
    public GetBikeCostInputData(double bikeTimeMinutes) {
        this.bikeTimeMinutes = bikeTimeMinutes;
    }

    /**
     * Returns the biking time (in minutes).
     *
     * @return biking time in minutes
     */
    public double getBikeTimeMinutes() {
        return bikeTimeMinutes;
    }
}
