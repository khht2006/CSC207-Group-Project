package usecase;

// Output data carrying the cycling duration in minutes.
public class BikeRouteOutputData {
    private final double durationMinutes;
    private final String errorMessage;

    public BikeRouteOutputData(double durationMinutes) {
        this(durationMinutes, null);
    }

    public BikeRouteOutputData(double durationMinutes, String errorMessage) {
        this.durationMinutes = durationMinutes;
        this.errorMessage = errorMessage;
    }

    public double getDurationMinutes() {
        return durationMinutes;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean hasError() {
        return errorMessage != null && !errorMessage.isBlank();
    }
}
