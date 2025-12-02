package interface_adapter.compare_summary;

import usecase.compare_summary.CompareSummaryOutputBoundary;
import usecase.compare_summary.CompareSummaryOutputData;

/**
 * The Presenter for the Compare Summary case.
 */
public class CompareSummaryPresenter implements CompareSummaryOutputBoundary {

    private final CompareSummaryViewModel viewModel;

    public CompareSummaryPresenter(CompareSummaryViewModel viewModel) {
        this.viewModel = viewModel;
    }

    /**
     * Formats the output data and stores in view model.
     * @param outputData the data containing the computed bike cost
     */
    public void present(CompareSummaryOutputData outputData) {
        final double bikeMinutes = outputData.getBikeMinutes();
        final double walkMinutes = outputData.getWalkMinutes();
        final double bikeCost = outputData.getBikeCost();
        final double diffInMinutes = outputData.getDiffInMinutes();

        viewModel.setBikeTimeText(bikeMinutes);
        viewModel.setWalkTimeText(walkMinutes);
        viewModel.setBikeCostText(String.format("$%.2f", bikeCost));
        viewModel.setDiffInMinutesText(diffInMinutes);
    }

    public CompareSummaryViewModel getViewModel() {
        return viewModel;
    }
}
