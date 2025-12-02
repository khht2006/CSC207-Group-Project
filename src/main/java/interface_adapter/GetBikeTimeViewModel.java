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

    /**
     * Return the display text for bike time.
     * @return the current bike time display string
     */
    public double getBikeTimeValue() {
        final String num = bikeTimeText.replaceAll("[^0-9.]", "");
        if (num.isEmpty()) {
            return 0.0;
        }
        return Double.parseDouble(num);
    }
}
