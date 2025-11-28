package usecase;

import api.ApiFetcher;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class BikeRouteInteractorTest {
    @Test
    void parsesDurationMinutesFromOrsJson() {
        // Given ORS-like directions JSON with a duration in seconds
        String sampleJson = """
                {
                  "type": "FeatureCollection",
                  "features": [
                    {
                      "type": "Feature",
                      "properties": {
                        "summary": {
                          "distance": 1234.5,
                          "duration": 1200.5
                        }
                      }
                    }
                  ]
                }
                """;

        StubApiFetcher apiFetcher = new StubApiFetcher(sampleJson);
        CapturingPresenter presenter = new CapturingPresenter();
        BikeRouteInteractor interactor = new BikeRouteInteractor(apiFetcher, presenter);

        BikeRouteInputData input = new BikeRouteInputData(43.0, -79.0, 43.1, -79.1);

        // When
        interactor.execute(input);

        // Then
        Assertions.assertNotNull(presenter.captured, "Presenter should receive output");
        Assertions.assertFalse(presenter.captured.hasError(), "Output should not have error");
        double expectedMinutes = 1200.5 / 60.0;
        Assertions.assertEquals(expectedMinutes, presenter.captured.getDurationMinutes(), 1e-6);
    }

    private static StubApiFetcher getStubApiFetcher() {
        String sampleJson = """
                {
                  "type": "FeatureCollection",
                  "features": [
                    {
                      "type": "Feature",
                      "properties": {
                        "summary": {
                          "distance": 1234.5,
                          "duration": 1200.5
                        }
                      }
                    }
                  ]
                }
                """;
        
        return new StubApiFetcher(sampleJson);
    }

    private static class StubApiFetcher extends ApiFetcher {
        private final String json;

        StubApiFetcher(String json) {
            this.json = json;
        }

        @Override
        public String fetchCyclingDirectionsJson(double startLon, double startLat,
                                                 double endLon, double endLat) {
            return json;
        }
    }

    private static class CapturingPresenter implements BikeRouteOutputBoundary {
        BikeRouteOutputData captured;

        @Override
        public void present(BikeRouteOutputData outputData) {
            this.captured = outputData;
        }
    }
}
