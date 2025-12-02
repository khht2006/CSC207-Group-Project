package usecase.select_route_locations;

public interface SelectRouteLocationsOutputBoundary {
    void prepareSuccessView(SelectRouteLocationsOutputData outputData);
    void prepareFailView(String error);
}
