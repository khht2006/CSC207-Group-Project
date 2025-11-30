package usecase.search_history;

/**
 * Output boundary for the Search History use case.
 */
public interface SearchHistoryOutputBoundary {
    /**
     * Presents the search history data to the output layer (presenter).
     *
     * @param data the output data containing the loaded search history records
     */
    void present(SearchHistoryOutputData data);
}
