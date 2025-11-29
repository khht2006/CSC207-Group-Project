package usecase.get_bike_cost;

/**
 * Output boundary for the GetBikeCost use case.
 * <p>
 * Implementations of this interface receive the computed bike cost.
 */
public interface GetBikeCostOutputBoundary {

    /**
     * Presents the result of the GetBikeCost use case.
     *
     * @param outputData the data containing the computed bike cost
     */
    void present(GetBikeCostOutputData outputData);
}
