package votix.services;


import votix.models.Candidate;
import votix.models.ElectionResult;
import votix.models.PollingStaff;
import votix.models.PollingStation;
import votix.models.*;

import java.util.ArrayList;
import java.util.List;

public abstract class PersistenceHandler {
    protected String username;
    protected String password;

    // Abstract methods to be implemented by subclasses
    public abstract ArrayList<ElectionResult> fetchElectionResults(String res);
    public abstract ArrayList<ElectionResult> searchByArea(String areaName);
    public abstract ArrayList<ElectionResult> searchByCandidate(String candidateName);
    public abstract ArrayList<ElectionResult> searchByParty(String partyName);

    public abstract ArrayList<String> reportData();
    public abstract ArrayList<String> getElectionForm();
    public abstract ArrayList<String> electionReportData();
    public abstract void createLog(String message);
    public abstract List<Log> ViewLogs();
    public abstract boolean addPollingStaffAccount(PollingStaff staff);
    public abstract boolean addCandidate(Candidate candidate, String area);
    public abstract int verifyStaff(String login, String password,String mac_address);

    public abstract int verifyMacAddress(int stationId, String currentMac);

    public abstract int verifyAdmin(String login, String password);
    public abstract List<PollingStationPC> getPollingPCs();
    public abstract ArrayList<Candidate> fetchCandidates(int areaid);
    public abstract boolean isVoterRegistered(String name,String cnic,int areaid);
    public abstract int fetchArea(int stationid);
    public abstract void updateVoteCount(int candid,int areaid);
    public abstract ArrayList<PollingStation> fetchStations(int areaID);
    public abstract void changeVoterStatus(String cnic);
    public abstract ArrayList<String> getPartyNames();
    public abstract ArrayList<String> getAreaID();
    public abstract ArrayList<Object> getStaffAssignments();
    public abstract ArrayList<PollingStaff> getStaffList();
    public abstract ArrayList<Integer> getStations();
    public abstract ArrayList<Integer> getStations(String area);
    public abstract Voter getVoterByCnic(String cnic);

    public abstract ArrayList<Candidate> fetchAllCandidates();
    public abstract void updatePollingStaffAccount(String username, String password, int staffid, int stationid);
    public abstract void deactivatePollingStaffAccount(int staffid);
    public abstract void activatePollingStaffAccount(int id);
    public abstract void setSystemInactive(int systemID);
    public abstract void setSystemActive(int systemID);
    public abstract ArrayList<ElectionResult> WinnerByArea(String areaName, String napa);
    public abstract int fetchTotalVotesByArea(String areaName);

    public abstract ArrayList<ElectionResult> getForm(int sID, String areaName, String Napa);
    public abstract List<Object[]> getCandidateVotes();
    public abstract String fetchAreaName(int areaID);
}


