package usecase.delete_history;

import entity.SearchRecord;
import org.junit.jupiter.api.Test;
import usecase.search_history.SearchHistoryInputData;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for DeleteHistoryInteractor.
 */
class DeleteHistoryInteractorTest {

    /**
     * Fake gateway implementing the actual SearchHistoryInputData interface.
     */
    private static class FakeGateway implements SearchHistoryInputData {

        boolean deleteCalled = false;
        List<SearchRecord> storage = new ArrayList<>();

        FakeGateway() {
            // add 2 fake records so deleteAll() actually "deletes" something
            storage.add(new SearchRecord("A", "B", 10.0, 1.0, 20.0));
            storage.add(new SearchRecord("C", "D", 15.0, 2.0, 25.0));
        }

        @Override
        public void save(SearchRecord record) {
            storage.add(record);
        }

        @Override
        public List<SearchRecord> load() {
            return new ArrayList<>(storage);
        }

        @Override
        public void deleteAll() {
            deleteCalled = true;
            storage.clear();
        }
    }

    /**
     * Presenter stub that captures output data.
     */
    private static class PresenterStub implements DeleteHistoryOutputBoundary {
        DeleteHistoryOutputData received;

        @Override
        public void present(DeleteHistoryOutputData data) {
            this.received = data;
        }
    }

    /**
     * Test: interactor calls deleteAll() and presenter receives success=true.
     */
    @Test
    void testDeleteHistorySuccess() {
        FakeGateway gateway = new FakeGateway();
        PresenterStub presenter = new PresenterStub();

        DeleteHistoryInteractor interactor =
                new DeleteHistoryInteractor(gateway, presenter);

        interactor.execute();

        assertTrue(gateway.deleteCalled);
        assertNotNull(presenter.received);
        assertTrue(presenter.received.isSuccess());
        assertTrue(gateway.storage.isEmpty());
    }
}
