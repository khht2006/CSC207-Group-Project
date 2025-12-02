package login;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import interface_adapter.ComparePresenter;
import interface_adapter.CompareViewModel;
import usecase.compare_summary.*;

public class CompareSummaryInteractorTest {

    @Test
    void testUpdate() {
        CompareViewModel viewModel = new CompareViewModel();
        ComparePresenter presenter = new ComparePresenter(viewModel);
        CompareSummaryInteractor interactor = new CompareSummaryInteractor(presenter);

        double walkMinutes = 12.7;
        double bikeMinutes = 7.9;
        double bikeCost = 1.00 + 0.12 * bikeMinutes;

        CompareSummaryInputData inputData = new CompareSummaryInputData(walkMinutes, bikeMinutes, bikeCost);

        interactor.execute(inputData);
        assertEquals("Walk Time: 12.7 minutes", viewModel.getWalkTime());
        assertEquals("Bike Time: 7.9 minutes", viewModel.getBikeTime());
        assertEquals(String.format("$%.2f", bikeCost), viewModel.getBikeCost());
        assertEquals("Time saved by biking: 4.8 minutes", viewModel.getDiffInMinutes());
    }

    @Test
    void testZeroMinutes() {
        // arrange
        CompareViewModel viewModel = new interface_adapter.CompareViewModel();
        ComparePresenter presenter = new interface_adapter.ComparePresenter(viewModel);
        CompareSummaryInteractor interactor = new CompareSummaryInteractor(presenter);

        double walkMinutes = 5.0;
        double bikeMinutes = 0.0;
        double bikeCost = 1.00 + 0.12 * bikeMinutes;

        CompareSummaryInputData input = new CompareSummaryInputData(walkMinutes, bikeMinutes, bikeCost);

        // act
        interactor.execute(input);

        // assert
        assertEquals("Walk Time: 5.0 minutes", viewModel.getWalkTime());
        assertEquals("Bike Time: 0.0 minutes", viewModel.getBikeTime());
        assertEquals("$1.00", viewModel.getBikeCost());
        assertEquals("Time saved by biking: 5.0 minutes", viewModel.getDiffInMinutes());
    }
}
