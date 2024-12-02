package votix.controllers.PollingPC;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import votix.models.Voter;
import votix.models.PollingStation;

import java.util.List;

public class PollingStationInfoController {

    @FXML
    private VBox voterTable;

    @FXML
    private Label votedCountLabel;

    @FXML
    private Label notVotedCountLabel;

    @FXML
    private Label totalVotersLabel;


    private PollingStation pollingStation; // Reference to the PollingStation object

    public void setPollingStation(PollingStation station) {
        this.pollingStation = station;
        if (pollingStation != null) {
            System.out.println("polling station in polling station info: " + pollingStation.getStationID());
        }
        populateVoterTable();
        updateVoterCounts(); // Update counts after populating the table
    }


    private void populateVoterTable() {
        List<Voter> voters = pollingStation.getVoters();
        for (Voter voter : voters) {
            addVoterRow(voter);
        }
    }

    private void addVoterRow(Voter voter) {
        // Create the main AnchorPane for the row
        AnchorPane row = new AnchorPane();
        row.getStyleClass().add("table-row");

        // Apply zebra striping
        if (voterTable.getChildren().size() % 2 == 0) {
            row.setStyle("-fx-background-color: #f5f5f5;"); // Even row color
        } else {
            row.setStyle("-fx-background-color: #f7f7f7;"); // Odd row color
        }

        // CNIC Label
        Label cnicLabel = new Label(voter.getId());
        cnicLabel.getStyleClass().add("table-cell");
        AnchorPane.setLeftAnchor(cnicLabel, 10.0); // Position CNIC to the left
        row.getChildren().add(cnicLabel);

        // Status Label
        Label statusLabel = new Label(voter.getStatus() ? "Voted" : "Not Voted");
        statusLabel.getStyleClass().add("table-cell");

        // Set the color based on the status
        // Apply subtle colors using CSS classes
        if (voter.getStatus()) {
            statusLabel.getStyleClass().add("voted");
        } else {
            statusLabel.getStyleClass().add("not-voted");
        }

        // Center align the status label
        AnchorPane.setLeftAnchor(statusLabel, 300.0); // Adjust left anchor to your layout
        AnchorPane.setRightAnchor(statusLabel, 10.0); // Add right anchor to balance

        statusLabel.setStyle("-fx-alignment: center;"); // Center text within the label
        row.getChildren().add(statusLabel);

        // Add the row to the voter table
        voterTable.getChildren().add(row);
    }

    private void updateVoterCounts() {
        if (pollingStation == null) return;

        int votedCount = 0;
        int notVotedCount = 0;

        List<Voter> voters = pollingStation.getVoters();
        int totalVoters = voters.size();

        for (Voter voter : voters) {
            if (voter.getStatus()) {
                votedCount++;
            } else {
                notVotedCount++;
            }
        }

        // Update labels with the counts
        totalVotersLabel.setText("Total Registered: " + totalVoters);
        votedCountLabel.setText("Voted: " + votedCount);
        notVotedCountLabel.setText("Not Voted: " + notVotedCount);
    }



}
