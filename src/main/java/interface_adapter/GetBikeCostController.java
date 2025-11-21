package interface_adapter;

import usecase.get_bike_cost.GetBikeCostInputBoundary;
import usecase.get_bike_cost.GetBikeCostInputData;


public class GetBikeCostController {
    private final GetBikeCostInputBoundary interactor;
    private final GetBikeTimeViewModel timeViewModel;

    public GetBikeCostController(GetBikeCostInputBoundary interactor, GetBikeTimeViewModel timeViewModel) {
        this.interactor = interactor;
        this.timeViewModel = timeViewModel;
    }

    /**
     * Calculate the total travel cost for biking.
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
