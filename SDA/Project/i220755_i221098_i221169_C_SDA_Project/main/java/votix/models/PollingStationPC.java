package votix.models;

// PollingStationPC class
public class PollingStationPC {
    private String systemID;
    private boolean systemStatus;
    private String configurationSettings;
    private String areaName;
    private int stationID;

    // Methods
    public void initializeSystem() {
        // Initialize system
    }

    public void shutdownSystem() {
        // Shutdown system
    }

    public void updateSettings(String settings) {
        this.configurationSettings = settings;
    }

    public String getSystemID() {
        return systemID;
    }

    public void setSystemID(String systemID) {
        this.systemID = systemID;
    }

    public boolean getSystemStatus() {
        return systemStatus;
    }

    public void setSystemStatus(boolean systemStatus) {
        this.systemStatus = systemStatus;
    }

    public String getConfigurationSettings() {
        return configurationSettings;
    }

    public void setConfigurationSettings(String configurationSettings) {
        this.configurationSettings = configurationSettings;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public int getStationID() {
        return stationID;
    }

    public void setStationID(int stationID) {
        this.stationID = stationID;
    }
}

