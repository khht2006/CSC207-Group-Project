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
    public void search(String query, int maxResults) {
        // default maxResults to 5
        GeocodeInputData inputData = new GeocodeInputData(query, maxResults);
        interactor.geocode(inputData);
    }

    /**
     * Triggers finding the single best match.
     */
    public void findBest(String query) {
        GeocodeInputData inputData = new GeocodeInputData(query, 1);
        interactor.findBestMatch(inputData);
    }
}
