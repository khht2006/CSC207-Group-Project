package interface_adapter.original_destination;

import usecase.original_destination.OriginalDestinationInputBoundary;
import usecase.original_destination.OriginalDestinationInputData;

public class OriginalDestinationController {
    private final OriginalDestinationInputBoundary interactor;

    public OriginalDestinationController(OriginalDestinationInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void swapLocations(String originName, String destinationName) {
        OriginalDestinationInputData inputData =
                new OriginalDestinationInputData(originName, destinationName);
        interactor.swapLocations(inputData);
    }

    public void selectOrigin(String locationName) {
        interactor.selectOrigin(locationName);
    }

    public void selectDestination(String locationName) {
        interactor.selectDestination(locationName);
    }
}
