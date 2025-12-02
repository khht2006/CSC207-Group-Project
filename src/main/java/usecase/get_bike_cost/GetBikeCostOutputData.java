package usecase.get_bike_cost;

/**
 * Output data for the GetBikeCost use case.
 */
public class GetBikeCostOutputData {

    /** The computed bike cost. */
    private final double bikeCost;

    /**
     * Creates a new {@code GetBikeCostOutputData} with the given bike cost.
     *
     * @param bikeCost the computed cost of biking
     */
    public GetBikeCostOutputData(double bikeCost) {
        this.bikeCost = bikeCost;
    }

    /**
     * Returns the computed bike cost.
     *
     * @return the bike cost
     */
    public double getBikeCost() {
        return bikeCost;
    }
}
