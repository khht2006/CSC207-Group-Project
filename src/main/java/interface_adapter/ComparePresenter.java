package interface_adapter;

import usecase.BikeRouteOutputBoundary;
import usecase.BikeRouteOutputData;
import usecase.get_bike_cost.GetBikeCostOutputBoundary;
import usecase.get_bike_cost.GetBikeCostOutputData;
import usecase.compare_summary.CompareSummaryOutputBoundary;
import usecase.compare_summary.CompareSummaryOutputData;

/**
 * The Presenter for the Compare Summary case.
 */
public class ComparePresenter implements CompareSummaryOutputBoundary {

    private final CompareViewModel viewModel;

    public ComparePresenter(CompareViewModel viewModel) {
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

    public CompareViewModel getViewModel() {
        return viewModel;
    }
}
