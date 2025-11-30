package interface_adapter;

import entity.SearchRecord;
import java.util.List;

public class SearchHistoryViewModel {
    private List<SearchRecord> history;

    public void setHistory(List<SearchRecord> history) {
        this.history = history;
    }

    public List<SearchRecord> getHistory() {
        return history;
    }
}
