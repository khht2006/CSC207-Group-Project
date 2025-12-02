package usecase.compare_summary;

/**
 * Output data for the Compare Summary use case.
 */
public class CompareSummaryOutputData {
    private final double walkMinutes;
    private final double bikeMinutes;
    private final double bikeCost;
    /** The computed difference between walking and biking time. */
    private final double diffInMinutes;

    /**
     * Creates a new output with the given difference.
     * @param walkMinutes walk time in minutes
     * @param bikeMinutes bike time in minutes
     * @param bikeCost bike cost
     * @param diffInMinutes difference in walk and bike time in minutes
     */
    public CompareSummaryOutputData(double walkMinutes, double bikeMinutes,
                                    double bikeCost, double diffInMinutes) {
        this.walkMinutes = walkMinutes;
        this.bikeMinutes = bikeMinutes;
        this.bikeCost = bikeCost;
        this.diffInMinutes = diffInMinutes;
    }

    public double getWalkMinutes() {
        return walkMinutes;
    }

    public double getBikeMinutes() {
        return bikeMinutes;
    }

    public double getBikeCost() {
        return bikeCost;
    }

    /**
     * Returns the computed difference.
     * @return the computed difference
     */
    public double getDiffInMinutes() {
        return diffInMinutes;
    }
}
