package interface_adapter.select_route_locations;

import usecase.select_route_locations.SelectRouteLocationsOutputBoundary;
import usecase.select_route_locations.SelectRouteLocationsOutputData;
import entity.Location;
import java.util.function.BiConsumer;
import javax.swing.JOptionPane;

public class SelectRouteLocationsPresenter implements SelectRouteLocationsOutputBoundary {

    private final BiConsumer<Location, Location> onSuccess;

    public SelectRouteLocationsPresenter(BiConsumer<Location, Location> onSuccess) {
        this.onSuccess = onSuccess;
    }

    @Override
    public void prepareSuccessView(SelectRouteLocationsOutputData outputData) {
        // Validation passed → trigger navigation callback
        onSuccess.accept(outputData.getOrigin(), outputData.getDestination());
    }

    @Override
    public void prepareFailView(String error) {
        // Show validation error dialog
        JOptionPane.showMessageDialog(null, error, "Validation Error", JOptionPane.WARNING_MESSAGE);
    }
}
