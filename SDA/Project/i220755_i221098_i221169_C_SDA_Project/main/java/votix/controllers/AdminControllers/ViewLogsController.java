package votix.controllers.AdminControllers;


import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;
import votix.models.Log;
import votix.services.AdminElectionManagementSystem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class ViewLogsController {

    public AnchorPane contentPane;
    public ImageView backArrow;
    private Stage stage;

    public Button pollingStation;
    public Button Area;
    @FXML
    private VBox logTable; // Reference to the VBox in the FXML
    @FXML
    private TextField filterTextField;
    @FXML
    private TextField areaFilterTextField; // Filter by area ID
    @FXML
    private TextField pollingStationFilterTextField; // Filter by polling station ID


    private AdminElectionManagementSystem ems; // Reference to the EMS object

    private Thread sseListenerThread;

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
                            System.out.println("New SSE Event: " + finalLine);

                            // Optionally, re-populate logs or handle event
                            populateLogTable();
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

    @FXML
    public void initialize() {
        setupTextFieldValidator(pollingStationFilterTextField);
        setupTextFieldValidator(areaFilterTextField);
        setupFocusListeners();
        backArrow.setCursor(Cursor.HAND);
        // Start listening for SSE updates
        listenToSSE();
    }

    // Method to set up focus listeners for text fields
    private void setupFocusListeners() {
        filterTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) { // If filterTextField gains focus
                areaFilterTextField.clear();
                pollingStationFilterTextField.clear();
            }
        });

        areaFilterTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) { // If areaFilterTextField gains focus
                filterTextField.clear();
                pollingStationFilterTextField.clear();

            }
        });

        pollingStationFilterTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) { // If pollingStationFilterTextField gains focus
                filterTextField.clear();
                areaFilterTextField.clear();
            }
        });
    }

    // Method to apply number-only input restriction
    private void setupTextFieldValidator(TextField textField) {
        TextFormatter<Integer> numberFormatter = new TextFormatter<>(
                new IntegerStringConverter(),  // Use IntegerStringConverter to only allow numbers
                0,                             // Default value (optional)
                change -> {
                    // Allow the change only if the new text is a valid number
                    return change.getControlNewText().matches("\\d*") ? change : null;
                }
        );
        textField.setTextFormatter(numberFormatter);
    }


    // Set the EMS and populate logs
    public void setEMS(AdminElectionManagementSystem ems, Stage st) {
        this.ems = ems;
        this.stage = st;
        if (ems != null) {
            System.out.println("EMS set in ViewLogsController");
            populateLogTable(); // Populate logs immediately after EMS is set


        }
    }

    // Populate the log table with logs from the EMS
    private void populateLogTable() {
        List<Log> logs = ems.viewLogs(); // Retrieve logs using the EMS method
        if (logs.isEmpty()) {
            System.out.println("No logs available to display.");
            return;
        }

        // Sort logs by timestamp in descending order
        logs.sort((log1, log2) -> log2.getTimeStamp().compareTo(log1.getTimeStamp()));

        // Retain only the header row and clear other rows
        logTable.getChildren().retainAll(logTable.getChildren().get(0)); // Retain the first child (header)

        // Add each log as a new row to the log table
        for (Log log : logs) {
            addLogRow(log, null, null); // Add each log as a row to the log table without highlighting
        }
    }


    private void addLogRow(Log log, String highlightText, String filterType) {
        // Create the main AnchorPane for the row
        AnchorPane row = new AnchorPane();
        row.getStyleClass().add("table-row");

        // Log ID Label (Center-aligned)
        Label logIdLabel = new Label(String.valueOf(log.getLogId()));
        logIdLabel.getStyleClass().add("table-cell");
        logIdLabel.setStyle("-fx-alignment: center; -fx-font-size: 16px;");
        AnchorPane.setLeftAnchor(logIdLabel, 0.0);   // Adjust for column position
        AnchorPane.setRightAnchor(logIdLabel, 1130.0); // Make it symmetric for centering
        AnchorPane.setTopAnchor(logIdLabel, 0.0);    // Center vertically
        AnchorPane.setBottomAnchor(logIdLabel, 0.0); // Center vertically
        row.getChildren().add(logIdLabel);

        // Action Label (Center-aligned)
        Label actionLabel = new Label();
        actionLabel.getStyleClass().add("table-cell");
        actionLabel.setStyle("-fx-alignment: center; -fx-font-size: 16px;");
        AnchorPane.setLeftAnchor(actionLabel, 200.0);  // Adjust for column position
        AnchorPane.setRightAnchor(actionLabel, 300.0); // Make it symmetric for centering
        AnchorPane.setTopAnchor(actionLabel, 0.0);    // Center vertically
        AnchorPane.setBottomAnchor(actionLabel, 0.0); // Center vertically

        // Handle Highlighting Logic
        if (highlightText != null && !highlightText.isEmpty()) {
            switch (filterType.toLowerCase()) {
                case "action":
                    actionLabel.setGraphic(createHighlightedText(log.getAction(), highlightText));
                    break;
                case "pollingstation":
                    if (log.getAction().toLowerCase().contains("polling station " + highlightText)) {
                        actionLabel.setGraphic(createHighlightedText(log.getAction(), "Polling Station " + highlightText));
                    } else {
                        actionLabel.setText(log.getAction());
                    }
                    break;
                case "area":
                    if (log.getAction().toLowerCase().contains("area " + highlightText)) {
                        actionLabel.setGraphic(createHighlightedText(log.getAction(), "Area " + highlightText));
                    } else {
                        actionLabel.setText(log.getAction());
                    }
                    break;
                default:
                    actionLabel.setText(log.getAction());
            }
        } else {
            actionLabel.setText(log.getAction());
        }
        row.getChildren().add(actionLabel);

        // Timestamp Label (Center-aligned)
        Label timestampLabel = new Label(log.getTimeStamp());
        timestampLabel.getStyleClass().add("table-cell");
        timestampLabel.setStyle("-fx-alignment: center; -fx-font-size: 16px;");
        AnchorPane.setLeftAnchor(timestampLabel, 970.0);  // Adjust for column position
        AnchorPane.setRightAnchor(timestampLabel, 50.0);  // Make it symmetric for centering
        AnchorPane.setTopAnchor(timestampLabel, 0.0);    // Center vertically
        AnchorPane.setBottomAnchor(timestampLabel, 0.0); // Center vertically
        row.getChildren().add(timestampLabel);

        // Add the row to the log table
        logTable.getChildren().add(row);
    }

    // Helper method to create a label with highlighted text
    private HBox createHighlightedText(String fullText, String highlight) {
        String lowerFullText = fullText.toLowerCase();
        String lowerHighlight = highlight.toLowerCase();

        int startIndex = lowerFullText.indexOf(lowerHighlight);
        if (startIndex == -1) return new HBox(new Label(fullText)); // No match, return plain label

        int endIndex = startIndex + highlight.length();

        String before = fullText.substring(0, startIndex);
        String highlighted = fullText.substring(startIndex, endIndex);
        String after = fullText.substring(endIndex);

        Label beforeLabel = new Label(before);
        Label highlightLabel = new Label(highlighted);
        Label afterLabel = new Label(after);

        String commonStyle = "-fx-font-size: 16px;"; // Ensure consistent font size
        beforeLabel.setStyle(commonStyle);
        highlightLabel.setStyle(commonStyle + " -fx-background-color: yellow; -fx-font-weight: bold");
        afterLabel.setStyle(commonStyle);

        return new HBox(beforeLabel, highlightLabel, afterLabel);
    }




    public void applyFiltersPollingStation() {
        String pollingStationFilterText = pollingStationFilterTextField.getText().toLowerCase();

        // Clear other text fields
        areaFilterTextField.clear();
        filterTextField.clear();

        // Filter logs based on exact polling station ID
        List<Log> filteredLogs = ems.viewLogs().stream()
                .filter(log -> pollingStationFilterText.isEmpty() ||
                        log.getAction().toLowerCase().matches(".*\\bpolling station " + pollingStationFilterText + "\\b.*"))
                .toList();

        // Keep headers and clear the log table content
        logTable.getChildren().retainAll(logTable.getChildren().get(0)); // Retain the header row only
        for (Log log : filteredLogs) {
            addLogRow(log, pollingStationFilterText, "pollingStation"); // Highlight Polling Station
        }
    }



    public void applyFiltersArea() {
        String areaFilterText = areaFilterTextField.getText().toLowerCase();

        // Clear other text fields
        pollingStationFilterTextField.clear();
        filterTextField.clear();

        // Filter logs based on exact area ID
        List<Log> filteredLogs = ems.viewLogs().stream()
                .filter(log -> areaFilterText.isEmpty() ||
                        log.getAction().toLowerCase().matches(".*\\barea " + areaFilterText + "\\b.*"))
                .toList();

        // Keep headers and clear the log table content
        logTable.getChildren().retainAll(logTable.getChildren().get(0)); // Retain the header row only
        for (Log log : filteredLogs) {
            addLogRow(log, areaFilterText, "area"); // Highlight Area
        }
    }



    @FXML
    private void filterLogs() {
        String filterText = filterTextField.getText().toLowerCase(); // Filter by action text

        // Clear other text fields
        pollingStationFilterTextField.clear();
        areaFilterTextField.clear();

        // Filter logs based on the action
        List<Log> filteredLogs = ems.viewLogs().stream()
                .filter(log -> filterText.isEmpty() || log.getAction().toLowerCase().contains(filterText))
                .toList();

        // Keep headers and clear the log table content
        logTable.getChildren().retainAll(logTable.getChildren().get(0)); // Retain the header row only
        for (Log log : filteredLogs) {
            addLogRow(log,filterText,"action");
        }
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

            // Optionally reset the scene if necessary
            Scene currentScene = this.stage.getScene();

            System.out.println(contentPane);
            System.out.println("contentPane visible: " + contentPane.isVisible());
            System.out.println("contentPane parent: " + contentPane.getParent());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void removeFilter(ActionEvent actionEvent) {
        // Clear all filters
        filterTextField.clear();
        areaFilterTextField.clear();
        pollingStationFilterTextField.clear();

        // Retrieve logs and sort them in descending order by timestamp
        List<Log> logs = ems.viewLogs();
        logs.sort((log1, log2) -> log2.getTimeStamp().compareTo(log1.getTimeStamp()));

        // Clear the log table content while retaining the header row
        logTable.getChildren().retainAll(logTable.getChildren().get(0));

        // Populate the table with sorted logs
        for (Log log : logs) {
            addLogRow(log, null, null);
        }
    }


}
