package votix.services;

import votix.models.*;

import java.util.ArrayList;
import java.util.List;

public class AdminElectionManagementSystem implements ElectionManagementSystem{

    private PersistenceHandler ph;
    private ArrayList<Area> area;
    private String electionType;


    public AdminElectionManagementSystem(PersistenceHandler ph) {

        area = new ArrayList<>();
        this.ph = ph;
    }
    public AdminElectionManagementSystem() {

        area = new ArrayList<>();
    }
    public PersistenceHandler getPh() {return ph;}

    public void setPh(PersistenceHandler ph) {this.ph = ph;}

    public void displayAllResults() {
    }

    public ArrayList<ElectionResult> fetchElectionResults(String Napa)
    {
        return ph.fetchElectionResults(Napa);
    }
    public ArrayList<ElectionResult> searchElectionResultsByArea(String areaName) {
        return ph.searchByArea(areaName);
    }

    public ArrayList<ElectionResult> searchElectionResultsByCandidate(String candidateName) {
        return ph.searchByCandidate(candidateName);
    }

    public ArrayList<ElectionResult> searchElectionResultsByParty(String partyName) {
        return ph.searchByParty(partyName);
    }
    public boolean addCandidate(Candidate cand, String area) {
        return ph.addCandidate(cand, area);
    }

    public void managePollingStaff(String operation, int id) {
        if(operation.equals("deactivate")){
            ph.deactivatePollingStaffAccount(id);
        }
        else if(operation.equals("activate")){
            ph.activatePollingStaffAccount(id);
        }
    }
    public ArrayList<String> getPartyNames(){
        return ph.getPartyNames();
    }
    public void initiateSystem() {
        // Implementation to initiate the system
    }

//    public ElectionResult getElectionResult() {
//        return results;
//    }

    public void recountVotes() {
        // Implementation to recount votes
    }

    public void monitorPollingStation() {
        // Implementation to monitor polling station
    }

    public void monitorActiveSystems() {
        // Implementation to monitor active systems
    }

    public void inspectAuditLogs() {
        // Implementation to inspect audit logs
    }

    public void terminateSystem() {
        // Implementation to terminate the system
    }

    public void generateElectionReport() {
        // Implementation to generate election report
    }

    public void issueElectionReport() {
        // Implementation to issue an election report
    }

    public void accessElectionForms() {
        // Implementation to access election forms
    }

    public void displayElectionResults() {
        // Implementation to display election results
    }

    public void filterLogs(String criteria) {
        // Implementation to filter logs
    }

    public List<Log> viewLogs() {
        // Implementation to view a specific log entry
        return ph.ViewLogs();
    }

    public List<PollingStationPC> getPollingPCs(){
        return ph.getPollingPCs();
    }
    public PersistenceHandler getPersistentHandler() {return ph;}
    @Override
    public void setPersistenceHandler(PersistenceHandler ph) {this.ph = ph;}
    public boolean authorizeAdmin(String username, String password)
    {
        int is_verified = ph.verifyAdmin(username,password);
        return is_verified == 1;
    }



    public ArrayList<String> getAreaID(){
    return ph.getAreaID();
    }
//    public void setElectionResult(ElectionResult result) {
//        this.electionResult = result;
//    }

    public void setElectionType(String electionType) {
        this.electionType = electionType;
    }

    public String getElectionType() {
        return electionType;
    }

    public ArrayList<Candidate> getAllCand(){
        return ph.fetchAllCandidates();
    }
    public void updatePollingStaff(Integer staffid, String username, String password, Integer stationid) {
        ph.updatePollingStaffAccount(username, password,staffid, stationid);
    }

    // not decided yet

    public ArrayList<Integer> getStations() {
        // Implementation to get stations
        return ph.getStations();
    }

    public ArrayList<Candidate> getCands() {
        // Implementation to get candidates
        System.out.println("Getting candidates:.......");
        if(ph!=null){

            return ph.fetchCandidates(1);
      }
        System.out.println("Errorr...............");
        return new ArrayList<>();
    }


    public void createLogEntry(String entry) {
        // Implementation to create a log entry
    }


    public void searchStaffByStaffName() {
      //  return ph.getStaffByName();
    }
    public boolean addPollingStaff(PollingStaff p){return ph.addPollingStaffAccount(p);}

    public void searchStaffByAreaID() {
    }
    public ArrayList<PollingStaff> getPollingStaff() {
        return ph.getStaffList();
    }

    public ArrayList<Object> getStaffAssignments() {
       return ph.getStaffAssignments();
    }

    public void changeSystemStatus(int systemID,boolean status){
        if(status){
            ph.setSystemActive(systemID);
        }
        else{
            ph.setSystemInactive(systemID);
        }

    }

    public ArrayList<ElectionResult> WinnerByArea(String searchArea, String napa) {
        return ph.WinnerByArea(searchArea,napa);
    }
    public int fetchTotalVotesByArea(String areaName){
        return ph.fetchTotalVotesByArea(areaName);
    }

    public ArrayList<Integer> getStations(String searchArea) {
        return ph.getStations(searchArea);
    }
    public ArrayList<ElectionResult> getForm(int stID, String searchArea,String Napa )
    {
        return ph.getForm(stID,searchArea, Napa);
    }
    public List<Object[]> getCandidateVotes() {
        return ph.getCandidateVotes();
    }
}
