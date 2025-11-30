package interface_adapter;

import usecase.search_history.SearchHistoryData;
import entity.SearchRecord;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * File-based implementation of {@link SearchHistoryData}.
 */
public class SearchHistoryGateway implements SearchHistoryData {

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
        } catch (IOException ignored) {}
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
                            p[0],
                            p[1],
                            Double.parseDouble(p[2]),
                            Double.parseDouble(p[3]),
                            Double.parseDouble(p[4])
                    ));
                }
            }
        } catch (IOException ignored) {}

        return history;
    }
}
