package usecase.delete_history;

/**
 * Input boundary for the Delete Search History use case.
 *
 * <p>This interface defines the method that controllers must call when the
 * user requests to delete all stored search history. Implementations of this
 * interface encapsulate the application-specific logic for clearing saved
 * search records.</p>
 */
public interface DeleteHistoryInputBoundary {

    /**
     * Executes the Delete Search History use case.
     *
     * <p>Calling this method should trigger the interactor to remove
     * all previously saved search history from the data storage layer.</p>
     */
    void execute();
}
