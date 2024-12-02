package votix.controllers.PollingPC;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import votix.controllers.PopUps.ErrorMessageController;
import votix.services.PollingPCElectionManagementSystem;

import java.io.IOException;

public class CaptureVoterInfoController {

    @FXML
    private TextField nameTextField;

    @FXML
    private TextField cnicTextField;

    @FXML
    private Label biometricInstructionLabel; // New label for biometric instructions

    private int stationId;

    private PollingPCElectionManagementSystem ems; // Your Election Management System instance
    public void setElectionManagementSystem(PollingPCElectionManagementSystem electionManagementSystem,int stationid) {

        this.ems = electionManagementSystem;
        this.stationId = stationid;
        if(ems!=null){
            System.out.println("Election Management System is already set");
        }
        else{
            System.out.println("Election Management System is null in capture voter info set");
        }
    }

    @FXML
    public void initialize() {
        restrictNameField();
        restrictCnicField();

    }

    private void restrictNameField() {
        nameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            // Allow alphabets (upper/lower case) and spaces
            if (!newValue.matches("[a-zA-Z ]*")) {
                nameTextField.setText(oldValue);
            }
        });
    }

    private void restrictCnicField() {
        cnicTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            // Allow digits and hyphen (-)
            if (!newValue.matches("[0-9-]*")) {
                cnicTextField.setText(oldValue);
            }
        });
    }


    @FXML
    private void handleSubmit() {
        boolean is_registered = ems.isVoterRegistered(nameTextField.getText(), cnicTextField.getText());
        boolean isValid = !nameTextField.getText().isEmpty() && !cnicTextField.getText().isEmpty();

        if (isValid && is_registered) {
            boolean hasVoted = ems.getVoterStatus(cnicTextField.getText(), stationId);
            if (!hasVoted) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxmlFiles/PollingPC/CastVote.fxml"));
                    AnchorPane castVotePane = loader.load();
                    CastVoteController controller = loader.getController();

                    if (controller != null) {
                        if (ems == null) {
                            System.out.println("EMS is null in capture voter info !");
                        } else {
                            System.out.println("EMS is not null in capture voter info !");
                        }
                        controller.setElectionManagementSystem(ems, cnicTextField.getText());
                    }

                    Stage secondaryStage = new Stage();
                    secondaryStage.setTitle("Cast Vote");
                    Scene castVoteScene = new Scene(castVotePane);
                    secondaryStage.setScene(castVoteScene);

                    Screen secondScreen = Screen.getScreens().size() > 1 ? Screen.getScreens().get(1) : Screen.getPrimary();
                    Rectangle2D bounds = secondScreen.getVisualBounds();
                    secondaryStage.setX(bounds.getMinX());
                    secondaryStage.setY(bounds.getMinY());
                    secondaryStage.setWidth(bounds.getWidth() - 20);
                    secondaryStage.setHeight(bounds.getHeight() - 20);

                    secondaryStage.show();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                // Voter has already cast the vote, show error popup
                showErrorPopup("You've already voted.");
            }
        } else {
            // Fields are empty or voter is not registered
            if (!is_registered) {
                showErrorPopup("You are not registered.");
            } else if (!isValid) {
                showErrorPopup("Please fill in all fields.");
            }
        }
    }

    private void showErrorPopup(String message) {
        try {
            // Load the FXML for the error popup
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxmlFiles/PopUps/ErrorMessage.fxml"));
            AnchorPane errorPopup = loader.load();
            ErrorMessageController errorMessageController = loader.getController();

            // Set the message in the popup
            errorMessageController.setMessageLabel(message);

            // Create a new stage for the popup
            Stage popupStage = new Stage();
            popupStage.setTitle("Error");
            Scene scene = new Scene(errorPopup);
            popupStage.setScene(scene);

            // Set the primary stage for closing if needed
            errorMessageController.setPrimaryStage(popupStage);

            popupStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


//    private void simulateFingerprintCapture() {
//        // Simulate fingerprint data capture
//        String fingerprintData = "sample_fingerprint_data"; // Replace with actual capture
//
//        try {
//            // Hash the fingerprint data
//            MessageDigest digest = MessageDigest.getInstance("SHA-256");
//            byte[] hash = digest.digest(fingerprintData.getBytes());
//            String hashString = Base64.getEncoder().encodeToString(hash);
//
//            System.out.println("Fingerprint Hash: " + hashString); // Print the hash
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
//    }
}
