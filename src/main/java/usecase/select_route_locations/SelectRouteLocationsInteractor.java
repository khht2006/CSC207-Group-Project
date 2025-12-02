package usecase.select_route_locations;

import entity.Location;

/**
 * Use case: validates that two locations are ready for route calculation.
 * Business rules:
 * - Both locations must be selected
 * - Origin and destination must be different
 */
public class SelectRouteLocationsInteractor implements SelectRouteLocationsInputBoundary {

    private final SelectRouteLocationsOutputBoundary outputBoundary;

    public SelectRouteLocationsInteractor(SelectRouteLocationsOutputBoundary outputBoundary) {
        this.outputBoundary = outputBoundary;
    }

    @Override
    public void execute(SelectRouteLocationsInputData inputData) {
        Location origin = inputData.getOrigin();
        Location destination = inputData.getDestination();

        // Business rule 1: both must be selected
        if (origin == null || destination == null) {
            outputBoundary.prepareFailView("Please select both origin and destination");
            return;
        }

        // Business rule 2: must be different locations
        if (origin.equals(destination)) {
            outputBoundary.prepareFailView("Origin and destination must be different");
            return;
        }

        // Success: locations are valid for route calculation
        SelectRouteLocationsOutputData outputData =
                new SelectRouteLocationsOutputData(origin, destination);
        outputBoundary.prepareSuccessView(outputData);
    }
}
