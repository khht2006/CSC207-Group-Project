package usecase.delete_history;

/**
 * Output data for the Delete Search History use case.
 *
 * <p>This data transfer object represents the result of a delete-history
 * operation. It is passed from the interactor to the presenter so that the
 * presenter can prepare the appropriate view model.</p>
 */
public class DeleteHistoryOutputData {

    /** Indicates whether the delete operation completed successfully. */
    private final boolean success;

    /**
     * Constructs a new {@code DeleteHistoryOutputData}.
     *
     * @param success {@code true} if all search history was deleted successfully;
     *                {@code false} otherwise
     */
    public DeleteHistoryOutputData(boolean success) {
        this.success = success;
    }

    /**
     * Returns whether the delete-history operation succeeded.
     *
     * @return {@code true} if the operation was successful, otherwise {@code false}
     */
    public boolean isSuccess() {
        return success;
    }
}