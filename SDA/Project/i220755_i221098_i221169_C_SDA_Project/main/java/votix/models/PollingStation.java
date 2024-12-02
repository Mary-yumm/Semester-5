package votix.models;

import java.util.ArrayList;

// PollingStation class
public class PollingStation {
    private int stationID;
    private ArrayList<PollingStaff> assignedPollingStaff = new ArrayList<>();
    private ArrayList<PollingStationPC> pollingPCs = new ArrayList<>();
    private ArrayList<Voter> voters = new ArrayList<>();

    public PollingStation(int sid) {
        this.stationID = sid;
        this.pollingPCs = null;
        this.assignedPollingStaff = null;
        this.voters = null;
    }
    public PollingStation() {
    }



    // Getters and Setters
    public int getStationID() {
        return stationID;
    }

    public void setStationID(int stationID) {
        this.stationID = stationID;
    }


    public ArrayList<PollingStaff> getAssignedPollingStaff() {
        return assignedPollingStaff;
    }

    public void setAssignedPollingStaff(ArrayList<PollingStaff> assignedPollingStaff) {
        this.assignedPollingStaff = assignedPollingStaff;
    }

    public ArrayList<PollingStationPC> getPollingPCs() {
        return this.pollingPCs;
    }

    public void setPollingPCs(ArrayList<PollingStationPC> pollingPCs) {
        this.pollingPCs = pollingPCs;
    }

    public ArrayList<Voter> getVoters() {
        return voters;
    }

    public void setVoters(ArrayList<Voter> voters) {
        this.voters = voters;
    }

    public void monitorSystemStatus() {
        // Monitor system status
    }

    public PollingStaff manageAssignedStaff() {
        // Manage assigned staff
        return new PollingStaff();
    }
}


