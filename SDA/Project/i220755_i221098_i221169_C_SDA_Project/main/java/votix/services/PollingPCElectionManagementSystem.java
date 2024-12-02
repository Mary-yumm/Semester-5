package votix.services;

import votix.models.*;

import java.util.ArrayList;
import java.util.List;

public class PollingPCElectionManagementSystem implements ElectionManagementSystem {
    private PersistenceHandler ph;
    private Area area;
    private int stationId;
    private int systemId;
    private String mac;

    public PollingPCElectionManagementSystem() {
        area = new Area();
    }

    public boolean authorizePollingStaff(String username, String password,String mac_address) {
        // Implementation to authorize polling staff
        int is_verified = ph.verifyStaff(username,password,mac_address);
        this.mac = mac_address;
        if(is_verified == 0) {
            return false;
        }
        else{
            stationId = is_verified;
            int areaID = ph.fetchArea(is_verified);
            String areaName = ph.fetchAreaName(areaID);
            systemId = ph.verifyMacAddress(stationId,mac);
            area.setAreaID(areaID);
            area.setAreaName(areaName);

            return true;
        }
    }

    public void initializeArrays(){
        area.setCands(ph.fetchCandidates(area.getAreaID()));
        area.setStations(ph.fetchStations(area.getAreaID()));

    }

    public void setMac(String mac){
        this.mac = mac;
    }

    public String getMac(){
        return mac;
    }
    public boolean isVoterRegistered(String name,String cnic) {
        // Implementation to check if a voter is registered
        return ph.isVoterRegistered(name,cnic,getAreaId());
    }

    public int getAreaId(){
        return area.getAreaID();
    }
    public String getAreaName(){
        return area.getAreaName();
    }

    public int getStationId(){
        return stationId;
    }

    public void setStationId(int id){
        stationId = id;
    }

    public void castVote(int candidateId) {
        // Implementation to cast a vote
        ph.updateVoteCount(candidateId,getAreaId());

    }

    public void updateVoterStatus(String cnic) {
        ph.changeVoterStatus(cnic);
        area.updateVoterStatus(cnic,stationId);
    }

    public boolean getVoterStatus(String cnic,int stationid){
        return area.getVoterStatus(cnic,stationid);
    }

    @Override
    public void setPersistenceHandler(PersistenceHandler handler) {
        this.ph = handler;
    }

    @Override
    public ArrayList<Integer> getStations(String searchArea) {
        return null;
    }

    @Override
    public ArrayList<Integer> getStations() {
        return null;
    }

    public PollingStation getStation(int id) {
        for(PollingStation ps:area.getStations()){
            if(ps.getStationID() == id){
                return ps;
            }
        }
        return null;
    }

    @Override
    public ArrayList<Candidate> getCands() {
        return area.getCands();

    }

    @Override
    public void createLogEntry(String entry) {
        ph.createLog(entry);
    }

    @Override
    public List<Log> viewLogs() {
        return List.of();
    }

    @Override
    public ArrayList<ElectionResult> getForm(int stID, String searchArea, String napa) {
        return null;
    }

    public Voter getVoterByCnic(String cnic){
        return ph.getVoterByCnic(cnic);
    }

    public void setSystemInactive(int systemID) {
        ph.setSystemInactive(systemID);
    }

    public void setSystemActive(int systemID) {
        ph.setSystemActive(systemID);
    }
    public int getSystemID(){
        return systemId;
    }



}
