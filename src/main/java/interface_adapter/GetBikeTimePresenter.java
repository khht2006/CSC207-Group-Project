package interface_adapter;

import usecase.BikeRouteOutputBoundary;
import usecase.BikeRouteOutputData;

// Presenter for biking time results.

public class GetBikeTimePresenter implements BikeRouteOutputBoundary {

    private final GetBikeTimeViewModel viewModel;

    public GetBikeTimePresenter(GetBikeTimeViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void present(BikeRouteOutputData outputData) {
        if (outputData.hasError()) {
            viewModel.setBikeTimeText("Bike Time: " + outputData.getErrorMessage());
            return;
        }

        double minutes = outputData.getDurationMinutes();
        viewModel.setBikeTimeText(String.format("Bike Time: %.1f minutes", minutes));
    }

    public GetBikeTimeViewModel getViewModel() {
        return viewModel;
    }
}
