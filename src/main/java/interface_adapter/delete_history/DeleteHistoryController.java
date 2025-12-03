package interface_adapter.delete_history;

import usecase.delete_history.DeleteHistoryInputBoundary;

/**
 * Controller for the Delete Search History use case.
 *
 * <p>This controller receives user actions from the UI layer and forwards
 * the request to the interactor through the input boundary. It performs
 * no business logic, maintaining the separation of concerns required by
 * Clean Architecture.</p>
 */
public class DeleteHistoryController {

    /** The input boundary that handles the delete-history application logic. */
    private final DeleteHistoryInputBoundary interactor;

    /**
     * Constructs a new {@code DeleteHistoryController}.
     *
     * @param interactor the input boundary responsible for executing the use case
     */
    public DeleteHistoryController(DeleteHistoryInputBoundary interactor) {
        this.interactor = interactor;
    }

    /**
     * Executes the Delete Search History use case.
     *
     * <p>This method is typically invoked by a UI event, such as a button click,
     * and delegates the request directly to the interactor.</p>
     */
    public void execute() {
        interactor.execute();
    }
}
