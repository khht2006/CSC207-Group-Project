package main.java.interface_adapter;
import main.java.usecase.get_bike_cost.GetBikeCostOutputBoundary;
import main.java.usecase.get_bike_cost.GetBikeCostOutputData;

public class GetBikeCostPresenter implements GetBikeCostOutputBoundary{
    private final GetBikeCostViewModel viewModel;
    public GetBikeCostPresenter(GetBikeCostViewModel viewModel) {
        this.viewModel = viewModel;
    }

    public void present(GetBikeCostOutputData outputData) {
        viewModel.setBikeCostText(String.format("$%.2f",outputData.getBikeCost()));
    }
    public GetBikeCostViewModel getViewModel() {
        return viewModel;

    }
}
