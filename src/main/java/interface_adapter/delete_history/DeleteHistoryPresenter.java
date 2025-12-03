package interface_adapter.delete_history;

import usecase.delete_history.DeleteHistoryOutputBoundary;
import usecase.delete_history.DeleteHistoryOutputData;

/**
 * Presenter for the Delete Search History use case.
 *
 * <p>This presenter receives the output data from the interactor and updates
 * the corresponding view model. It acts as the translation layer between the
 * application logic (use case) and the UI.</p>
 */
public class DeleteHistoryPresenter implements DeleteHistoryOutputBoundary {

    /** View model representing the state of the delete-history UI component. */
    private final DeleteHistoryViewModel viewModel;

    /**
     * Constructs a new {@code DeleteHistoryPresenter}.
     *
     * @param viewModel the view model that stores UI-related state for this use case
     */
    public DeleteHistoryPresenter(DeleteHistoryViewModel viewModel) {
        this.viewModel = viewModel;
    }

    /**
     * Updates the view model based on the result of the delete-history operation.
     *
     * @param data the output data containing the success status of the operation
     */
    @Override
    public void present(DeleteHistoryOutputData data) {
        viewModel.setDeleted(data.isSuccess());
    }
}
