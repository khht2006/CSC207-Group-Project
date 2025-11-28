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

}
