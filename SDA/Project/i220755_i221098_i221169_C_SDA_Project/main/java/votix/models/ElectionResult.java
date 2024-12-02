package votix.models;

import java.util.ArrayList;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class ElectionResult {

    private ArrayList<CandidateVoteRecord> candidateVoteRecords;

    private String areaName;
    private String candidateName;
    private String partyName;
    private int voteCount;

    public ElectionResult(String area, String candidateName, String partyName, int voteCount) {
        this.areaName = area;
        this.candidateName = candidateName;
        this.partyName = partyName;
        this.voteCount = voteCount;
    }

    @Override
    public String toString() {
        return "ElectionResult{" +
                "area='" + areaName + '\'' +
                ", candidateName='" + candidateName + '\'' +
                ", partyName='" + partyName + '\'' +
                ", voteCount=" + voteCount +
                '}';
    }

    public ElectionResult() {

    }

    // Getters and setters for the class properties
    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getCandidateName() {
        return candidateName;
    }

    public void setCandidateName(String candidateName) {
        this.candidateName = candidateName;
    }

    public String getPartyName() {
        return partyName;
    }

    public void setPartyName(String partyName) {
        this.partyName = partyName;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }

    public void setResult(String candidateId) {

        for (CandidateVoteRecord record : candidateVoteRecords) {
            if (record.candidateId.equals(candidateId)) {
                record.voteCount++; // Increment vote count for the candidate
                System.out.println("Vote recorded for candidate: " + candidateId);
                return; // Exit after finding the candidate
            }
        }
        System.out.println("Candidate not found in area");
    }
}
