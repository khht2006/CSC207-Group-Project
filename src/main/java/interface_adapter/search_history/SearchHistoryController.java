package interface_adapter.search_history;

import usecase.search_history.SearchHistoryInputBoundary;

/**
 * The controller for the Search History use case.
 *
 * <p>
 * This class receives a user request to view past searches and
 * delegates the action to the SearchHistoryInputBoundary (the interactor).
 * </p>
 */
public class SearchHistoryController {

    private final SearchHistoryInputBoundary interactor;

    /**
     * Creates a new controller for the Search History use case.
     *
     * @param interactor the interactor that handles the search history retrieval
     */
    public SearchHistoryController(SearchHistoryInputBoundary interactor) {
        this.interactor = interactor;
    }

    /**
     * Executes the search history retrieval use case.
     */
    public void execute() {
        interactor.execute();
    }
}
