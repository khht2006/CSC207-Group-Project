package interface_adapter;

import usecase.BikeRouteOutputBoundary;
import usecase.BikeRouteOutputData;

// Presenter for biking time results.

public class GetBikeTimePresenter implements BikeRouteOutputBoundary {

    private final GetBikeTimeViewModel viewModel;

    public GetBikeTimePresenter(GetBikeTimeViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void present(BikeRouteOutputData outputData) {
        if (outputData.hasError()) {
            viewModel.setBikeTimeText("Bike Time: " + outputData.getErrorMessage());
            return;
        }

        double totalMinutes = outputData.getTotalDurationMinutes();

        double walk1Min = outputData.getWalkToStation().getDurationSeconds() / 60.0;
        String station1 = outputData.getStartStation().getName();

        double bikeMin = outputData.getBikeSegment().getDurationSeconds() / 60.0;
        String station2 = outputData.getEndStation().getName();

        double walk2Min = outputData.getWalkFromStation().getDurationSeconds() / 60.0;
        String destination = outputData.getDestinationName();

        // Formatted HTML string
        String text = String.format("<html>Total Time: %.1f minutes<br>" +
                        "Walk %.1f minutes to %s<br>" +
                        "Bike %.1f minutes to %s<br>" +
                        "Walk %.1f minutes to %s</html>",
                totalMinutes, walk1Min, station1, bikeMin, station2, walk2Min, destination);

        viewModel.setBikeTimeText(text);
        viewModel.setCyclingTimeMinutes(outputData.getCyclingDurationMinutes());
        viewModel.setTotalTimeMinutes(totalMinutes);
    }

    public GetBikeTimeViewModel getViewModel() {
        return viewModel;
    }
}
