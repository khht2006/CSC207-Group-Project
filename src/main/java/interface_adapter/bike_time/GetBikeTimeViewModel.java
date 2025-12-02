package interface_adapter.bike_time;

/**
 * View model for displaying bike travel time.
 */
public class GetBikeTimeViewModel {
    private String bikeTimeText = "Bike Time:";
    private double cyclingTimeMinutes;
    private double totalTimeMinutes;

    public String getBikeTimeText() {
        return bikeTimeText;
    }

    public void setBikeTimeText(String bikeTimeText) {
        this.bikeTimeText = bikeTimeText;
    }

    public double getCyclingTimeMinutes() {
        return cyclingTimeMinutes;
    }

    public void setCyclingTimeMinutes(double cyclingTimeMinutes) {
        this.cyclingTimeMinutes = cyclingTimeMinutes;
    }

    public double getTotalTimeMinutes() {
        return totalTimeMinutes;
    }

    public void setTotalTimeMinutes(double totalTimeMinutes) {
        this.totalTimeMinutes = totalTimeMinutes;
    }

    public double getBikeTimeValue() {
        return getTotalTimeMinutes();
    }

}
