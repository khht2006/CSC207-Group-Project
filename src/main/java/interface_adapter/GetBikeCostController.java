package interface_adapter;

import usecase.get_bike_cost.GetBikeCostInputBoundary;
import usecase.get_bike_cost.GetBikeCostInputData;

/**
 * Controller for the GetBikeCost use case.
 * Extracts the biking time from the view model, converts it to a numeric
 * value, and sends it to the interactor for cost calculation.
 */
public class GetBikeCostController {

    private final GetBikeCostInputBoundary interactor;
    private final GetBikeTimeViewModel timeViewModel;

    /**
     * Creates a new {@code GetBikeCostController}.
     *
     * @param interactor    the input boundary for running the use case
     * @param timeViewModel the view model containing the displayed bike time text
     */
    public GetBikeCostController(GetBikeCostInputBoundary interactor, GetBikeTimeViewModel timeViewModel) {
        this.interactor = interactor;
        this.timeViewModel = timeViewModel;
    }

    /**
     * Reads the biking time from the view model, parses it into minutes,
     * and sends the input data to the interactor for cost calculation.
     */
    public void calculateCost() {
        double bikeTime = timeViewModel.getBikeTimeValue();
        if (Double.isNaN(bikeTime) || bikeTime < 0) {
            bikeTime = 0.0;
        }
        interactor.execute(new GetBikeCostInputData(bikeTime));
    }
}
