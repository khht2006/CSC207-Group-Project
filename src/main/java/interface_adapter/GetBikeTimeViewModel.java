package interface_adapter;

/**
 * View model for displaying bike travel time.
 */
public class GetBikeTimeViewModel {
    private String bikeTimeText = "Bike Time:";

    public String getBikeTimeText() {
        return bikeTimeText;
    }

    public void setBikeTimeText(String bikeTimeText) {
        this.bikeTimeText = bikeTimeText;
    }
}
