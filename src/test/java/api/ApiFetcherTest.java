package api;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ApiFetcherTest {
    @Test
    void lengthTest() throws Exception {
        ApiFetcher fetcher = new StubApiFetcher();

        String walkJson = fetcher.fetchWalkingDirectionsJson(0, 0, 0, 0);
        String bikeInfo = fetcher.fetchBikeStationInformationJson();

        Assertions.assertEquals("walk-json", walkJson);
        Assertions.assertEquals("bike-stations-json", bikeInfo);
    }

    private static class StubApiFetcher extends ApiFetcher {
        @Override
        public String fetchWalkingDirectionsJson(double startLon, double startLat,
                                                 double endLon, double endLat) {
            return "walk-json";
        }

        @Override
        public String fetchBikeStationInformationJson() {
            return "bike-stations-json";
        }
    }
}
