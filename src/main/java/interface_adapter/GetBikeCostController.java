package interface_adapter;

import usecase.get_bike_cost.GetBikeCostInputBoundary;
import usecase.get_bike_cost.GetBikeCostInputData;

/**
 * Controller for the GetBikeCost use case.
 * <p>
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
     */
    public void calculateCost() {
        String timeText = timeViewModel.getBikeTimeText();

        String numberOnly = timeText
                .replace("Bike Time:", "")
                .replace("minutes", "")
                .trim();

        double minutes = Double.parseDouble(numberOnly);

        GetBikeCostInputData inputData = new GetBikeCostInputData(minutes);
        interactor.execute(inputData);
    }
}
