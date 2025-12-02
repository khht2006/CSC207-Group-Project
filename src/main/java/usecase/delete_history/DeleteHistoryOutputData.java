package usecase.delete_history;

public class DeleteHistoryOutputData {
    private final boolean success;

    public DeleteHistoryOutputData(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }
}
