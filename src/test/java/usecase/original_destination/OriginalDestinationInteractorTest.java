package usecase.original_destination;

import entity.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import usecase.original_destination.OriginalDestinationInputData;
import usecase.original_destination.OriginalDestinationInteractor;
import usecase.original_destination.OriginalDestinationOutputBoundary;
import usecase.original_destination.OriginalDestinationOutputData;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OriginalDestinationInteractorTest {

    private OriginalDestinationInteractor interactor;
    private MockOriginalDestinationPresenter presenter;
    private Location location1;
    private Location location2;

    // A mock presenter to capture output data for verification
    private static class MockOriginalDestinationPresenter implements OriginalDestinationOutputBoundary {
        OriginalDestinationOutputData presentedData;
        boolean wasCalled = false;

        @Override
        public void presentSwappedLocations(OriginalDestinationOutputData outputData) {
            this.presentedData = outputData;
            this.wasCalled = true;
        }

        @Override
        public void presentError(String errorMessage) {
            fail("Error message should not be presented.");
        }
    }

    @BeforeEach
    void setUp() {
        presenter = new MockOriginalDestinationPresenter();
        interactor = new OriginalDestinationInteractor(presenter);

        location1 = new Location("Location A", 43.6532, -79.3832);
        location2 = new Location("Location B", 34.0522, -118.2437);

        List<Location> availableLocations = new ArrayList<>();
        availableLocations.add(location1);
        availableLocations.add(location2);
        interactor.updateAvailableLocations(availableLocations);
    }

    @Test
    void testSelectOrigin() {
        interactor.selectOrigin("Location A");
        assertEquals(location1, interactor.getSelectedOrigin());
    }

    @Test
    void testSelectOriginNotFound() {
        interactor.selectOrigin("Non-existent Location");
        assertNull(interactor.getSelectedOrigin());
    }

    @Test
    void testSelectDestination() {
        interactor.selectDestination("Location B");
        assertEquals(location2, interactor.getSelectedDestination());
    }

    @Test
    void testSelectDestinationNotFound() {
        interactor.selectDestination("Non-existent Location");
        assertNull(interactor.getSelectedDestination());
    }

    @Test
    void testSelectWithNoAvailableLocations() {
        // Create a new interactor without updating available locations
        OriginalDestinationInteractor freshInteractor = new OriginalDestinationInteractor(presenter);
        freshInteractor.selectOrigin("Location A");
        assertNull(freshInteractor.getSelectedOrigin());
    }

    @Test
    void testSwapLocations() {
        // Arrange
        interactor.selectOrigin("Location A");
        interactor.selectDestination("Location B");

        // Act
        interactor.swapLocations(new OriginalDestinationInputData("Location A", "Location B"));

        // Assert
        assertEquals(location2, interactor.getSelectedOrigin(), "Origin should be location B after swap.");
        assertEquals(location1, interactor.getSelectedDestination(), "Destination should be location A after swap.");

        assertTrue(presenter.wasCalled, "Presenter should have been called.");
        assertNotNull(presenter.presentedData, "Presenter should have received data.");
        assertEquals("Location B", presenter.presentedData.getOriginText(), "Presented origin name should be 'Location B'.");
        assertEquals("Location A", presenter.presentedData.getDestinationText(), "Presented destination name should be 'Location A'.");
    }

    @Test
    void testSwapLocationsWithNulls() {
        // Arrange: No locations are selected initially
        Location initialOrigin = interactor.getSelectedOrigin();
        Location initialDestination = interactor.getSelectedDestination();

        // Act
        interactor.swapLocations(new OriginalDestinationInputData("", ""));

        // Assert
        assertEquals(initialDestination, interactor.getSelectedOrigin()); // both null
        assertEquals(initialOrigin, interactor.getSelectedDestination()); // both null

        assertTrue(presenter.wasCalled);
        assertEquals("", presenter.presentedData.getOriginText());
        assertEquals("", presenter.presentedData.getDestinationText());
    }

    @Test
    void testSwapLocationsWithOneNull() {
        // Arrange
        interactor.selectOrigin("Location A");

        // Act
        interactor.swapLocations(new OriginalDestinationInputData("Location A", ""));

        // Assert
        assertNull(interactor.getSelectedOrigin());
        assertEquals(location1, interactor.getSelectedDestination());

        assertTrue(presenter.wasCalled);
        assertEquals("", presenter.presentedData.getOriginText());
        assertEquals("Location A", presenter.presentedData.getDestinationText());
    }
}
