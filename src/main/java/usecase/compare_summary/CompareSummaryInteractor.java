package usecase.compare_summary;

import interface_adapter.compare_summary.CompareSummaryPresenter;

/**
 * Simplified CompareSummaryInteractor that expects both walk and bike durations
 * to be provided in the input data. It performs the core calculation (walk - bike),
 * writes formatted values into the CompareViewModel via the presenter, and
 * delegates bike cost calculation to GetBikeCostInteractor.
 * This version intentionally omits exception handling and validation.
 */
public class CompareSummaryInteractor implements CompareSummaryInputBoundary {

    private final CompareSummaryOutputBoundary presenter;

    public CompareSummaryInteractor(CompareSummaryOutputBoundary presenter) {
        this.presenter = presenter;
    }

    @Override
    public void execute(CompareSummaryInputData inputData) {
        final double walkMinutes = inputData.getWalkMinutes();
        final double bikeMinutes = inputData.getBikeMinutes();
        final double bikeCost = inputData.getBikeCost();

        final double diffInMinutes = walkMinutes - bikeMinutes;

        final CompareSummaryOutputData outputData = new CompareSummaryOutputData(walkMinutes, bikeMinutes,
                    bikeCost, diffInMinutes);
        presenter.present(outputData);
    }
}