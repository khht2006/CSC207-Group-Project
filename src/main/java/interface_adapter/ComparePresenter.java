package interface_adapter;

import usecase.BikeRouteOutputBoundary;
import usecase.BikeRouteOutputData;

import usecase.get_bike_cost.GetBikeCostOutputData;
import usecase.get_bike_cost.GetBikeCostOutputBoundary;

/**
 * The Presenter for the Compare Summary case.
 */

public class ComparePresenter implements BikeRouteOutputBoundary, GetBikeCostOutputBoundary {
    private final CompareViewModel viewModel;

    public ComparePresenter(CompareViewModel viewModel) {
        this.viewModel = viewModel;
    }

    public void present(BikeRouteOutputData outputData) {
            double minutes = outputData.getDurationMinutes();
            viewModel.setBikeTimeText(String.format("Bike Time: %.1f minutes", minutes));
    }

    public void present(GetBikeCostOutputData outputData) {
        viewModel.setBikeCostText(String.format("$%.2f",outputData.getBikeCost()));
    }

    public CompareViewModel getViewModel() {
        return viewModel;
    }
}