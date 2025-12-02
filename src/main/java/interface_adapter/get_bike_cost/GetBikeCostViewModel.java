package interface_adapter.get_bike_cost;

/**
 * View model for storing and displaying the formatted bike travel cost.
 */
public class GetBikeCostViewModel {

    /** The formatted bike cost text shown in the UI. */
    private String bikeCostText = "Bike Cost:";

    /**
     * Returns the formatted bike cost text.
     *
     * @return the displayed bike cost text
     */
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
