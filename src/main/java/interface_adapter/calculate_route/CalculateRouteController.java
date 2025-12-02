package interface_adapter.calculate_route;

import entity.Location;
import usecase.calculate_route.CalculateRouteInputBoundary;
import usecase.calculate_route.CalculateRouteInputData;

public class CalculateRouteController {

    private final CalculateRouteInputBoundary interactor;

    public CalculateRouteController(CalculateRouteInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void execute(Location origin, Location destination) {
        CalculateRouteInputData inputData = new CalculateRouteInputData(origin, destination);
        interactor.execute(inputData);
    }
}
