package usecase.calculate_route;

import entity.Location;
import entity.SearchRecord;
import interface_adapter.bike_time.GetBikeTimeViewModel;
import interface_adapter.search_history.SearchHistoryGateway;
import usecase.bike_route.BikeRouteInputData;
import usecase.bike_route.BikeRouteInteractor;
import usecase.walk_route.WalkRouteInteractor;

public class CalculateRouteInteractor implements CalculateRouteInputBoundary {

    private final BikeRouteInteractor bikeRoute;
    private final WalkRouteInteractor walkRoute;
    private final SearchHistoryGateway historyGateway;
    private final CalculateRouteOutputBoundary outputBoundary;
    private final GetBikeTimeViewModel bikeTimeViewModel;

    public CalculateRouteInteractor(
            BikeRouteInteractor bikeRoute,
            WalkRouteInteractor walkRoute,
            SearchHistoryGateway historyGateway,
            CalculateRouteOutputBoundary outputBoundary,
            GetBikeTimeViewModel bikeTimeViewModel) {
        this.bikeRoute = bikeRoute;
        this.walkRoute = walkRoute;
        this.historyGateway = historyGateway;
        this.outputBoundary = outputBoundary;
        this.bikeTimeViewModel = bikeTimeViewModel;
    }

    @Override
    public void execute(CalculateRouteInputData inputData) {
        Location origin = inputData.getOrigin();
        Location destination = inputData.getDestination();

        try {
            BikeRouteInputData bikeInputData = new BikeRouteInputData(
                    origin.getLatitude(), origin.getLongitude(),
                    destination.getLatitude(), destination.getLongitude(),
                    destination.getName()
            );
            bikeRoute.execute(bikeInputData);

            double bikeTime = bikeTimeViewModel.getBikeTimeValue();

            double walkTime = walkRoute.execute(
                    origin.getLatitude(), origin.getLongitude(),
                    destination.getLatitude(), destination.getLongitude()
            ).getTimeMinutes();

            double bikeCost = calculateBikeCost(bikeTime);

            SearchRecord searchRecord = new SearchRecord(
                    origin.getName(), destination.getName(),
                    bikeTime, bikeCost, walkTime
            );
            historyGateway.save(searchRecord);

            CalculateRouteOutputData outputData = new CalculateRouteOutputData(
                    bikeTime, walkTime, bikeCost, destination.getName()
            );
            outputBoundary.prepareSuccessView(outputData);

        } catch (Exception e) {
            outputBoundary.prepareFailView("Failed to calculate route: " + e.getMessage());
        }
    }

    private double calculateBikeCost(double bikeTimeMinutes) {
        final double costPerMinute = 0.12;
        final double unlockFee = 1.00;
        return unlockFee + (bikeTimeMinutes * costPerMinute);
    }
}
