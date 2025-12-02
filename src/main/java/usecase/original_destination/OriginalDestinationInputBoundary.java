package usecase.original_destination;

public interface OriginalDestinationInputBoundary {
    void swapLocations(OriginalDestinationInputData inputData);
    void selectOrigin(String locationName);
    void selectDestination(String locationName);
}
