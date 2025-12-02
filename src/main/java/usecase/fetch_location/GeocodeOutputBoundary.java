package usecase.fetch_location;

public interface GeocodeOutputBoundary {
    void prepareSuccessView(GeocodeOutputData outputData);
    void prepareFailView(String error);
}