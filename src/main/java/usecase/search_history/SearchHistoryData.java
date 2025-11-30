package usecase.search_history;

import entity.SearchRecord;

/**
 * Saves and loads search history records.
 */
public interface SearchHistoryData {
    void save(SearchRecord record);
    java.util.List<SearchRecord> load();
}
