package votix.models;

// CandidateVoteRecord class to hold the details of candidate votes
class CandidateVoteRecord {
    String candidateId;
    int voteCount;

    public CandidateVoteRecord(String candidateId, int voteCount) {
        this.candidateId = candidateId;
        this.voteCount = voteCount;
    }
}
