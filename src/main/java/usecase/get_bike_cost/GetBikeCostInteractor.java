package main.java.usecase.get_bike_cost;

public class GetBikeCostInteractor implements GetBikeCostInputBoundary {
    private static final double BIKE_UNLOCK_COST = 1.00;
    private static final double BIKE_MINUTE_COST = 0.12;

    private final GetBikeCostOutputBoundary presenter;

    public GetBikeCostInteractor(GetBikeCostOutputBoundary presenter) {
        this.presenter = presenter;
    }

    @Override
    public void execute(GetBikeCostInputData inputData) {
        double timeInMinutes = inputData.getBikeTimeMinutes();
        double totalCost = BIKE_UNLOCK_COST + BIKE_MINUTE_COST *timeInMinutes;
        GetBikeCostOutputData outputData = new GetBikeCostOutputData(totalCost);

        presenter.present(outputData);
    }
}
