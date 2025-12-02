package interface_adapter.search_history;

import usecase.search_history.SearchHistoryInputData;
import entity.SearchRecord;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * File-based implementation of {@link SearchHistoryInputData}.
 */
public class SearchHistoryGateway implements SearchHistoryInputData {

    private static final String FILE_PATH = "search_history.txt";

    /**
     * Saves a search record by appending it to the history file.
     *
     * @param record the record to save
     */
    @Override
    public void save(SearchRecord record) {
        try (FileWriter writer = new FileWriter(FILE_PATH, true)) {
            writer.write(
                    record.getOrigin() + "|" +
                            record.getDestination() + "|" +
                            record.getBikeTime() + "|" +
                            record.getBikeCost() + "|" +
                            record.getWalkTime() + "\n"
            );
        } catch (IOException ignored) {
        }
    }

    /**
     * Loads all stored search history records from the file.
     *
     * @return a list of {@link SearchRecord} objects parsed from the file
     */
    @Override
    public List<SearchRecord> load() {
        List<SearchRecord> history = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] p = line.split("\\|");
                if (p.length == 5) {
                    history.add(new SearchRecord(
                            p[0],               // origin
                            p[1],               // destination
                            Double.parseDouble(p[2]), // bike time
                            Double.parseDouble(p[3]), // bike cost
                            Double.parseDouble(p[4])  // walk time
                    ));
                }
            }
        } catch (IOException ignored) {
            // file might not exist — treat as empty history
        }

        return history;
    }

    /**
     * Deletes all search history by clearing the file.
     */
    @Override
    public void deleteAll() {
        try {
            new FileWriter(FILE_PATH, false).close(); // overwrite with empty file
        } catch (IOException ignored) {
        }
    }
}
//