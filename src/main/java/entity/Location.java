package entity;

/**
 * Represents a geographic location with a display name and coordinates.
 */
public class Location {

    private final String name;
    private final double latitude;
    private final double longitude;

    /**
     * An entity representing a location with a name and latitude/longitude coordinates.
     * @param name      human-readable label (e.g. "Toronto City Hall")
     * @param latitude  latitude in decimal degrees
     * @param longitude longitude in decimal degrees
     */
    public Location(String name, double latitude, double longitude) {
        this.name = name == null ? "" : name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    /**
     * Returns (lat, lon) in that order.
     * @return (lat, lon)
     */
    public double[] getCoordinates() {
        return new double[]{latitude, longitude};
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @Override
    public String toString() {
        return name + " (" + latitude + ", " + longitude + ")";
    }
}
