package usecase.delete_history;

import usecase.search_history.SearchHistoryInputData;

/**
 * Interactor for the Delete Search History use case.
 *
 * <p>This interactor coordinates the deletion of all stored search
 * history by delegating to the data-access gateway and notifying the
 * output boundary (presenter) after completion.</p>
 */
public class DeleteHistoryInteractor implements DeleteHistoryInputBoundary {

    /** Gateway responsible for interacting with the search history data storage. */
    private final SearchHistoryInputData gateway;

    /** Presenter responsible for preparing the view model after deletion. */
    private final DeleteHistoryOutputBoundary presenter;

    /**
     * Constructs a new {@code DeleteHistoryInteractor}.
     *
     * @param gateway   the gateway used to access and modify stored search history
     * @param presenter the presenter that formats the output data after deletion
     */
    public DeleteHistoryInteractor(SearchHistoryInputData gateway,
                                   DeleteHistoryOutputBoundary presenter) {
        this.gateway = gateway;
        this.presenter = presenter;
    }

    /**
     * Executes the Delete Search History use case.
     *
     * <p>This method instructs the gateway to remove all saved search
     * records and then notifies the presenter that the operation
     * completed successfully.</p>
     */
    @Override
    public void execute() {
        gateway.deleteAll();
        presenter.present(new DeleteHistoryOutputData(true));
    }
}
