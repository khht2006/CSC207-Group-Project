package usecase.fetch_location;

import api.ApiFetcher;
import entity.Location;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

class GeocodeLocationInteractorTest {

    @Test
    void geocodeSuccess_parsesLocationCorrectly() {
        // JSON must have coordinates BEFORE properties
        String sampleJson = """
                {
                  "features": [
                    {
                      "geometry": {
                        "coordinates": [-79.4005, 43.6571]
                      },
                      "properties": {
                        "housenumber": "487",
                        "street": "Spadina Avenue",
                        "locality": "Toronto",
                        "label": "487 Spadina Avenue, Toronto"
                      }
                    }
                  ]
                }
                """;

        StubApiFetcher api = new StubApiFetcher(sampleJson);
        CapturingPresenter presenter = new CapturingPresenter();
        GeocodeLocationInteractor interactor = new GeocodeLocationInteractor(api, presenter);

        GeocodeInputData input = new GeocodeInputData("spadina", 5);

        // When
        interactor.geocode(input);

        // Then
        Assertions.assertNotNull(presenter.successData, "Presenter should receive success output");
        Assertions.assertNull(presenter.failMessage, "No failure should occur");

        List<Location> locations = presenter.successData.getLocations();
        Assertions.assertEquals(1, locations.size());

        Location loc = locations.get(0);
        Assertions.assertEquals("487 Spadina Avenue, Toronto", loc.getName());
        Assertions.assertEquals(43.6571, loc.getLatitude(), 1e-6);
        Assertions.assertEquals(-79.4005, loc.getLongitude(), 1e-6);
    }

    @Test
    void geocodeFailure_callsFailView() {
        StubFailingApiFetcher failingApi = new StubFailingApiFetcher();
        CapturingPresenter presenter = new CapturingPresenter();
        GeocodeLocationInteractor interactor = new GeocodeLocationInteractor(failingApi, presenter);

        GeocodeInputData input = new GeocodeInputData("anything", 5);

        interactor.geocode(input);

        Assertions.assertNull(presenter.successData, "No success output expected");
        Assertions.assertNotNull(presenter.failMessage, "Failure message should exist");
        Assertions.assertTrue(presenter.failMessage.contains("Failed to geocode"));
    }


    // -------------------------------------------------------------------------
    // Stubs
    // -------------------------------------------------------------------------

    private static class StubApiFetcher extends ApiFetcher {
        private final String json;

        StubApiFetcher(String json) {
            this.json = json;
        }

        @Override
        public String fetchGeocodeJson(String text, int maxResults) throws IOException {
            return json;
        }
    }

    private static class StubFailingApiFetcher extends ApiFetcher {
        @Override
        public String fetchGeocodeJson(String text, int maxResults) throws IOException {
            throw new IOException("Simulated API error");
        }
    }

    private static class CapturingPresenter implements GeocodeOutputBoundary {
        GeocodeOutputData successData;
        String failMessage;

        @Override
        public void prepareSuccessView(GeocodeOutputData outputData) {
            this.successData = outputData;
        }

        @Override
        public void prepareFailView(String error) {
            this.failMessage = error;
        }
    }
}
