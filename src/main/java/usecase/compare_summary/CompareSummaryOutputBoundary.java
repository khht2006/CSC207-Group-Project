package usecase.compare_summary;

import usecase.get_bike_cost.GetBikeCostOutputData;

/**
 * Output boundary for the Compare Summary use case.
 */
public interface CompareSummaryOutputBoundary {
    /**
     * Presents the result of the Compare Summary use case.
     * @param outputData the data containing the computed bike cost
     */
    void present(CompareSummaryOutputData outputData);
}
