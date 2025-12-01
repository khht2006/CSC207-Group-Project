package usecase.fetch_location;

import api.ApiFetcher;
import entity.Location;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

class GeocodeLocationInteractorTest {

    /**
     * A fake ApiFetcher that returns pre-determined JSON strings
     * and allows us to verify inputs without using Mockito.
     */
    static class FakeApiFetcher extends ApiFetcher {
        private String responseJson = "{}";
        private Exception exceptionToThrow = null;

        // Capturing arguments for verification
        String lastQuery;
        int lastMaxResults;

        @Override
        public String fetchGeocodeJson(String query, int maxResults) throws IOException, InterruptedException {
            this.lastQuery = query;
            this.lastMaxResults = maxResults;

            if (exceptionToThrow != null) {
                if (exceptionToThrow instanceof IOException) {
                    throw (IOException) exceptionToThrow;
                } else if (exceptionToThrow instanceof InterruptedException) {
                    throw (InterruptedException) exceptionToThrow;
                } else {
                    throw new RuntimeException(exceptionToThrow);
                }
            }
            return responseJson;
        }

        void setResponseJson(String json) {
            this.responseJson = json;
            this.exceptionToThrow = null;
        }

        void setExceptionToThrow(Exception e) {
            this.exceptionToThrow = e;
        }
    }

    @Test
    void testSearchLocations_ParsesFullAddressCorrectly() throws IOException, InterruptedException {
        FakeApiFetcher fakeFetcher = new FakeApiFetcher();
        // Construct JSON where properties follow coordinates to ensure regex finding works as implemented
        // The interactor searches for properties starting from the index of "coordinates" match
        String json = "{"
                + "  \"features\": [{"
                + "    \"geometry\": {\"coordinates\": [-79.3832, 43.6536]},"
                + "    \"properties\": {"
                + "      \"housenumber\": \"100\","
                + "      \"street\": \"Queen Street\","
                + "      \"locality\": \"Toronto\","
                + "      \"label\": \"Ignored Label\""
                + "    }"
                + "  }]"
                + "}";
        fakeFetcher.setResponseJson(json);

        GeocodeLocationInteractor interactor = new GeocodeLocationInteractor(fakeFetcher);
        List<Location> locations = interactor.searchLocations("Toronto", 5);

        Assertions.assertEquals(1, locations.size());
        Location loc = locations.get(0);

        // Expected format: "housenumber street, locality"
        Assertions.assertEquals("100 Queen Street, Toronto", loc.getName());
        Assertions.assertEquals(43.6536, loc.getLatitude(), 0.0001);
        Assertions.assertEquals(-79.3832, loc.getLongitude(), 0.0001);

        // Verify arguments passed to fetcher
        Assertions.assertEquals("Toronto", fakeFetcher.lastQuery);
        Assertions.assertEquals(5, fakeFetcher.lastMaxResults);
    }

    @Test
    void testSearchLocations_FallbackToName() throws IOException, InterruptedException {
        FakeApiFetcher fakeFetcher = new FakeApiFetcher();
        String json = "{"
                + "  \"features\": [{"
                + "    \"geometry\": {\"coordinates\": [10.0, 20.0]},"
                + "    \"properties\": {"
                + "      \"name\": \"CN Tower\","
                + "      \"label\": \"CN Tower, Toronto\""
                + "    }"
                + "  }]"
                + "}";
        fakeFetcher.setResponseJson(json);

        GeocodeLocationInteractor interactor = new GeocodeLocationInteractor(fakeFetcher);
        List<Location> locations = interactor.searchLocations("CN Tower", 5);

        Assertions.assertEquals(1, locations.size());
        Assertions.assertEquals("CN Tower", locations.get(0).getName());
    }

    @Test
    void testSearchLocations_FallbackToLabel() throws IOException, InterruptedException {
        FakeApiFetcher fakeFetcher = new FakeApiFetcher();
        // Missing street/house/name, so use label
        String json = "{"
                + "  \"features\": [{"
                + "    \"geometry\": {\"coordinates\": [10.0, 20.0]},"
                + "    \"properties\": {"
                + "      \"label\": \"Generic Label\""
                + "    }"
                + "  }]"
                + "}";
        fakeFetcher.setResponseJson(json);

        GeocodeLocationInteractor interactor = new GeocodeLocationInteractor(fakeFetcher);
        List<Location> locations = interactor.searchLocations("Something", 5);

        Assertions.assertEquals(1, locations.size());
        Assertions.assertEquals("Generic Label", locations.get(0).getName());
    }

