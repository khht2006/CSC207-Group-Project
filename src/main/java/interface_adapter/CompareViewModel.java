package interface_adapter;

/**
 * The View Model for the Compare Summary view.
 */
public class CompareViewModel {

    private String walkTimeText = "Walk Time: -- minutes";
    private String bikeTimeText = "Bike Time: -- minutes";
    private String bikeCostText = "Bike Cost: --";
    private String diffInMinutesText = "Time saved by biking: -- minutes";

    public String getWalkTime() {
        return walkTimeText;
    }

    public void setWalkTimeText(double minutes) {
        this.walkTimeText = String.format("Walk Time: %.1f minutes", minutes);
    }

    public String getBikeTime() {
        return bikeTimeText;
    }

    public void setBikeTimeText(double minutes) {
        this.bikeTimeText = String.format("Bike Time: %.1f minutes", minutes);
    }

    public String getBikeCost() {
        return bikeCostText;
    }

    public void setBikeCostText(String costText) {
        this.bikeCostText = costText;
    }

    public String getDiffInMinutes() {
        return diffInMinutesText;
    }

    public void setDiffInMinutesText(double diffInMinutes) {
        this.diffInMinutesText = String.format("Time saved by biking: %.1f minutes", diffInMinutes);
    }
}
