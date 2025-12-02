package interface_adapter;

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
                    record.getOrigin() + "|"
                            + record.getDestination() + "|"
                            + record.getBikeTime() + "|"
                            + record.getBikeCost() + "|"
                            + record.getWalkTime() + "|"
                            + record.getTimeSavedMinutes() + "\n"
            );
        }
        catch (IOException ignored) {
        }
    }

    /**
     * Loads all stored search history records from the file.
     *
     * @return a list of {@link SearchRecord} objects parsed from the file
     */
    @Override
    public List<SearchRecord> load() {
        final List<SearchRecord> history = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;

            while ((line = reader.readLine()) != null) {
                final String[] p = line.split("\\|");
                if (p.length >= 5) {
                    final double bikeTime = Double.parseDouble(p[2]);
                    final double bikeCost = Double.parseDouble(p[3]);
                    final double walkTime = Double.parseDouble(p[4]);

                    // Support both old (5-field) and new (6-field) formats.
                    final double timeSaved = p.length >= 6
                            ? Double.parseDouble(p[5])
                            : walkTime - bikeTime;

                    history.add(new SearchRecord(
                            p[0],
                            p[1],
                            bikeTime,
                            bikeCost,
                            walkTime,
                            timeSaved
                    ));
                }
            }
        }
        catch (IOException ignored) {
            // no existing files (empty history).
        }

        return history;
    }
}
