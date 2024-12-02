package votix.controllers.PollingPC;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import votix.models.Candidate;
import votix.models.Voter;
import votix.services.PollingPCElectionManagementSystem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.net.HttpURLConnection;
import java.net.URL;

public class CastVoteController {

    @FXML
    private VBox candidateTable;

    @FXML
    private Button submitButton;

    private PollingPCElectionManagementSystem ems; // This needs to be passed from wherever you call this controller
    private CheckBox lastSelectedCheckbox; // Track the last selected checkbox

    @FXML
    private Label voterNameLabel;

    @FXML
    private Label voterCnicLabel;

    // Map to store each checkbox with its associated candidate ID
    private Map<CheckBox, Integer> checkboxCandidateMap = new HashMap<>();
    String cnic;

    // Method to set the Election Management System and populate candidates
    public void setElectionManagementSystem(PollingPCElectionManagementSystem system,String cnic) {
        this.ems = system;
        this.cnic = cnic;
        // Assuming the voter information is available from the EMS
        Voter voter = ems.getVoterByCnic(cnic); // You will need to implement this method to retrieve the voter by CNIC

        if (voter != null) {
            voterNameLabel.setText("Voter Name: " + voter.getName());
            voterCnicLabel.setText("CNIC: " + voter.getId());
        }

        populateCandidates();
    }
    public void handleSubmit(ActionEvent actionEvent) {
        // Check if there is a selected checkbox
        if (lastSelectedCheckbox != null && lastSelectedCheckbox.isSelected()) {
            // Retrieve the candidate ID from the map using the selected checkbox
            Integer candidateId = checkboxCandidateMap.get(lastSelectedCheckbox);
            if (candidateId != null) {

                ems.castVote(candidateId);
                ems.updateVoterStatus(cnic);
                ems.createLogEntry("A vote was cast for CNIC: " + cnic + " at polling pc " + ems.getSystemID()+", Station "+ems.getStationId() + ", Area " + ems.getAreaId() + ", " + ems.getAreaName());
                // Notify ViewLogsController via SSE
                notifyViewLogs("Vote cast for candidate ID: " + candidateId);
                System.out.println("voteddd");

            }

            // Close the window
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.close();
        }
    }

    private void notifyViewLogs(String message) {
        try {
            URL url = new URL("http://100.91.228.86:8080/notify"); // Replace localhost with the backend server's IP
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            // Add the message parameter to the request
            String data = "message=" + message;
            connection.getOutputStream().write(data.getBytes());
            connection.getOutputStream().flush();
            connection.getOutputStream().close();

            connection.getResponseCode(); // Optional: Check response code

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // New method to populate candidates after ems is set
    private void populateCandidates() {
        if (ems != null) {
            System.out.println("EMS is not null, loading candidates...");
            List<Candidate> candidates = ems.getCands();
            for (Candidate candidate : candidates) {
                addCandidateRow(candidate);
            }
        } else {
            System.out.println("EMS is null!");
        }
    }

    private void addCandidateRow(Candidate candidate) {
        // Create the main AnchorPane for the row
        AnchorPane row = new AnchorPane();
        row.getStyleClass().add("table-row");

        // Apply zebra striping
        if (candidateTable.getChildren().size() % 2 == 0) {
            row.setStyle("-fx-background-color: #f5f5f5;"); // Even row color
        } else {
            row.setStyle("-fx-background-color: #f7f7f7;"); // Odd row color
        }

        // Candidate Name
        Label nameLabel = new Label(candidate.getName());
        nameLabel.getStyleClass().add("table-cell");
        AnchorPane.setTopAnchor(nameLabel, 10.0);
        AnchorPane.setLeftAnchor(nameLabel, 10.0);
        row.getChildren().add(nameLabel);

        // Party Name
        Label partyLabel = new Label(candidate.getPartyName());
        partyLabel.getStyleClass().add("table-cell");
        AnchorPane.setTopAnchor(partyLabel, 10.0);
        AnchorPane.setLeftAnchor(partyLabel, 400.0);
        row.getChildren().add(partyLabel);

        // Party Symbol
        ImageView partySymbolView = new ImageView(candidate.getPartySymbol());
        partySymbolView.setFitHeight(40);
        //mpartySymbolView.setFitWidth(40);
        partySymbolView.setPreserveRatio(true);  // Maintain the aspect ratio

        AnchorPane.setLeftAnchor(partySymbolView, 820.0);
        row.getChildren().add(partySymbolView);

        // Checkbox for voting
        CheckBox selectCheckbox = new CheckBox();
        AnchorPane.setLeftAnchor(selectCheckbox, 1080.0);
        AnchorPane.setTopAnchor(selectCheckbox, 10.0);
        row.getChildren().add(selectCheckbox);

        // Add the checkbox and candidate ID to the map
        checkboxCandidateMap.put(selectCheckbox, candidate.getCid());

        // Add listener to each checkbox
        selectCheckbox.setOnAction(event -> handleCheckboxSelection(selectCheckbox));

        // Add the row to the candidate table
        candidateTable.getChildren().add(row);
    }

    // Handle checkbox selection and ensure only one checkbox can be selected at a time
    private void handleCheckboxSelection(CheckBox currentCheckbox) {
        // If there was a previously selected checkbox, uncheck it
        if (lastSelectedCheckbox != null && lastSelectedCheckbox != currentCheckbox) {
            lastSelectedCheckbox.setSelected(false);
        }
        // Update the last selected checkbox to the current one
        lastSelectedCheckbox = currentCheckbox;
    }



}