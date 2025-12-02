package usecase.get_bike_cost;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the GetBikeCistInteractor.
 */
class GetBikeCostInteractorTest {

    private static class PresenterStub implements GetBikeCostOutputBoundary {
        GetBikeCostOutputData received;

        @Override
        public void present(GetBikeCostOutputData outputData) {
            this.received = outputData;
        }
    }

    @Test
    void testBikeCostCalculation() {
        PresenterStub presenter = new PresenterStub();
        GetBikeCostInteractor interactor = new GetBikeCostInteractor(presenter);

        GetBikeCostInputData input = new GetBikeCostInputData(15);

        interactor.execute(input);

        assertNotNull(presenter.received);

        double expected = 1.00 + 0.12 * 15;
        assertEquals(expected, presenter.received.getBikeCost(), 1e-6);
    }

    @Test
    void testZeroMinutesStillCostsUnlockFee() {
        PresenterStub presenter = new PresenterStub();
        GetBikeCostInteractor interactor = new GetBikeCostInteractor(presenter);

        GetBikeCostInputData input = new GetBikeCostInputData(0);

        interactor.execute(input);

        assertEquals(1.00, presenter.received.getBikeCost(), 1e-6);
    }
}
