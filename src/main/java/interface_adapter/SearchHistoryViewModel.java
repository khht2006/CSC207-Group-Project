package interface_adapter;

import java.util.List;

import entity.SearchRecord;

/**
 * View model for storing the loaded search history records.
 */
public class SearchHistoryViewModel {

    /** The list of past search history records. */
    private List<SearchRecord> history;

    /**
     * Sets the list of search history records.
     *
     * @param history the list of saved search records
     */
    public void setHistory(List<SearchRecord> history) {
        this.history = history;
    }

    /**
     * Returns the list of search history records.
     *
     * @return the stored search history records
     */
    public List<SearchRecord> getHistory() {
        return history;
    }
}
