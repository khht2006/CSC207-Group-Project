package usecase.fetch_location;

public interface GeocodeInputBoundary {
    void geocode(GeocodeInputData inputData);
    void findBestMatch(GeocodeInputData inputData);
}