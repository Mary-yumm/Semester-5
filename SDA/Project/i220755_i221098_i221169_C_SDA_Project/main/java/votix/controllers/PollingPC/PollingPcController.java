package votix.controllers.PollingPC;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import votix.services.PollingPCElectionManagementSystem;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class PollingPcController {
    @FXML
    private AnchorPane contentPane; // The pane where content will be dynamically loaded

    @FXML
    private Button tab1Button; // Capture Voter Info Button
//    @FXML
//    private Button tab2Button; // Cast Vote Button
    @FXML
    private Button tab3Button; // Polling Station Info Button

    private PollingPCElectionManagementSystem ems; // Your Election Management System instance
    private Stage primaryStage;

    // Setter to receive ElectionManagementSystem from the MainApp
    public void setElectionManagementSystem(PollingPCElectionManagementSystem electionManagementSystem) {
        this.ems = electionManagementSystem;
        ems.createLogEntry("System " + ems.getSystemID() + " initialized");
        if(ems!=null) {
            System.out.println("ems set in PollingPcController: " + (ems != null));  // Debugging line
            loadInitiateScreen(); // Load the initiate screen by default
        }
        else{
            System.out.println("ems set in PollingPcController: null");
        }
    }
    private void notifyPollingPcClosed() {
        try {
            URL url = new URL("http://100.91.228.86:8080/notify"); // Change to your actual endpoint
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            //String message = "Polling PC closed for station ID: " + ems.getStationId();
            String message = "closed";
            try (OutputStream os = connection.getOutputStream()) {
                os.write(("message=" + message).getBytes());
                os.flush();
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("Polling PC close event successfully notified.");
            } else {
                System.out.println("Failed to notify polling PC close event. Response code: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void setPrimaryStage(Stage primaryStage) {
        System.out.println("in pc primary stage");

        this.primaryStage = primaryStage;
        // Add a close event listener
        primaryStage.setOnCloseRequest(event -> {
            ems.createLogEntry("Polling PC " + ems.getSystemID() + " was closed at station " + ems.getStationId() + " in area: " + ems.getAreaId() + " "+ems.getAreaName());
            ems.setSystemInactive(ems.getSystemID());
            notifyPollingPcClosed();
        });
    }

    @FXML
    private void initialize() {

    }
    // Load the Initiate System screen
    private void loadInitiateScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxmlFiles/PollingPC/InitiateSystem.fxml"));
            AnchorPane initiatePane = loader.load();

            // Get the controller and set the callback for when the countdown completes
            InitiateSystemController controller = loader.getController();
            if (controller != null) {
                System.out.println("Setting InitiateSystemController...");
                controller.setOnCountdownComplete(this::loadCaptureVoterInfo); // Switch to Capture Voter Info after countdown
            } else {
                System.out.println("InitiateSystemController is null!");
            }

            // Load the Initiate Screen into the content pane
            contentPane.getChildren().setAll(initiatePane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Change content based on button clicked
    public void selectTab1() {
        loadCaptureVoterInfo();
        setActiveButton(tab1Button); // Set the active tab to Tab 1

    }

//    public void selectTab2() {
//        loadCastVote();
//        setActiveButton(tab2Button); // Set the active tab to Tab 2
//
//    }

    public void selectTab3() {
        loadPollingStationInfo();
        setActiveButton(tab3Button); // Set the active tab to Tab 3

    }

    private void loadCaptureVoterInfo() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxmlFiles/PollingPC/CaptureVoterInfo.fxml"));
            AnchorPane captureVoterInfoPane = loader.load();
            CaptureVoterInfoController controller = loader.getController();

            // Check if controller is not null and set EMS
            if (controller != null) {
                System.out.println("setting");
                controller.setElectionManagementSystem(this.ems,ems.getStationId());  // Pass the ems instance
                controller.initialize();
            } else {
                System.out.println("CaptureVoterInfoController is null!");  // Debugging line
            }

            contentPane.getChildren().setAll(captureVoterInfoPane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadPollingStationInfo() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxmlFiles/PollingPC/PollingStationInfo.fxml"));
            AnchorPane pollingStationInfo = loader.load();
            PollingStationInfoController controller = loader.getController();
            if (controller != null) {
                System.out.println("setting");
                controller.setPollingStation(ems.getStation(ems.getStationId()));
            }
            else {
                System.out.println("PollingStationInfoController is null!");  // Debugging line
            }
            contentPane.getChildren().setAll(pollingStationInfo); // Replace current content with Polling Station Info
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to set the active button
    private void setActiveButton(Button activeButton) {
        // Remove the 'selected' class from all buttons
        tab1Button.getStyleClass().remove("selected");
        //tab2Button.getStyleClass().remove("selected");
        tab3Button.getStyleClass().remove("selected");

        // Add the 'selected' class to the clicked button
        activeButton.getStyleClass().add("selected");
    }
}
