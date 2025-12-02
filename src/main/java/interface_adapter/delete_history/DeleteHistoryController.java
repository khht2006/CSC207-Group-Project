package interface_adapter.delete_history;

import usecase.delete_history.DeleteHistoryInputBoundary;

public class DeleteHistoryController {

    private final DeleteHistoryInputBoundary interactor;

    public DeleteHistoryController(DeleteHistoryInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void execute() {
        interactor.execute();
    }
}
//