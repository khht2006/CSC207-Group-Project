package interface_adapter;

import entity.Route;
import entity.Station;
import org.junit.jupiter.api.Test;
import usecase.BikeRouteOutputData;

import java.util.List

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GetBikeTimePresenterTest {

    @Test
    void formatsDurationIntoViewModel() {
        Route walk1 = new Route(100, 120, List.of("walk-start"));
        Route bike = new Route(300, 300, List.of("bike"));
        Route walk2 = new Route(50, 60, List.of("walk-end"));
        Station start = new Station("Start Station", 0, 0);
        Station end = new Station("End Station", 0, 0);

        BikeRouteOutputData outputData = new BikeRouteOutputData(
                walk1, start, bike, end, walk2, "Destination");
        GetBikeTimeViewModel vm = new GetBikeTimeViewModel();
        GetBikeTimePresenter presenter = new GetBikeTimePresenter(vm);

        presenter.present(outputData);

        assertEquals(
                "<html>Total Time: 8.0 minutes<br>" +
                        "Walk 2.0 minutes to Start Station<br>" +
                        "Bike 5.0 minutes to End Station<br>" +
                        "Walk 1.0 minutes to Destination</html>",
                vm.getBikeTimeText());
        assertEquals(5.0, vm.getCyclingTimeMinutes());
        assertEquals(8.0, vm.getTotalTimeMinutes());
    }

    @Test
    void showsErrorMessage() {
        BikeRouteOutputData errorData = new BikeRouteOutputData("Problem occurred");
        GetBikeTimeViewModel vm = new GetBikeTimeViewModel();
        GetBikeTimePresenter presenter = new GetBikeTimePresenter(vm);

        presenter.present(errorData);

        assertEquals("Bike Time: Problem occurred", vm.getBikeTimeText());
        assertEquals(0.0, vm.getBikeTimeValue());
    }
}
