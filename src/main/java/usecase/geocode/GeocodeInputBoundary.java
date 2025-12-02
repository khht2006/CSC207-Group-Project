package usecase.geocode;

public interface GeocodeInputBoundary {
    void geocode(GeocodeInputData inputData);
    void findBestMatch(GeocodeInputData inputData);
}