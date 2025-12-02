package interface_adapter;

import usecase.BikeRouteInputBoundary;
import usecase.BikeRouteInputData;

// Controller for requesting biking time given origin/destination coordinates.
public class GetBikeTimeController {

    private final BikeRouteInputBoundary interactor;

    public GetBikeTimeController(BikeRouteInputBoundary interactor) {
        this.interactor = interactor;
    }

    // Triggers calculation of cycling time for the provided coordinates.
    public void execute(double originLat, double originLon,
                        double destinationLat, double destinationLon,
                        String destinationName) {
        BikeRouteInputData inputData = new BikeRouteInputData(
                originLat, originLon, destinationLat, destinationLon, destinationName);
        interactor.execute(inputData);
    }
}
