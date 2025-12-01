package interface_adapter;

/**
 * View model for displaying bike travel cost.
 */
public class GetBikeCostViewModel {
    private String bikeCostText = "Bike Cost:";
    private double bikeCostValue;

    public String getBikeCostText() {
        return bikeCostText;
    }
    public void setBikeCostText(String bikeCostText) {
        this.bikeCostText = bikeCostText;
    }

    public void setBikeCostValue(double bikeCostValue) {
        this.bikeCostValue = bikeCostValue;
    }

    public double getBikeCostValue() {
        return bikeCostValue;
    }
}