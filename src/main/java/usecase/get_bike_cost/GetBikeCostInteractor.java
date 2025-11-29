package usecase.get_bike_cost;

/**
 * Interactor for calculating the total Bike Share cost.
 * <p>
 * Uses the bike travel time from the input data to compute the cost and
 * passes the result to the output boundary.
 */
public class GetBikeCostInteractor implements GetBikeCostInputBoundary {

    private static final double BIKE_UNLOCK_COST = 1.00;
    private static final double BIKE_MINUTE_COST = 0.12;

    private final GetBikeCostOutputBoundary presenter;

    /**
     * Creates a new {@code GetBikeCostInteractor} with the given presenter.
     *
     * @param presenter the output boundary that will receive the computed cost
     */
    public GetBikeCostInteractor(GetBikeCostOutputBoundary presenter) {
        this.presenter = presenter;
    }

    /**
     * Computes the bike cost and forwards the result.
     *
     * @param inputData the input containing the bike travel time in minutes
     */
    @Override
    public void execute(GetBikeCostInputData inputData) {
        double timeInMinutes = inputData.getBikeTimeMinutes();
        double totalCost = BIKE_UNLOCK_COST + BIKE_MINUTE_COST * timeInMinutes;

        GetBikeCostOutputData outputData = new GetBikeCostOutputData(totalCost);
        presenter.present(outputData);
    }
}
