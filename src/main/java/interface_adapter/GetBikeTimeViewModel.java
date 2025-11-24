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

    public double getBikeTimeValue() {
        String num = bikeTimeText.replaceAll("[^0-9.]", "");
        if (num.isEmpty()) {
            return 0.0;
        }
        return Double.parseDouble(num);
    }
}
