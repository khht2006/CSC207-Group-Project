package interface_adapter;

import usecase.get_bike_cost.GetBikeCostOutputBoundary;
import usecase.get_bike_cost.GetBikeCostOutputData;

/**
 * Presenter for the GetBikeCost use case.
 * Formats the computed bike cost and updates the {@link GetBikeCostViewModel}.
 */
public class GetBikeCostPresenter implements GetBikeCostOutputBoundary {

    private final GetBikeCostViewModel viewModel;

    /**
     * Creates a new {@code GetBikeCostPresenter}.
     *
     * @param viewModel the view model to update with the formatted cost
     */
    public GetBikeCostPresenter(GetBikeCostViewModel viewModel) {
        this.viewModel = viewModel;
    }

    /**
     * Formats the bike cost and stores it in the view model.
     *
     * @param outputData the data containing the computed bike cost
     */
    @Override
    public void present(GetBikeCostOutputData outputData) {
        viewModel.setBikeCostText(String.format("$%.2f",outputData.getBikeCost()));
        viewModel.setBoldBikeCostValue(outputData.getBikeCost());
    }

    /**
     * Returns the view model used by this presenter.
     *
     * @return the bike cost view model
     */
    public GetBikeCostViewModel getViewModel() {
        return viewModel;
    }
}
