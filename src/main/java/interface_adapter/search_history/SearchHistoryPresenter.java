package interface_adapter.search_history;

import usecase.search_history.SearchHistoryOutputBoundary;
import usecase.search_history.SearchHistoryOutputData;

/**
 * Presenter for the Search History use case.
 * Formats the retrieved search history records and updates the view model.
 */
public class SearchHistoryPresenter implements SearchHistoryOutputBoundary {

    private final SearchHistoryViewModel viewModel;

    /**
     * Creates a new {@code SearchHistoryPresenter}.
     *
     * @param viewModel the view model that will be updated with history data
     */
    public SearchHistoryPresenter(SearchHistoryViewModel viewModel) {
        this.viewModel = viewModel;
    }

    /**
     * Updates the view model with the loaded search history records.
     *
     * @param data the output data containing the search history records
     */
    @Override
    public void present(SearchHistoryOutputData data) {
        viewModel.setHistory(data.getRecords());
    }
}
//