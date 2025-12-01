package usecase.geocode_location;

public class GeocodeInputData {
    private final String query;
    private final int maxResults;

    public GeocodeInputData(String query, int maxResults) {
        this.query = query;
        this.maxResults = maxResults;
    }

    public String getQuery() {
        return query;
    }

    public int getMaxResults() {
        return maxResults;
    }
}
