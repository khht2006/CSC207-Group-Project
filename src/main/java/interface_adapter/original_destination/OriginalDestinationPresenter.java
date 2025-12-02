package interface_adapter.original_destination;

import usecase.original_destination.OriginalDestinationOutputBoundary;
import usecase.original_destination.OriginalDestinationOutputData;

public class OriginalDestinationPresenter implements OriginalDestinationOutputBoundary {
    private final OriginalDestinationViewModel viewModel;

    public OriginalDestinationPresenter(OriginalDestinationViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void presentSwappedLocations(OriginalDestinationOutputData outputData) {
        viewModel.setOriginText(outputData.getOriginText());
        viewModel.setDestinationText(outputData.getDestinationText());
    }

    @Override
    public void presentError(String errorMessage) {
        viewModel.setErrorMessage(errorMessage);
    }
}
