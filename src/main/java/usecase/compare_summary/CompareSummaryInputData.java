package usecase.compare_summary;

/**
 * Input data for the Compare Summary use case.
 * Contains only precomputed walking and biking durations (in minutes).
 */
public class CompareSummaryInputData {
    private final double walkMinutes;
    private final double bikeMinutes;
    private final double bikeCost;

    public CompareSummaryInputData(double walkMinutes, double bikeMinutes, double bikeCost) {
        this.walkMinutes = walkMinutes;
        this.bikeMinutes = bikeMinutes;
        this.bikeCost = bikeCost;
    }

    /**
     * Returns the walking duration in minutes.
     * @return walkMinutes walk time in minutes
     */
    public double getWalkMinutes() {
        return walkMinutes;
    }

    /**
     * Returns the biking duration in minutes.
     * @return bikeMinutes bike time in minutes
     */
    public double getBikeMinutes() {
        return bikeMinutes;
    }

    /**
     * Returns the bike cost in dollars.
     * @return bikeCost bike cost in dollars
     */
    public double getBikeCost() {
        return bikeCost;
    }
}