package interface_adapter.geocode;

import entity.Location;
import usecase.geocode.GeocodeOutputBoundary;
import usecase.geocode.GeocodeOutputData;
import usecase.original_destination.OriginalDestinationInteractor;

import java.util.List;
import java.util.stream.Collectors;

public class GeocodePresenter implements GeocodeOutputBoundary {

    private final GeocodeViewModel viewModel;
    private final OriginalDestinationInteractor destinationInteractor;

    public GeocodePresenter(GeocodeViewModel viewModel,
                            OriginalDestinationInteractor destinationInteractor) {
        this.viewModel = viewModel;
        this.destinationInteractor = destinationInteractor;
    }

    @Override
    public void prepareSuccessView(GeocodeOutputData outputData) {
        List<Location> locations = outputData.getLocations();

        // Update interactor with available locations
        destinationInteractor.updateAvailableLocations(locations);

        // Update view model with string suggestions
        List<String> suggestions = locations.stream()
                .map(Location::getName)
                .collect(Collectors.toList());

        viewModel.setLocations(locations);
        viewModel.setSuggestions(suggestions);
    }

    @Override
    public void prepareFailView(String error) {
        viewModel.setErrorMessage(error);
    }
}
