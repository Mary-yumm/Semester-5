package votix.controllers.AdminControllers;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import votix.models.ElectionResult;
import votix.services.AdminElectionManagementSystem;
import votix.services.PersistenceHandler;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

public class ElectionFormController {

    @FXML
    private Button GenerateForm;
    @FXML
    private Button searchArea;

    @FXML
    private TextField areaField;

    @FXML
    private Label castedvotes;

    @FXML
    private AnchorPane contentPane;

    @FXML
    private Label electionDate;

    @FXML
    private ChoiceBox<String> napa;

    @FXML
    private Label regvotes;

    @FXML
    private TableColumn<Integer, Integer> stationIdColumn1;

    @FXML
    private TableView<Integer> stationTable1;

    @FXML
    private Label turnout;

    @FXML
    public ImageView backArrow;

    @FXML
    private TableView<ElectionResult> formTable;

    @FXML
    private TableColumn<ElectionResult, String> cand;

    @FXML
    private TableColumn<ElectionResult, String> party;

    @FXML
    private TableColumn<ElectionResult, Integer> vote;


    private PersistenceHandler ph;
    private Stage PrimaryStage;
    private AdminElectionManagementSystem ems;

    @FXML
    void handleGenerateForm(MouseEvent event) {
        // Retrieve the selected station ID from the table
        Integer selectedStationId = stationTable1.getSelectionModel().getSelectedItem();

        if (selectedStationId != null) {
            // Get the selected assembly type (Napa)
            String Napa = (napa.getValue() != null) ? napa.getValue() : "Default Value";
            System.out.println("Napa ChoiceBox: " + napa.getValue());
            if (Napa.equals("National Assembly"))
                Napa = "NA";
            else if (Napa.equals("Provincial Assembly"))
                Napa = "PA";

            // Fetch the area name entered in the search field
            String searchArea = areaField.getText();

            // Use the selected station ID and other parameters to generate the form
            ArrayList<ElectionResult> formdata = ems.getForm(selectedStationId, searchArea, Napa);

            if (formdata != null && !formdata.isEmpty()) {
                System.out.println("Search by station ID successful");
                loadForm(formdata);
            } else {
                System.out.println("No data found for the selected station ID");
            }
        } else {
            System.out.println("No station selected. Please select a row from the table.");
        }
    }



    // Add null checks and error handling
    public void loadForm(ArrayList<ElectionResult> formdata) {

        if (!formdata.isEmpty()) {
            // Clear the table or show a message
            formTable.getItems().clear();
        }
        // Example: Set the current date during initialization
        displayDate(LocalDate.now());
        ObservableList<ElectionResult> electionList = FXCollections.observableArrayList(formdata);
        formTable.setItems(electionList);

        // Ensure these match the model class exactly
        cand.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getCandidateName()));
        party.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getPartyName()));
        vote.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getVoteCount()));

        int castedvotes = 0;
        for (ElectionResult result : electionList) {
            castedvotes += result.getVoteCount();
        }

        loadSummary(castedvotes);
    }

    private void loadStation(ArrayList<Integer> stations) {
        ObservableList<Integer> stationList = FXCollections.observableArrayList(stations);
        stationTable1.setItems(stationList);
        stationIdColumn1.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue()));
    }
    public void returnToMenu(MouseEvent actionEvent) {
        try {
            System.out.println("Returning to the Admin Menu...");

            // Load the FXML file for the Admin Menu
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxmlFiles/AdminControlled/AdminMenu.fxml"));
            AnchorPane adminMenuPane = loader.load();

            // Get the controller for the Admin Menu
            AdminMenuController controller = loader.getController();

            if (controller != null) {
                System.out.println("Admin Menu Controller set.");
                // Pass required data to the Admin Menu controller
                controller.setElectionManagementSystem(this.ems);
                controller.setPrimaryStage(this.PrimaryStage);
            }

            // Set the new pane as the scene's root
            PrimaryStage.getScene().setRoot(adminMenuPane);
        } catch (IOException e) {
            System.out.println("Error loading Admin Menu.");
            e.printStackTrace();
        }
    }
    public void displayDate(LocalDate date) {
        electionDate.setText(date.toString()); // Display the date
    }

    @FXML
    void handleSearchArea(MouseEvent event) {
        // Using AreaName as area search field
        String searchArea = areaField.getText();
        if (!searchArea.isEmpty()) {
            ArrayList<Integer> st = ems.getStations(searchArea);
            System.out.println("Search by area successful");
            loadStation(st);

        } else {
            System.out.println("no area");
        }
    }

    private void clearData() {
        // Clear input fields
        areaField.clear();

        // Clear table data
        formTable.getItems().clear();
        stationTable1.getItems().clear();

        // Reset labels for election summary
        electionDate.setText("");
        regvotes.setText("0");
        castedvotes.setText("0");
        turnout.setText("0%");
    }


    private void loadSummary(int totalCastedVotes) {
        // Update total votes and voter turnout
        int TotalRegVoters = (int) (totalCastedVotes * 1.5);
        this.regvotes.setText(String.valueOf(TotalRegVoters)); // Display total votes directly

        this.castedvotes.setText(String.valueOf(totalCastedVotes));

        // Calculate and display voter turnout percentage
        int voterPercentage = (totalCastedVotes * 100) / TotalRegVoters; // Avoid floating-point arithmetic
        this.turnout.setText(voterPercentage + "%");
    }




    public void initialize() {
        backArrow.setCursor(Cursor.HAND);
        // Initialize the choice box for National/Provincial
        ObservableList<String> np = FXCollections.observableArrayList(
                "National Assembly", "Provincial Assembly");
        napa.setItems(np);
        napa.setValue("National Assembly");

        // In the loadForm method, make sure to use the correct column name
        vote.setCellValueFactory(new PropertyValueFactory<>("vote"));

    }
    public PersistenceHandler getPh() {
        return ph;
    }

    public void setPh(PersistenceHandler ph) {
        this.ph = ph;
    }

    public Stage getPrimaryStage() {
        return PrimaryStage;
    }

    public void setPrimaryStage(Stage primaryStage) {
        PrimaryStage = primaryStage;
    }

    public AdminElectionManagementSystem getEms() {
        return ems;
    }

    public void setEms(AdminElectionManagementSystem AEMS) {
        this.ems = AEMS;
    }

    public void set(AdminElectionManagementSystem ems, Stage primaryStage) {
        setEms(ems);
        setPrimaryStage(primaryStage);
    }

}
