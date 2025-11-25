package usecase.search_history;
import java.util.List;

/**
 * Interface for storing and loading search history.
 */
public interface SearchHistoryData {

    /**
     * Save the search record.
     */
    void save(SearchRecord record);

    /**
     * Load all saved search records.
     */
    List<SearchRecord> load();
}
