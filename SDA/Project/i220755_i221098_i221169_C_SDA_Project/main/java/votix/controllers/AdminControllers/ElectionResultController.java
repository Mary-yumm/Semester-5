package votix.controllers.AdminControllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import votix.models.ElectionResult;
import votix.services.AdminElectionManagementSystem;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

public class ElectionResultController {

    @FXML
    private Button AreaSearch;
    @FXML
    private TextField CandName;
    @FXML
    private TextField AreaName;
    @FXML
    private Button CandSearch;
    @FXML
    private TextField PartyName;
    @FXML
    private Button PartySearch;
    @FXML
    private TableView<ElectionResult> Table;
    @FXML
    private TableColumn<ElectionResult, String> areacol;
    @FXML
    private TableColumn<ElectionResult, String> candcol;
    @FXML
    private TableColumn<ElectionResult, String> partycol;
    @FXML
    private TableColumn<ElectionResult, Number> countcol;

    @FXML
    private Label datee;
    @FXML
    private ChoiceBox<String> napa;

    @FXML
    public ImageView backArrow;


    private AdminElectionManagementSystem ems;
    private Stage Primarystage;

    public void set(AdminElectionManagementSystem ems, Stage ps) {
        setEms(ems);
        setPrimarystage(ps);
        loadTableData(); // Call loadTableData after setting ems
    }



    @FXML
    void handleAreaSearch(MouseEvent event) {
        String searchArea = AreaName.getText(); // Using AreaName as area search field
        if (!searchArea.isEmpty()) {
            ArrayList<ElectionResult> results = ems.searchElectionResultsByArea(searchArea);
            System.out.println("Table Updated by area");
            updateTableData(results);
        } else {
            // If search field is empty, show all results
            System.out.println("Table no update by area");

            loadTableData();
        }
    }

    @FXML
    void handleCandSearch(MouseEvent event) {
        String searchCand = CandName.getText(); // Using CandName as area search field
        if (!searchCand.isEmpty()) {
            ArrayList<ElectionResult> results = ems.searchElectionResultsByCandidate(searchCand);
            System.out.println("Table Updated by cand");

            updateTableData(results);
        } else {
            // If search field is empty, show all results
            System.out.println("Table no update by cand");
            loadTableData();
        }
    }

    @FXML
    void handlePartySearch(MouseEvent event) {
        String searchParty = PartyName.getText().trim();
        if (!searchParty.isEmpty()) {
            ArrayList<ElectionResult> results = ems.searchElectionResultsByParty(searchParty);
            System.out.println("Table Updated by party");

            updateTableData(results);
        } else {
            System.out.println("Table no update by party");

            loadTableData();
        }
    }
    private void updateTableData(ArrayList<ElectionResult> results) {
        ObservableList<ElectionResult> electionList = FXCollections.observableArrayList(results);
        Table.setItems(electionList);
    }

    @FXML
    void hanlderdropdown(MouseEvent event) {
        loadTableData();

    }
    // Add this method to clear searches
    private void clearSearches() {
        AreaName.clear();  // Area search
        CandName.clear();   // Candidate search
        PartyName.clear();  // Party search
        loadTableData();    // Reset to show all data
    }

    @FXML
    public void initialize() {
        // Initialize the choice box for National/Provincial
        ObservableList<String> np = FXCollections.observableArrayList(
                "NA", "PA");
        napa.setItems(np);
        napa.setValue("NA");

        backArrow.setCursor(Cursor.HAND);
        // Initialize the table columns
        areacol.setCellValueFactory(new PropertyValueFactory<>("areaName"));
        candcol.setCellValueFactory(new PropertyValueFactory<>("candidateName"));
        partycol.setCellValueFactory(new PropertyValueFactory<>("partyName"));
        countcol.setCellValueFactory(new PropertyValueFactory<>("voteCount"));

        // Add text change listeners for real-time search
        AreaName.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.trim().isEmpty()) {
                loadTableData();
            }
        });

        CandName.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.trim().isEmpty()) {
                loadTableData();
            }
        });

        PartyName.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.trim().isEmpty()) {
                loadTableData();
            }
        });



    }
    // Add a method to handle combined search
    /*private void handleCombinedSearch() {
        String area = AreaName.getText().trim();
        String candidate = CandName.getText().trim();
        String party = PartyName.getText().trim();
        String type = napa.getValue().toString();

        // You'll need to implement this in your persistence layer
        ArrayList<ElectionResult> results = ems.searchElectionResultsCombined(area, candidate, party, type);
        updateTableData(results);
    }*/
    private void loadTableData() {

        String Napa = (napa.getValue() != null) ? napa.getValue() : "Default Value";
        System.out.println("Napa ChoiceBox: " + napa.getValue());
        if (Napa.equals("NA"))
            Napa = "NA";
        else if (Napa.equals("PA"))
            Napa = "PA";

        if (ems == null) {
            System.out.println("EMS is null!");
            return;
        }

        try {
            ArrayList<ElectionResult> results = ems.fetchElectionResults(Napa);
            if (results == null) {
                System.out.println("No results returned!");
                return;
            }
            // Example: Set the current date during initialization
            displayDate(LocalDate.now());
            ObservableList<ElectionResult> electionList = FXCollections.observableArrayList(results);
            Table.setItems(electionList);

            // Print for debugging
            System.out.println("Loaded " + electionList.size() + " results");
            //for (ElectionResult result : electionList) {
                //System.out.println(result);
            //}
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                controller.setPrimaryStage(this.Primarystage);
            }

            // Set the new pane as the scene's root
            Primarystage.getScene().setRoot(adminMenuPane);
        } catch (IOException e) {
            System.out.println("Error loading Admin Menu.");
            e.printStackTrace();
        }
    }


    public void displayDate(LocalDate date) {
        datee.setText(date.toString()); // Display the date
    }

    public AdminElectionManagementSystem getEms() {
        return ems;
    }

    public void setEms(AdminElectionManagementSystem ems) {
        this.ems = ems;
    }
    public Stage getPrimarystage() {
        return Primarystage;
    }

    public void setPrimarystage(Stage primarystage) {
        this.Primarystage = primarystage;
    }
}
