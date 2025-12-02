package interface_adapter.compare_summary;

import usecase.compare_summary.CompareSummaryInputBoundary;
import usecase.compare_summary.CompareSummaryInputData;

/**
 * Controller for the Compare Summary use case.
 * Mirrors the style of GetBikeTimeController: it adapts the UI-provided
 * latitude/longitude values into the interactor's input data and triggers execution.
 */
public class CompareSummaryController {

    private final CompareSummaryInputBoundary interactor;

    public CompareSummaryController(CompareSummaryInputBoundary interactor) {
        this.interactor = interactor;
    }

    /**
     * Controller for the Compare Summary use case.
     * @param walkMinutes walk time in minutes
     * @param bikeMinutes bike time in minutes
     * @param bikeCost bike cost in minutes
     */
    public void execute(double walkMinutes,
                        double bikeMinutes,
                        double bikeCost) {
        // CompareSummaryInputData constructor expects (originLon, originLat, destinationLon, destinationLat)
        final CompareSummaryInputData inputData = new CompareSummaryInputData(
                walkMinutes, bikeMinutes, bikeCost);
        interactor.execute(inputData);
    }
}