package interface_adapter;

/**
 * View model for storing and displaying the formatted bike travel cost.
 */
public class GetBikeCostViewModel {

    /** The formatted bike cost text shown in the UI. */
    private String bikeCostText = "Bike Cost:";
    private double bikeCostValue;

    public String getBikeCostText() {
        return bikeCostText;
    }

    /**
     * Sets the formatted bike cost text.
     *
     * @param bikeCostText the text to display
     */
    public void setBikeCostText(String bikeCostText) {
        this.bikeCostText = bikeCostText;
    }

    public void setBoldBikeCostValue(double bikeCostValue) {
        this.bikeCostValue = bikeCostValue;
    }

    public double getBoldBikeCostValue() {
        return bikeCostValue;
    }

    /**
     * Extracts the numeric bike cost value from the formatted text.
     *
     * @return the numeric bike cost, or 0.0 if no number is present
     */
    public double getBikeCostValue() {
        final String number = bikeCostText.replaceAll("[^0-9.]", "");
        if (number.isEmpty()) {
            return 0.0;
        }
        return Double.parseDouble(number);
    }
}
