package ApiTest;
import app.ApiFetcher;


public class ApiTestMain {
    public static void main(String[] args) throws Exception {
        ApiFetcher fetcher = new ApiFetcher();

        // Example coordinates: Toronto City Hall to UofT (approx)
        double startLon = -79.3832;
        double startLat = 43.6536;
        double endLon = -79.3948;
        double endLat = 43.6629;

        String walkJson = fetcher.fetchWalkingDirectionsJson(startLon, startLat, endLon, endLat);
        System.out.println("Walking JSON length: " + walkJson.length());

        String bikeInfo = fetcher.fetchBikeStationInformationJson();
        System.out.println("Station info JSON length: " + bikeInfo.length());
    }
}