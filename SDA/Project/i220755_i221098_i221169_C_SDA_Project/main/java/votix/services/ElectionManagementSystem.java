package votix.services;

import votix.models.Candidate;
import votix.models.ElectionResult;
import votix.models.Log;

import java.util.ArrayList;
import java.util.List;

public interface ElectionManagementSystem {

    public abstract void setPersistenceHandler(PersistenceHandler handler);

    //public void setElectionResult(ElectionResult result);
    public ArrayList<Integer> getStations(String searchArea);


    // not decided yet

    public ArrayList<Integer> getStations();

    public ArrayList<Candidate> getCands();

    public void createLogEntry(String entry);

    List<Log> viewLogs();

    ArrayList<ElectionResult> getForm(int stID, String searchArea, String Napa);
}

