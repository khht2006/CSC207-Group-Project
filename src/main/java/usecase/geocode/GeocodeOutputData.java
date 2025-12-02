package usecase.geocode;

import entity.Location;
import java.util.List;

public class GeocodeOutputData {
    private final List<Location> locations;
    private final boolean useCaseFailed;

    public GeocodeOutputData(List<Location> locations, boolean useCaseFailed) {
        this.locations = locations;
        this.useCaseFailed = useCaseFailed;
    }

    public List<Location> getLocations() {
        return locations;
    }

    public boolean isUseCaseFailed() {
        return useCaseFailed;
    }
}