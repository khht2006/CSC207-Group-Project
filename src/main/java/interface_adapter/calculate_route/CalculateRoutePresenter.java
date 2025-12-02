package interface_adapter.calculate_route;

import interface_adapter.bike_time.GetBikeTimeViewModel;
import interface_adapter.bike_cost.GetBikeCostViewModel;
import usecase.calculate_route.CalculateRouteOutputBoundary;
import usecase.calculate_route.CalculateRouteOutputData;
import view.GetTimePanel;

public class CalculateRoutePresenter implements CalculateRouteOutputBoundary {

    private final GetTimePanel bikeTimePanel;
    private final GetBikeTimeViewModel bikeTimeVM;
    private final GetBikeCostViewModel bikeCostVM;
    private final Runnable onSuccess;

    public CalculateRoutePresenter(GetTimePanel bikeTimePanel,
                                   GetBikeTimeViewModel bikeTimeVM,
                                   GetBikeCostViewModel bikeCostVM,
                                   Runnable onSuccess) {
        this.bikeTimePanel = bikeTimePanel;
        this.bikeTimeVM = bikeTimeVM;
        this.bikeCostVM = bikeCostVM;
        this.onSuccess = onSuccess;
    }

    @Override
    public void prepareSuccessView(CalculateRouteOutputData outputData) {
        bikeTimeVM.setTotalTimeMinutes(outputData.getBikeTime());
        bikeCostVM.setBikeCostValue(outputData.getBikeCost());

        bikeTimePanel.updateBikeTimeText();
        bikeTimePanel.setWalkTimeText(outputData.getWalkTime());

        onSuccess.run();
    }

    @Override
    public void prepareFailView(String error) {
        javax.swing.JOptionPane.showMessageDialog(null, error, "Route Error",
                javax.swing.JOptionPane.ERROR_MESSAGE);
    }
}
