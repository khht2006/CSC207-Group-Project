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

    @Test
    void presentsErrorWhenDurationMissing() {
        // No duration field -> parseDurationMinutes should throw
        String missingDurationJson = """
                { "routes": [ { "summary": { "distance": 10.0 } } ] }
                """;
        CapturingPresenter presenter = new CapturingPresenter();
        BikeRouteInteractor interactor =
                new BikeRouteInteractor(new StubApiFetcher(missingDurationJson), presenter);
        interactor.execute(new BikeRouteInputData(0, 0, 1, 1));

        Assertions.assertTrue(presenter.captured.hasError());
        Assertions.assertEquals("Duration not found in ORS response.", presenter.captured.getErrorMessage());
    }

    @Test
    void presentsErrorOnIoException() {
        CapturingPresenter presenter = new CapturingPresenter();
        BikeRouteInteractor interactor =
                new BikeRouteInteractor(new IOExceptionApiFetcher(), presenter);

        interactor.execute(new BikeRouteInputData(0, 0, 1, 1));

        Assertions.assertTrue(presenter.captured.hasError());
        Assertions.assertTrue(
                presenter.captured.getErrorMessage().startsWith("Failed to fetch cycling directions"));
    }

    @Test
    void preservesInterruptStatusOnInterruptedException() {
        CapturingPresenter presenter = new CapturingPresenter();
        BikeRouteInteractor interactor =
                new BikeRouteInteractor(new InterruptedApiFetcher(), presenter);

        interactor.execute(new BikeRouteInputData(0, 0, 1, 1));

        Assertions.assertTrue(Thread.currentThread().isInterrupted(), "Interrupt flag should be set");
        // Clear flag for other tests
        Thread.interrupted();

        Assertions.assertTrue(presenter.captured.hasError());
        Assertions.assertEquals("Directions request interrupted.", presenter.captured.getErrorMessage());
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

    private static class IOExceptionApiFetcher extends ApiFetcher {
        @Override
        public String fetchCyclingDirectionsJson(double startLon, double startLat,
                                                 double endLon, double endLat) throws java.io.IOException {
            throw new java.io.IOException("Simulated IO failure");
        }
    }

    private static class InterruptedApiFetcher extends ApiFetcher {
        @Override
        public String fetchCyclingDirectionsJson(double startLon, double startLat,
                                                 double endLon, double endLat) throws InterruptedException {
            throw new InterruptedException("Simulated interrupt");
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
