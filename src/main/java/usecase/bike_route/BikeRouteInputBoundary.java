package usecase.bike_route;

/**
 * Input boundary for requesting a cycling route duration.
 */
public interface BikeRouteInputBoundary {

    /**
     * Executes the use case with the given input data.
     *
     * @param inputData input data containing route coordinates
     */
    void execute(BikeRouteInputData inputData);
}
