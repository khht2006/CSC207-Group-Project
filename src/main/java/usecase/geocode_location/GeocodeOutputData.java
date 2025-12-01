package usecase.geocode_location;

import java.util.List;

public class GeocodeOutputData {
    private final List<String> locationNames;
    private final List<Double> latitudes;
    private final List<Double> longitudes;

    public GeocodeOutputData(
            List<String> locationNames,
            List<Double> latitudes,
            List<Double> longitudes) {
        this.locationNames = locationNames;
        this.latitudes = latitudes;
        this.longitudes = longitudes;
    }

    public List<String> getLocationNames() {
        return locationNames;
    }

    public List<Double> getLatitudes() {
        return latitudes;
    }

    public List<Double> getLongitudes() {
        return longitudes;
    }
}
