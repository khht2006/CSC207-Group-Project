package usecase.compare_summary;

/**
 * Input boundary for the Compare Summary use case.
 */
public interface CompareSummaryInputBoundary {
    /**
     * Executes the use case using the given input data.
     * @param inputData the data containing the walk and bike travel time
     */
    void execute(CompareSummaryInputData inputData);
}
