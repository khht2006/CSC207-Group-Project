package interface_adapter;

import usecase.search_history.SearchHistoryOutputBoundary;
import usecase.search_history.SearchHistoryOutputData;

public class SearchHistoryPresenter implements SearchHistoryOutputBoundary {

    private final SearchHistoryViewModel viewModel;

    public SearchHistoryPresenter(SearchHistoryViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void present(SearchHistoryOutputData data) {
        viewModel.setHistory(data.getRecords());
    }
}
