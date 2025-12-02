package usecase.original_destination;

public class OriginalDestinationOutputData {
    private final String originText;
    private final String destinationText;
    private final String errorMessage;

    public OriginalDestinationOutputData(String originText, String destinationText) {
        this.originText = originText;
        this.destinationText = destinationText;
        this.errorMessage = null;
    }

    public OriginalDestinationOutputData(String errorMessage) {
        this.originText = null;
        this.destinationText = null;
        this.errorMessage = errorMessage;
    }

    public String getOriginText() {
        return originText;
    }

    public String getDestinationText() {
        return destinationText;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean hasError() {
        return errorMessage != null;
    }
}
