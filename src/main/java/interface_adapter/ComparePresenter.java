package interface_adapter;

import usecase.BikeRouteOutputBoundary;
import usecase.BikeRouteOutputData;
import usecase.get_bike_cost.GetBikeCostOutputBoundary;
import usecase.get_bike_cost.GetBikeCostOutputData;


/**
 * The Presenter for the Compare Summary case.
 */
public class ComparePresenter implements BikeRouteOutputBoundary, GetBikeCostOutputBoundary {

    private final CompareViewModel viewModel;

    public ComparePresenter(CompareViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void present(BikeRouteOutputData outputData) {
        double minutes = outputData.getTotalDurationMinutes();
        viewModel.setBikeTimeText(minutes);
    }

    @Override
    public void present(GetBikeCostOutputData outputData) {
        double cost = outputData.getBikeCost();
        viewModel.setBikeCostText(String.format("$%.2f", cost));
    }

    public CompareViewModel getViewModel() {
        return viewModel;
    }
}