package usecase;

/**
 * Output data carrying the cycling duration in minutes.
 */
public class BikeRouteOutputData {

    private final double durationMinutes;
    private final String errorMessage;

    /**
     * Creates an output data object with the given duration and no error.
     *
     * @param durationMinutes cycling duration in minutes
     */
    public BikeRouteOutputData(double durationMinutes) {
        this(durationMinutes, null);
    }

    /**
     * Creates an output data object with the given duration and error message.
     *
     * @param durationMinutes cycling duration in minutes
     * @param errorMessage optional error message
     */
    public BikeRouteOutputData(double durationMinutes, String errorMessage) {
        this.durationMinutes = durationMinutes;
        this.errorMessage = errorMessage;
    }

    /**
     * Returns the cycling duration in minutes.
     *
     * @return duration in minutes
     */
    public double getDurationMinutes() {
        return durationMinutes;
    }

    /**
     * Returns the error message, if any.
     *
     * @return error message or {@code null}
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Returns whether an error occurred.
     *
     * @return {@code true} if an error is present, {@code false} otherwise
     */
    public boolean hasError() {
        return errorMessage != null && !errorMessage.isBlank();
    }
}
