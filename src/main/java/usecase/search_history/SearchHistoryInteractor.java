package usecase.search_history;

/**
 * The interactor for the Search History use case.
 *
 * <p>
 * This class retrieves previously saved search records from the
 * data access gateway.
 */
public class SearchHistoryInteractor implements SearchHistoryInputBoundary {

    private final SearchHistoryInputData gateway;
    private final SearchHistoryOutputBoundary presenter;

    /**
     * Creates a new SearchHistoryInteractor.
     *
     * @param gateway   the data access interface used to load stored search history
     * @param presenter the output boundary that formats the data for the UI
     */
    public SearchHistoryInteractor(SearchHistoryInputData gateway,
                                   SearchHistoryOutputBoundary presenter) {
        this.gateway = gateway;
        this.presenter = presenter;
    }

    /**
     * Executes the Search History use case by loading all stored
     * search records and sending them to the output boundary.
     */
    @Override
    public void execute() {
        var records = gateway.load();
        presenter.present(new SearchHistoryOutputData(records));
    }
}
//