package usecase.geocode;

public interface GeocodeOutputBoundary {
    void prepareSuccessView(GeocodeOutputData outputData);
    void prepareFailView(String error);
}