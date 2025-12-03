package usecase.delete_history;

/**
 * Output boundary for the Delete Search History use case.
 *
 * <p>This interface defines how the interactor communicates the result of a
 * delete-history operation to the presenter. The presenter is responsible for
 * formatting the output data and preparing it for the view model.</p>
 */
public interface DeleteHistoryOutputBoundary {

    /**
     * Prepares the output data after the delete-history operation completes.
     *
     * @param data the result of the delete operation, typically indicating success
     */
    void present(DeleteHistoryOutputData data);
}
