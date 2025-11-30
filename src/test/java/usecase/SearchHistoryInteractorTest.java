package usecase;

import entity.SearchRecord;
import org.junit.jupiter.api.Test;
import usecase.search_history.SearchHistoryData;
import usecase.search_history.SearchHistoryInteractor;
import usecase.search_history.SearchHistoryOutputBoundary;
import usecase.search_history.SearchHistoryOutputData;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link SearchHistoryInteractor}.
 */
class SearchHistoryInteractorTest {

    private static class PresenterStub implements SearchHistoryOutputBoundary {
        SearchHistoryOutputData received;

        @Override
        public void present(SearchHistoryOutputData data) {
            this.received = data;
        }
    }

    private static class FakeGateway implements SearchHistoryData {
        private final List<SearchRecord> records;

        FakeGateway(List<SearchRecord> records) {
            this.records = records;
        }

        @Override
        public void save(SearchRecord record) {
            // Not needed for this test
        }

        @Override
        public List<SearchRecord> load() {
            return records;
        }
    }

    /**
     * Tests that the interactor loads search history records and passes
     * them to the presenter correctly.
     */
    @Test
    void testInteractorLoadsRecordsAndPresentsThem() {
        SearchRecord r1 = new SearchRecord("A", "B", 10.0, 1.5, 20.0);
        SearchRecord r2 = new SearchRecord("C", "D", 15.0, 2.0, 25.0);

        FakeGateway gateway = new FakeGateway(List.of(r1, r2));
        PresenterStub presenter = new PresenterStub();

        SearchHistoryInteractor interactor =
                new SearchHistoryInteractor(gateway, presenter);

        interactor.execute();

        assertNotNull(presenter.received);
        List<SearchRecord> out = presenter.received.getRecords();
        assertEquals(2, out.size());
        assertEquals("A", out.get(0).getOrigin());
        assertEquals("D", out.get(1).getDestination());
        assertEquals(1.5, out.get(0).getBikeCost());
    }
}
