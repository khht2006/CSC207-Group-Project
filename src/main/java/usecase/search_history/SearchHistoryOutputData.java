package usecase.search_history;

import entity.SearchRecord;

import java.util.List;

/**
 * Output data for the Search History use case.
 */
public class SearchHistoryOutputData {
    private final List<SearchRecord> records;

    /**
     * Creates a SearchHistoryOutputData object containing saved search records.
     *
     * @param records the list of search history records retrieved from the data source
     */
    public SearchHistoryOutputData(List<SearchRecord> records) {
        this.records = records;
    }

    /**
     * Returns the list of stored search history records.
     *
     * @return the search history records
     */
    public List<SearchRecord> getRecords() {
        return records;
    }
}
