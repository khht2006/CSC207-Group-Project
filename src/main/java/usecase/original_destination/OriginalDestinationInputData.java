package usecase.original_destination;

public class OriginalDestinationInputData {
    private final String originName;
    private final String destinationName;

    public OriginalDestinationInputData(String originName, String destinationName) {
        this.originName = originName;
        this.destinationName = destinationName;
    }

    public String getOriginName() {
        return originName;
    }

    public String getDestinationName() {
        return destinationName;
    }
}
