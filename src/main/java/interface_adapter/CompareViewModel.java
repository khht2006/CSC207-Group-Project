package main.java.interface_adapter;

public class CompareViewModel {
    private String walkTimeText = "Walk Time:";
    private String bikeTimeText = "Bike Time:";
    private String bikeCostText = "Bike Cost:";

    public String getWalkTime() {return walkTimeText;}

    public void setWalkTimeText(String walkTime) {
        this.walkTimeText = walkTime;
    }
    public String getBikeTime() {return bikeTimeText;}

    public void setBikeTimeText(String bikeTime) {
        this.bikeTimeText = bikeTime;
    }

    public String getBikeCost() {return bikeCostText;}

    public void setBikeCostText(String bikeCost) {
        this.bikeCostText = bikeCost;
    }

}
