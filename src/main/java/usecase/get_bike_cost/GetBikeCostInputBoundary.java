package usecase.get_bike_cost;

/**
 * Input boundary for the GetBikeCost use case.
 */
public interface GetBikeCostInputBoundary {

    /**
     * Executes the use case using the given input data.
     *
     * @param inputData the data containing the bike travel time
     */
    void execute(GetBikeCostInputData inputData);
}
