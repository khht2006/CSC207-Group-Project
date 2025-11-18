package interface_adapter;

import usecase.get_bike_cost.GetBikeCostInputBoundary;
import usecase.get_bike_cost.GetBikeCostInputData;

public class GetBikeCostController {
    private final GetBikeCostInputBoundary interactor;
//  private final GetBikeTimeViewModel timeViewModel; // Nee to get the bike time from timeViewModel

    public GetBikeCostController(GetBikeCostInputBoundary interactor) {
        this.interactor = interactor;
//        this.timeViewModel = timeViewModel;
    }

//    public void execute() {
//        double bikeTime = timeViewModel.getBikeTimeMinutes();
//        GetBikeCostInputData getBikeCostInputData = new GetBikeCostInputData(bikeTime);
//        interactor.execute(inputData);
//    }
}
