package interface_adapter.delete_history;

import usecase.delete_history.DeleteHistoryOutputBoundary;
import usecase.delete_history.DeleteHistoryOutputData;

public class DeleteHistoryPresenter implements DeleteHistoryOutputBoundary {

    private final DeleteHistoryViewModel viewModel;

    public DeleteHistoryPresenter(DeleteHistoryViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void present(DeleteHistoryOutputData data) {
        viewModel.setDeleted(data.isSuccess());
    }
}
//