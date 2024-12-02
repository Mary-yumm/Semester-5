package votix.models;

import java.util.ArrayList;

public class Area {
    // Attributes
    private ArrayList<Candidate> cands;
    private ArrayList<PollingStation> stations;
    private ElectionResult result;
    private int areaID;
    private String areaName;

    // Constructor
    public Area() {
        this.cands = new ArrayList<>();
        this.stations = new ArrayList<>();
        //this.areaID = areaID;
        result = new ElectionResult();
    }

    // Methods
    public void setCands(ArrayList<Candidate> cands) {
        this.cands = cands;
    }
    public void setStations(ArrayList<PollingStation> stations) {
        this.stations = stations;
    }
    public void setAreaID(int areaID) {
        this.areaID = areaID;
    }
    public ArrayList<Candidate> getCands() {
        return cands;
    }
    public ArrayList<PollingStation> getStations() {
        return stations;
    }
    public int getAreaID(){
        return areaID;
    }
    public String getAreaName(){
        return areaName;
    }
    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }
    public void monitorSystemStatus() {
        // Logic to monitor system status
    }

    public void addCandidate(Candidate candidate) {
        this.cands.add(candidate);
    }

    public PollingStaff manageStaffByStation() {
        // Logic to manage staff by station
        return new PollingStaff(); // Placeholder return
    }

    public void viewStaff(int stationID) {
        // Logic to view staff at a specific station
    }

    public void updateVoterStatus(String cnic,int stationid){
        for(PollingStation ps : stations){
            if(ps.getStationID() == stationid){

                for(Voter voter: ps.getVoters()){
                    if(voter.getId().equals(cnic)){
                        voter.setStatus(true);
                        return;
                    }
                }
            }
        }
    }

    public boolean getVoterStatus(String cnic,int stationid){
        for(PollingStation ps : stations){
            System.out.println("heree" + stationid + " , " + ps.getStationID());
            if(ps.getStationID() == stationid){
                System.out.println("in stations: ");
                for(Voter voter: ps.getVoters()){
                    if(voter.getId().equals(cnic)){
                        System.out.println("voter " + cnic + " status: " + voter.getStatus());
                        return voter.getStatus();
                    }
                }
            }
        }
        System.out.println("voter " + cnic + " status: false not found ");
        return false;
    }


}
