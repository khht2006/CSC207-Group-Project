package usecase.bike_route;

import api.ApiFetcher;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class BikeRouteInteractorTest {

    private static final String STATION_INFO_JSON = """
            {
              "data": {
                "stations": [
                  { "name": "Far Station", "lat": 43.5, "lon": -79.5 },
                  { "name": "Origin Neighbor", "lat": 43.0, "lon": -79.001 },
                  { "name": "Destination Neighbor", "lat": 43.1005, "lon": -79.099 }
                ]
              }
            }
            """;

    private static final String EMPTY_STATION_INFO_JSON = """
            { "data": { "stations": [] } }
            """;

    private static final String WALK_WITH_SEGMENTS_JSON = """
            {
              "routes": [
                {
                  "summary": { "distance": 320.0, "duration": 180.0 },
                  "segments": [
                    {
                      "steps": [
                        { "instruction": "Head north" },
                        { "instruction": "Turn right" }
                      ]
                    },
                    { "other": "no-steps" }
                  ]
                }
              ]
            }
            """;

    private static final String BIKE_WITH_SEGMENTS_JSON = """
            {
              "routes": [
                {
                  "summary": { "distance": 2500.0, "duration": 600.0 },
                  "segments": [
                    {
                      "steps": [
                        { "instruction": "Bike straight" },
                        { "instruction": "Arrive at station" }
                      ]
                    }
                  ]
                }
              ]
            }
            """;

    private static final String WALK_NO_SEGMENTS_JSON = """
            {
              "routes": [
                {
                  "summary": { "distance": 200.0, "duration": 90.0 }
                }
              ]
            }
            """;

    @Test
    void executeCombinesRoutesAndSelectsNearestStations() {
        StubApiFetcher apiFetcher = new StubApiFetcher(
                STATION_INFO_JSON,
                WALK_WITH_SEGMENTS_JSON,
                BIKE_WITH_SEGMENTS_JSON,
                WALK_NO_SEGMENTS_JSON);
        CapturingPresenter presenter = new CapturingPresenter();
        BikeRouteInteractor interactor = new BikeRouteInteractor(apiFetcher, presenter);

        BikeRouteInputData input = new BikeRouteInputData(
                43.0, -79.0,
                43.1, -79.1,
                "Destination Place");

        interactor.execute(input);

        Assertions.assertNotNull(presenter.captured);
        Assertions.assertFalse(presenter.captured.hasError());
        Assertions.assertEquals("Origin Neighbor", presenter.captured.getStartStation().getName());
        Assertions.assertEquals("Destination Neighbor", presenter.captured.getEndStation().getName());
        Assertions.assertEquals("Destination Place", presenter.captured.getDestinationName());

        double expectedMinutes = (180.0 + 600.0 + 90.0) / 60.0;
        Assertions.assertEquals(expectedMinutes, presenter.captured.getTotalDurationMinutes(), 1e-6);
        Assertions.assertEquals(600.0 / 60.0, presenter.captured.getCyclingDurationMinutes(), 1e-6);

        Assertions.assertEquals("Head north", presenter.captured.getWalkToStation().getTurnInstructions().get(0));
        Assertions.assertEquals("Turn right", presenter.captured.getWalkToStation().getTurnInstructions().get(1));
        Assertions.assertEquals("Bike straight", presenter.captured.getBikeSegment().getTurnInstructions().get(0));
        Assertions.assertTrue(presenter.captured.getWalkFromStation().getTurnInstructions().isEmpty());
    }

    @Test
    void presentsErrorWhenNoStationsExist() {
        StubApiFetcher apiFetcher = new StubApiFetcher(
                EMPTY_STATION_INFO_JSON,
                WALK_WITH_SEGMENTS_JSON,
                BIKE_WITH_SEGMENTS_JSON,
                WALK_NO_SEGMENTS_JSON);
        CapturingPresenter presenter = new CapturingPresenter();
        BikeRouteInteractor interactor = new BikeRouteInteractor(apiFetcher, presenter);

        interactor.execute(new BikeRouteInputData(0, 0, 1, 1, "Any"));

        Assertions.assertTrue(presenter.captured.hasError());
        Assertions.assertEquals("No bike stations found.", presenter.captured.getErrorMessage());
    }

    @Test
    void presentsErrorOnIoException() {
        CapturingPresenter presenter = new CapturingPresenter();
        BikeRouteInteractor interactor =
                new BikeRouteInteractor(new IOExceptionApiFetcher(), presenter);

        interactor.execute(new BikeRouteInputData(0, 0, 1, 1, "Test Destination"));

        Assertions.assertTrue(presenter.captured.hasError());
        Assertions.assertTrue(
                presenter.captured.getErrorMessage().startsWith("Failed to fetch cycling directions"));
    }

    @Test
    void preservesInterruptStatusOnInterruptedException() {
        CapturingPresenter presenter = new CapturingPresenter();
        BikeRouteInteractor interactor =
                new BikeRouteInteractor(new InterruptedApiFetcher(), presenter);

        interactor.execute(new BikeRouteInputData(0, 0, 1, 1, "Test Destination"));

        Assertions.assertTrue(Thread.currentThread().isInterrupted(), "Interrupt flag should be set");
        // Clear flag for other tests
        Thread.interrupted();

        Assertions.assertTrue(presenter.captured.hasError());
        Assertions.assertEquals("Directions request interrupted.", presenter.captured.getErrorMessage());
    }

    @Test
    void presentsIllegalStateErrors() {
        CapturingPresenter presenter = new CapturingPresenter();
        BikeRouteInteractor interactor =
                new BikeRouteInteractor(new IllegalStateApiFetcher(), presenter);

        interactor.execute(new BikeRouteInputData(0, 0, 1, 1, "Test Destination"));

        Assertions.assertTrue(presenter.captured.hasError());
        Assertions.assertEquals("Station data unavailable", presenter.captured.getErrorMessage());
    }

    private static class StubApiFetcher extends ApiFetcher {
        private final String stationInfoJson;
        private final String walkToStationJson;
        private final String bikeJson;
        private final String walkFromStationJson;
        private boolean servedFirstWalk;

        StubApiFetcher(String stationInfoJson,
                       String walkToStationJson,
                       String bikeJson,
                       String walkFromStationJson) {
            this.stationInfoJson = stationInfoJson;
            this.walkToStationJson = walkToStationJson;
            this.bikeJson = bikeJson;
            this.walkFromStationJson = walkFromStationJson;
        }

        @Override
        public String fetchBikeStationInformationJson() {
            return stationInfoJson;
        }

        @Override
        public String fetchWalkingDirectionsJson(double startLon, double startLat,
                                                 double endLon, double endLat) {
            if (!servedFirstWalk) {
                servedFirstWalk = true;
                return walkToStationJson;
            }
            return walkFromStationJson;
        }

        @Override
        public String fetchCyclingDirectionsJson(double startLon, double startLat,
                                                 double endLon, double endLat) {
            return bikeJson;
        }
    }

    private static class IOExceptionApiFetcher extends ApiFetcher {
        @Override
        public String fetchBikeStationInformationJson() {
            return STATION_INFO_JSON;
        }

        @Override
        public String fetchWalkingDirectionsJson(double startLon, double startLat,
                                                 double endLon, double endLat) throws java.io.IOException {
            throw new java.io.IOException("Simulated IO failure");
        }

        @Override
        public String fetchCyclingDirectionsJson(double startLon, double startLat,
                                                 double endLon, double endLat) {
            return BIKE_WITH_SEGMENTS_JSON;
        }
    }

    private static class InterruptedApiFetcher extends ApiFetcher {
        @Override
        public String fetchBikeStationInformationJson() {
            return STATION_INFO_JSON;
        }

        @Override
        public String fetchWalkingDirectionsJson(double startLon, double startLat,
                                                 double endLon, double endLat) throws InterruptedException {
            throw new InterruptedException("Simulated interrupt");
        }

        @Override
        public String fetchCyclingDirectionsJson(double startLon, double startLat,
                                                 double endLon, double endLat) {
            return BIKE_WITH_SEGMENTS_JSON;
        }
    }

    private static class IllegalStateApiFetcher extends ApiFetcher {
        @Override
        public String fetchBikeStationInformationJson() {
            throw new IllegalStateException("Station data unavailable");
        }

        @Override
        public String fetchWalkingDirectionsJson(double startLon, double startLat,
                                                 double endLon, double endLat) {
            return WALK_WITH_SEGMENTS_JSON;
        }

        @Override
        public String fetchCyclingDirectionsJson(double startLon, double startLat,
                                                 double endLon, double endLat) {
            return BIKE_WITH_SEGMENTS_JSON;
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
