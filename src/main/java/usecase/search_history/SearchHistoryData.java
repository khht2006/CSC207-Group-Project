package usecase.search_history;

import java.util.List;

/**
 * Saves and loads search history records.
 */
public interface SearchHistoryData {
    void save(SearchRecord record);
    java.util.List<SearchRecord> load();
}
