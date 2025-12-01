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

    /**
     * Send signal to interactor to calculate bike time between origin and destination.
     * @param originLat origin latitude coord
     * @param originLon origin longitude coord
     * @param destinationLat destination lat coord
     * @param destinationLon destination lon coord
     */
    public void execute(double originLat,
                        double originLon,
                        double destinationLat,
                        double destinationLon) {
        final BikeRouteInputData inputData = new BikeRouteInputData(
                originLat, originLon, destinationLat, destinationLon);
        interactor.execute(inputData);
    }
}