    @Test
    void testSearchLocations_ReturnsUnknownIfEmptyProperties() throws IOException, InterruptedException {
        FakeApiFetcher fakeFetcher = new FakeApiFetcher();
        String json = "{"
                + "  \"features\": [{"
                + "    \"geometry\": {\"coordinates\": [10.0, 20.0]},"
                + "    \"properties\": {}"
                + "  }]"
                + "}";
        fakeFetcher.setResponseJson(json);

        GeocodeLocationInteractor interactor = new GeocodeLocationInteractor(fakeFetcher);
        List<Location> locations = interactor.searchLocations("Something", 5);

        Assertions.assertEquals(1, locations.size());
        Assertions.assertEquals("Unknown location", locations.get(0).getName());
    }

    @Test
    void testSearchLocations_RankingLogic() throws IOException, InterruptedException {
        FakeApiFetcher fakeFetcher = new FakeApiFetcher();
        // We provide multiple results.
        // To test ranking, we provide them in an order that needs resort.
        // Query: "Apple"
        // Items:
        // 1. "name": "Zebra" (Score 3 - Other)
        // 2. "name": "ApplePie" (Score 1 - StartsWith)
        // 3. "name": "Apple" (Score 0 - Exact)

        String json = "{"
                + "\"features\": ["
                + "  { \"geometry\": {\"coordinates\": [0,0]}, \"properties\": { \"name\": \"Zebra\" } },"
                + "  { \"geometry\": {\"coordinates\": [0,0]}, \"properties\": { \"name\": \"ApplePie\" } },"
                + "  { \"geometry\": {\"coordinates\": [0,0]}, \"properties\": { \"name\": \"Apple\" } }"
                + "]"
                + "}";
        fakeFetcher.setResponseJson(json);

        GeocodeLocationInteractor interactor = new GeocodeLocationInteractor(fakeFetcher);
        List<Location> results = interactor.searchLocations("Apple", 10);

        Assertions.assertEquals(3, results.size());
        // Expected order: Exact match, then Prefix match, then Other
        Assertions.assertEquals("Apple", results.get(0).getName());
        Assertions.assertEquals("ApplePie", results.get(1).getName());
        Assertions.assertEquals("Zebra", results.get(2).getName());
    }

    @Test
    void testFindBestLocation_ReturnsFirstOrNull() throws IOException, InterruptedException {
        FakeApiFetcher fakeFetcher = new FakeApiFetcher();

        // Case 1: Match found
        fakeFetcher.setResponseJson("{ \"geometry\": {\"coordinates\": [0,0]}, \"properties\": { \"name\": \"Found\" } }");
        GeocodeLocationInteractor interactor = new GeocodeLocationInteractor(fakeFetcher);
        Location loc = interactor.findBestLocation("Query");
        Assertions.assertNotNull(loc);
        Assertions.assertEquals("Found", loc.getName());
        Assertions.assertEquals(1, fakeFetcher.lastMaxResults);

        // Case 2: No match
        fakeFetcher.setResponseJson("{}");
        Location locNull = interactor.findBestLocation("Query");
        Assertions.assertNull(locNull);
    }

    @Test
    void testMaxResultsDefaulting() throws IOException, InterruptedException {
        FakeApiFetcher fakeFetcher = new FakeApiFetcher();
        GeocodeLocationInteractor interactor = new GeocodeLocationInteractor(fakeFetcher);

        // If we pass 0 or negative, it should use default (5)
        interactor.searchLocations("Query", -5);
        Assertions.assertEquals(5, fakeFetcher.lastMaxResults);
    }

    @Test
    void testExceptionHandling() {
        FakeApiFetcher fakeFetcher = new FakeApiFetcher();
        fakeFetcher.setExceptionToThrow(new IOException("Network failed"));

        GeocodeLocationInteractor interactor = new GeocodeLocationInteractor(fakeFetcher);

        Assertions.assertThrows(IOException.class, () -> {
            interactor.searchLocations("Query", 5);
        });
    }
}