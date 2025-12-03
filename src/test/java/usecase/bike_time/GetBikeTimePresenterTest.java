package usecase.bike_time;
import entity.Route;
import entity.Station;
import interface_adapter.bike_time.GetBikeTimePresenter;
import interface_adapter.bike_time.GetBikeTimeViewModel;
import org.junit.jupiter.api.Test;
import usecase.bike_route.BikeRouteOutputData;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GetBikeTimePresenterTest {

    @Test
    void formatsDurationIntoViewModel() {
        Route walkTo = new Route(500, 300, Collections.emptyList()); // 5.0 minutes
        Route bike = new Route(2000, 600, Collections.emptyList()); // 10.0 minutes
        Route walkFrom = new Route(300, 120, Collections.emptyList()); // 2.0 minutes

        Station startStation = new Station("Station A", 43.0, -79.0);
        Station endStation = new Station("Station B", 43.1, -79.1);

        BikeRouteOutputData outputData = new BikeRouteOutputData(
                walkTo,
                startStation,
                bike,
                endStation,
                walkFrom,
                "Campus"
        );

        GetBikeTimeViewModel vm = new GetBikeTimeViewModel();
        GetBikeTimePresenter presenter = new GetBikeTimePresenter(vm);

        presenter.present(outputData);

        assertEquals(
                "<html>Total Time: 17.0 minutes<br>" +
                        "Walk 5.0 minutes to Station A<br>" +
                        "Bike 10.0 minutes to Station B<br>" +
                        "Walk 2.0 minutes to Campus</html>",
                vm.getBikeTimeText()
        );
        assertEquals(10.0, vm.getCyclingTimeMinutes());
        assertEquals(17.0, vm.getBikeTimeValue());
    }

    @Test
    void showsErrorMessage() {
        BikeRouteOutputData errorData = new BikeRouteOutputData("Problem occurred");
        GetBikeTimeViewModel vm = new GetBikeTimeViewModel();
        GetBikeTimePresenter presenter = new GetBikeTimePresenter(vm);

        presenter.present(errorData);

        assertEquals("Bike Time: Problem occurred", vm.getBikeTimeText());
        // getBikeTimeValue should ignore non-numeric text
        assertEquals(0.0, vm.getBikeTimeValue());
    }
}
