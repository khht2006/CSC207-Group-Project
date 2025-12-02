package interface_adapter.select_route_locations;

import entity.Location;
import usecase.select_route_locations.SelectRouteLocationsInputBoundary;
import usecase.select_route_locations.SelectRouteLocationsInputData;

public class SelectRouteLocationsController {

    private final SelectRouteLocationsInputBoundary interactor;

    public SelectRouteLocationsController(SelectRouteLocationsInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void execute(Location origin, Location destination) {
        SelectRouteLocationsInputData inputData =
                new SelectRouteLocationsInputData(origin, destination);
        interactor.execute(inputData);
    }
}
