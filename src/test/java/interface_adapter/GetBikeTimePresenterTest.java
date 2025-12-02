package interface_adapter;

import interface_adapter.bike_time.GetBikeTimePresenter;
import interface_adapter.bike_time.GetBikeTimeViewModel;
import org.junit.jupiter.api.Test;
import usecase.bike_route.BikeRouteOutputData;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GetBikeTimePresenterTest {

    @Test
    void formatsDurationIntoViewModel() {
        BikeRouteOutputData outputData = new BikeRouteOutputData(12.34);
        GetBikeTimeViewModel vm = new GetBikeTimeViewModel();
        GetBikeTimePresenter presenter = new GetBikeTimePresenter(vm);

        presenter.present(outputData);

        assertEquals("Bike Time: 12.3 minutes", vm.getBikeTimeText());
        assertEquals(12.3, vm.getBikeTimeValue());
    }

    @Test
    void showsErrorMessage() {
        BikeRouteOutputData errorData = new BikeRouteOutputData(-1, "Problem occurred");
        GetBikeTimeViewModel vm = new GetBikeTimeViewModel();
        GetBikeTimePresenter presenter = new GetBikeTimePresenter(vm);

        presenter.present(errorData);

        assertEquals("Bike Time: Problem occurred", vm.getBikeTimeText());
        // getBikeTimeValue should ignore non-numeric text
        assertEquals(0.0, vm.getBikeTimeValue());
    }
}
