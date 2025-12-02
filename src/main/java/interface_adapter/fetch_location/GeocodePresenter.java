package interface_adapter.fetch_location;

import usecase.fetch_location.GeocodeOutputBoundary;
import usecase.fetch_location.GeocodeOutputData;
import interface_adapter.fetch_location.GeocodeViewModel; // Assuming the previous file was placed here
// If not, I will adjust the import. Based on the previous prompt I put it in interface_adapter.fetch_location
// but the user's view shows a flat structure. I will assume flat structure for now to match existing files
// like GetBikeCostPresenter.
// actually, let's look at GetBikeCostPresenter location. It is in interface_adapter.
// So I will put GeocodeViewModel there too.

public class GeocodePresenter implements GeocodeOutputBoundary {

    private final GeocodeViewModel viewModel;

    public GeocodePresenter(GeocodeViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void prepareSuccessView(GeocodeOutputData outputData) {
        // Update the view model with the list of locations found
        viewModel.setErrorMessage(null);
        viewModel.setLocations(outputData.getLocations());
    }

    @Override
    public void prepareFailView(String error) {
        // Update the view model with the error message
        viewModel.setErrorMessage(error);
        // Optionally clear locations or keep previous ones
        // viewModel.setLocations(new ArrayList<>());
    }
}