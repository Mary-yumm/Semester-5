package votix.models;

public class Log {
    private int logId;
    private String action;
    private String timeStamp;

    // Constructor
    public Log(int logId, String action, String timeStamp) {
        this.logId = logId;
        this.action = action;
        this.timeStamp = timeStamp;
    }

    // Getters and Setters
    public int getLogId() {
        return logId;
    }

    public void setLogId(int logId) {
        this.logId = logId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    // ToString method for easy formatting
    @Override
    public String toString() {
        return String.format("Log ID: %d | Action: %s | Timestamp: %s", logId, action, timeStamp);
    }
}
