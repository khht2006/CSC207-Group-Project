package interface_adapter.delete_history;

/**
 * View model for the Delete Search History UI component.
 *
 * <p>This view model stores the state resulting from a delete-history
 * operation. It is updated by the presenter and read by the view to
 * determine how the UI should respond.</p>
 */
public class DeleteHistoryViewModel {

    /** Indicates whether the search history was successfully deleted. */
    private boolean deleted;

    /**
     * Sets whether the delete-history operation succeeded.
     *
     * @param deleted {@code true} if the operation was successful; {@code false} otherwise
     */
    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    /**
     * Returns whether the search history was successfully deleted.
     *
     * @return {@code true} if the delete-history operation was successful; otherwise {@code false}
     */
    public boolean isDeleted() {
        return deleted;
    }
}
