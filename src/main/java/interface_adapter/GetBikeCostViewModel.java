package interface_adapter;

/**
 * View model for displaying bike travel cost.
 */
public class GetBikeCostViewModel {
    private String bikeCostText = "Bike Cost:";
    public String getBikeCostText() {
        return bikeCostText;
    }
    public void setBikeCostText(String bikeCostText) {
        this.bikeCostText = bikeCostText;
    }

    /**
     * Extracts the numeric bike cost from string.
     */
    public double getBikeCostValue() {
        String number = bikeCostText.replaceAll("[^0-9.]", "");
        if (number.isEmpty()) {
            return 0.0;
        }
        return Double.parseDouble(number);
    }
}
