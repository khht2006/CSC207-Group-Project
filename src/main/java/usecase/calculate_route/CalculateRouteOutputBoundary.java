package usecase.calculate_route;

public interface CalculateRouteOutputBoundary {
    void prepareSuccessView(CalculateRouteOutputData outputData);
    void prepareFailView(String error);
}
