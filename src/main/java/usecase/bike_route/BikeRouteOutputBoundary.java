package usecase.bike_route;

/**
 * Output boundary for presenting cycling route duration results.
 */
public interface BikeRouteOutputBoundary {

    /**
     * Presents the output data containing cycling route duration information.
     *
     * @param outputData data to be presented
     */
    void present(BikeRouteOutputData outputData);
}
