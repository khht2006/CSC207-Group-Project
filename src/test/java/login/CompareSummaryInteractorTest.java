package login;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import interface_adapter.compare_summary.CompareSummaryPresenter;
import interface_adapter.compare_summary.CompareSummaryViewModel;
import usecase.compare_summary.CompareSummaryInputData;
import usecase.compare_summary.CompareSummaryInteractor;

public class CompareSummaryInteractorTest {

    @Test
    void testUpdate() {
        final CompareSummaryViewModel viewModel = new CompareSummaryViewModel();
        final CompareSummaryPresenter presenter = new CompareSummaryPresenter(viewModel);
        final CompareSummaryInteractor interactor = new CompareSummaryInteractor(presenter);

        final double walkMinutes = 12.7;
        final double bikeMinutes = 7.9;
        final double bikeCost = 1.00 + 0.12 * bikeMinutes;

        final CompareSummaryInputData inputData = new CompareSummaryInputData(walkMinutes, bikeMinutes, bikeCost);

        interactor.execute(inputData);
        assertEquals("Walk Time: 12.7 minutes", viewModel.getWalkTime());
        assertEquals("Bike Time: 7.9 minutes", viewModel.getBikeTime());
        assertEquals(String.format("$%.2f", bikeCost), viewModel.getBikeCost());
        assertEquals("Time saved by biking: 4.8 minutes", viewModel.getDiffInMinutes());
    }

    @Test
    void testZeroMinutes() {
        // arrange
        final CompareSummaryViewModel viewModel = new CompareSummaryViewModel();
        final CompareSummaryPresenter presenter = new CompareSummaryPresenter(viewModel);
        final CompareSummaryInteractor interactor = new CompareSummaryInteractor(presenter);

        final double walkMinutes = 5.0;
        final double bikeMinutes = 0.0;
        final double bikeCost = 1.00 + 0.12 * bikeMinutes;

        final CompareSummaryInputData input = new CompareSummaryInputData(walkMinutes, bikeMinutes, bikeCost);

        interactor.execute(input);

        assertEquals("Walk Time: 5.0 minutes", viewModel.getWalkTime());
        assertEquals("Bike Time: 0.0 minutes", viewModel.getBikeTime());
        assertEquals("$1.00", viewModel.getBikeCost());
        assertEquals("Time saved by biking: 5.0 minutes", viewModel.getDiffInMinutes());
    }
}
