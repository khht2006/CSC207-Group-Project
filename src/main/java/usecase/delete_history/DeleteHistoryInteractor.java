package usecase.delete_history;

import usecase.search_history.SearchHistoryInputData;

public class DeleteHistoryInteractor implements DeleteHistoryInputBoundary {

    private final SearchHistoryInputData gateway;
    private final DeleteHistoryOutputBoundary presenter;

    public DeleteHistoryInteractor(SearchHistoryInputData gateway,
                                   DeleteHistoryOutputBoundary presenter) {
        this.gateway = gateway;
        this.presenter = presenter;
    }

    @Override
    public void execute() {
        gateway.deleteAll();
        presenter.present(new DeleteHistoryOutputData(true));
    }
}
