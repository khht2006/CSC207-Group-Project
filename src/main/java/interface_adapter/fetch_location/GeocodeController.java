package interface_adapter.fetch_location;

import usecase.fetch_location.GeocodeInputBoundary;
import usecase.fetch_location.GeocodeInputData;

public class GeocodeController {

    private final GeocodeInputBoundary interactor;

    public GeocodeController(GeocodeInputBoundary interactor) {
        this.interactor = interactor;
    }

    /**
     * Triggers the geocoding search.
     * @param query The free-text query from the user.
     */
    public void search(String query) {
        // default maxResults to 5 or 10
        GeocodeInputData inputData = new GeocodeInputData(query, 5);
        interactor.geocode(inputData);
    }
}
