package usecase.original_destination;

public interface OriginalDestinationOutputBoundary {
    void presentSwappedLocations(OriginalDestinationOutputData outputData);
    void presentError(String errorMessage);
}
