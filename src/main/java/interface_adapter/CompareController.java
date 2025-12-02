package interface_adapter;

import usecase.compare_summary.CompareSummaryInputBoundary;
import usecase.compare_summary.CompareSummaryInputData;

/**
 * Controller for the Compare Summary use case.
 * Mirrors the style of GetBikeTimeController: it adapts the UI-provided
 * latitude/longitude values into the interactor's input data and triggers execution.
 */
public class CompareController {

    private final CompareSummaryInputBoundary interactor;

    public CompareController(CompareSummaryInputBoundary interactor) {
        this.interactor = interactor;
    }

    // Triggers the compare-summary use case for the provided coordinates.
    public void execute(double walkMinutes,
                        double bikeMinutes,
                        double bikeCost) {
        // CompareSummaryInputData constructor expects (originLon, originLat, destinationLon, destinationLat)
        final CompareSummaryInputData inputData = new CompareSummaryInputData(
                walkMinutes, bikeMinutes, bikeCost);
        interactor.execute(inputData);
    }
}