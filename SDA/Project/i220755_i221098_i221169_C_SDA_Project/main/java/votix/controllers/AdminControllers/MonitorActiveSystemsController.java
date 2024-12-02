package votix.controllers.AdminControllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import votix.models.PollingStationPC;
import votix.services.AdminElectionManagementSystem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class MonitorActiveSystemsController {

    public AnchorPane contentPane;
    public ImageView backArrow;
    public ComboBox status;
    private Stage stage;

    public Button backbtn;

    @FXML
    private VBox systemTable; // VBox to dynamically add system rows
    private AdminElectionManagementSystem ems;
    private Thread sseListenerThread;


    public void initialize() {
        // Initialization
        backArrow.setCursor(Cursor.HAND);

        // Set ComboBox options
        status.getItems().addAll("All", "Active", "Inactive");
        status.setValue("All"); // Default selection

        // Add listener to handle ComboBox changes
        status.valueProperty().addListener((observable, oldValue, newValue) -> filterSystems((String) newValue));
        // Start listening for SSE events
        listenToSSE();
    }

    private void listenToSSE() {
        sseListenerThread = new Thread(() -> {
            try {
                URL url = new URL("http://100.91.228.86:8080/events"); // SSE URL
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.isEmpty()) {
                        String finalLine = line;
                        Platform.runLater(() -> {
                            System.out.println("Monitor SSE Event: " + finalLine);

                            if (finalLine.equalsIgnoreCase("closed")) {
                                System.out.println("closeddddddd");
                                populateSystemTable();
                            }
                            else {
                                System.out.println("Event did not match the condition: '" + finalLine + "'");
                            }
                        });
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        sseListenerThread.setDaemon(true);
        sseListenerThread.start();
    }

    public void setElectionManagementSystem(AdminElectionManagementSystem system, Stage st) {
        this.ems = system;
        this.stage = st;
        populateSystemTable();
    }

    private void populateSystemTable() {
        List<PollingStationPC> systems = ems.getPollingPCs();
        for (PollingStationPC system : systems) {
            addSystemRow(system);
        }
    }

    @FXML
    private void filterSystems(String newValue) {
        String filter = (String) status.getValue(); // Get the current filter value

        // Clear other filters if necessary
        // (If you have other filters like pollingStationFilterTextField or areaFilterTextField, you can clear them here.)

        // Filter systems based on the selected status (Active, Inactive, or All)
        List<PollingStationPC> filteredSystems = ems.getPollingPCs().stream()
                .filter(system -> switch (filter) {
                    case "Active" -> system.getSystemStatus();
                    case "Inactive" -> !system.getSystemStatus();
                    default -> true; // "All" option
                })
                .toList();

        // Keep headers and clear the system table content
        systemTable.getChildren().retainAll(systemTable.getChildren().get(0)); // Retain the header row only
        for (PollingStationPC system : filteredSystems) {
            addSystemRow(system);  // Add matching rows
        }
    }

    private void addSystemRow(PollingStationPC system) {
        // Create the main AnchorPane for the row
        AnchorPane row = new AnchorPane();
        row.getStyleClass().add("table-row");

        // System ID Label (Center-aligned)
        Label systemIdLabel = new Label(system.getSystemID());
        systemIdLabel.getStyleClass().add("table-cell");
        systemIdLabel.setStyle("-fx-alignment: center; -fx-font-size: 16px;");
        AnchorPane.setLeftAnchor(systemIdLabel, 20.0);   // Adjust for column position
        AnchorPane.setRightAnchor(systemIdLabel, 1100.0); // Symmetric for centering
        AnchorPane.setTopAnchor(systemIdLabel, 0.0);    // Center vertically
        AnchorPane.setBottomAnchor(systemIdLabel, 0.0); // Center vertically
        row.getChildren().add(systemIdLabel);

        // Station ID Label (Center-aligned)
        Label stationIdLabel = new Label(String.valueOf(system.getStationID()));
        stationIdLabel.getStyleClass().add("table-cell");
        stationIdLabel.setStyle("-fx-alignment: center; -fx-font-size: 16px;");
        AnchorPane.setLeftAnchor(stationIdLabel, 300.0);  // Adjust for column position
        AnchorPane.setRightAnchor(stationIdLabel, 850.0); // Symmetric for centering
        AnchorPane.setTopAnchor(stationIdLabel, 0.0);    // Center vertically
        AnchorPane.setBottomAnchor(stationIdLabel, 0.0); // Center vertically
        row.getChildren().add(stationIdLabel);

        // Status Label (Center-aligned)
        Label statusLabel = new Label(system.getSystemStatus() ? "Active" : "Inactive");
        statusLabel.getStyleClass().add("table-cell");
        statusLabel.getStyleClass().add(system.getSystemStatus() ? "voted" : "not-voted"); // Add dynamic style class
        statusLabel.setStyle("-fx-alignment: center; -fx-font-size: 16px;");
        AnchorPane.setLeftAnchor(statusLabel, 500.0);  // Adjust for column position
        AnchorPane.setRightAnchor(statusLabel, 560.0); // Symmetric for centering
        AnchorPane.setTopAnchor(statusLabel, 0.0);    // Center vertically
        AnchorPane.setBottomAnchor(statusLabel, 0.0); // Center vertically
        row.getChildren().add(statusLabel);


        // Config Label (Center-aligned)
        Label configLabel = new Label(system.getConfigurationSettings());
        configLabel.getStyleClass().add("table-cell");
        configLabel.setStyle("-fx-alignment: center; -fx-font-size: 16px;");
        AnchorPane.setLeftAnchor(configLabel, 700.0);  // Adjust for column position
        AnchorPane.setRightAnchor(configLabel, 250.0); // Symmetric for centering
        AnchorPane.setTopAnchor(configLabel, 0.0);    // Center vertically
        AnchorPane.setBottomAnchor(configLabel, 0.0); // Center vertically
        row.getChildren().add(configLabel);

        // Area Name Label (Center-aligned)
        Label areaNameLabel = new Label(system.getAreaName());
        areaNameLabel.getStyleClass().add("table-cell");
        areaNameLabel.setStyle("-fx-alignment: center; -fx-font-size: 16px;");
        AnchorPane.setLeftAnchor(areaNameLabel, 1050.0);  // Adjust for column position
        AnchorPane.setRightAnchor(areaNameLabel, 30.0);  // Symmetric for centering
        AnchorPane.setTopAnchor(areaNameLabel, 0.0);     // Center vertically
        AnchorPane.setBottomAnchor(areaNameLabel, 0.0);  // Center vertically
        row.getChildren().add(areaNameLabel);

        // Add the row to the system table
        systemTable.getChildren().add(row);
    }

    public void returnToMenu(MouseEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxmlFiles/AdminControlled/AdminMenu.fxml"));
            AnchorPane addCandidatePane = loader.load();
            AdminMenuController controller = loader.getController();

            // Check if controller is not null and set EMS and primaryStage
            if (controller != null) {
                System.out.println("setting admin");
                controller.setElectionManagementSystem(this.ems);  // Pass the ems instance
                controller.setPrimaryStage(this.stage);      // Pass the primaryStage instance
            }

            // Update contentPane
            contentPane.getChildren().setAll(addCandidatePane);
            contentPane.requestLayout();  // Request a layout refresh

            System.out.println(contentPane);
            System.out.println("contentPane visible: " + contentPane.isVisible());
            System.out.println("contentPane parent: " + contentPane.getParent());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
