package usecase.original_destination;

import entity.Location;
import java.util.List;

public class OriginalDestinationInteractor implements OriginalDestinationInputBoundary {

    private final OriginalDestinationOutputBoundary presenter;

    private Location selectedOrigin;
    private Location selectedDestination;
    private List<Location> availableLocations;

    public OriginalDestinationInteractor(OriginalDestinationOutputBoundary presenter) {
        this.presenter = presenter;
    }

    @Override
    public void swapLocations(OriginalDestinationInputData inputData) {
        Location temp = selectedOrigin;
        selectedOrigin = selectedDestination;
        selectedDestination = temp;

        String originName = selectedOrigin != null ? selectedOrigin.getName() : "";
        String destName = selectedDestination != null ? selectedDestination.getName() : "";

        OriginalDestinationOutputData outputData =
                new OriginalDestinationOutputData(originName, destName);
        presenter.presentSwappedLocations(outputData);
    }

    @Override
    public void selectOrigin(String locationName) {
        selectedOrigin = findLocationByName(locationName);
    }

    @Override
    public void selectDestination(String locationName) {
        selectedDestination = findLocationByName(locationName);
    }

    public void updateAvailableLocations(List<Location> locations) {
        this.availableLocations = locations;
    }

    public Location getSelectedOrigin() {
        return selectedOrigin;
    }

    public Location getSelectedDestination() {
        return selectedDestination;
    }

    private Location findLocationByName(String name) {
        if (availableLocations == null) {
            return null;
        }

        return availableLocations.stream()
                .filter(loc -> loc.getName().equals(name))
                .findFirst()
                .orElse(null);
    }
}
