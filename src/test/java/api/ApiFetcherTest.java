package api;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ApiFetcherTest {
    @Test
    void lengthTest() throws Exception {
        ApiFetcher fetcher = new ApiFetcher();

        // Example coordinates: Toronto City Hall to UofT (approx)
        double startLon = -79.3832;
        double startLat = 43.6536;
        double endLon = -79.3948;
        double endLat = 43.6629;

        String walkJson = fetcher.fetchWalkingDirectionsJson(startLon, startLat, endLon, endLat);
        String bikeInfo = fetcher.fetchBikeStationInformationJson();

        Assertions.assertEquals(2806, walkJson.length());
        Assertions.assertEquals(474190, bikeInfo.length());
    }
}