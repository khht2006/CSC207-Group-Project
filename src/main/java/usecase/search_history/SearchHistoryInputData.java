package usecase.search_history;

import entity.SearchRecord;

import java.util.List;

/**
 * Gateway interface for saving and loading search history records.
 * <p>
 * Implementations provide persistent storage for {@link SearchRecord} objects.
 */
public interface SearchHistoryInputData {

    /**
     * Saves a single search history record.
     *
     * @param record the record to save
     */
    void save(SearchRecord record);

    /**
     * Returns all saved search history records.
     *
     * @return a list of previously saved records
     */
    List<SearchRecord> load();
    void deleteAll();

}
//