package votix.controllers.AdminControllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import votix.DEMO;
import votix.controllers.PopUps.*;
import votix.models.Candidate;
import votix.services.AdminElectionManagementSystem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class addCandidateController {


    public Button backbtn;
    public ImageView backArrow;
    private AdminElectionManagementSystem ems;
    private Stage stage;
    @FXML
    private AnchorPane contentPane;

    @FXML
    private TextField cid;

    @FXML
    private TextField cname;

    @FXML
    private TextField age;

    @FXML
    private TextField cnic;

    @FXML
    private ComboBox<String> area;

    @FXML
    private ComboBox<String> nationality;

    @FXML
    private ComboBox<String> napa;

    @FXML
    private ComboBox<String> politicalparty;

    @FXML
    private Button registerButton;

    public void initialize() {
        backArrow.setCursor(Cursor.HAND);
    }



    public void setElectionManagementSystem(AdminElectionManagementSystem electionManagementSystem, Stage st) {
        if(electionManagementSystem == null){
            System.out.println("Receieved ems is null");
        }
        else{
            System.out.println("Receieved ems is not null");
        }
        this.ems = electionManagementSystem;
        if(ems!=null) {
            System.out.println("ems set in addcandidate: " + (ems != null));  // Debugging line
        }
        else{
            System.out.println("ems set in addcandidate: null");
        }
        initializingMethod(); //after connection to ems, load data onto comboboxes
        this.stage =st;
        nationality.setValue(null);  // This clears the default selection

    }

    public void AddNewCandidate() throws IOException {

        boolean check = checkForEmptyFields();
        boolean datatypeCheck = checkForIncorrectDataType();
        if(check) {
            if(datatypeCheck) {
                if(!isIdInUse()) {
                    System.out.println("out1");
                    boolean st = (Integer.parseInt(age.getText()) >= 25) && nationality.getValue().equals("Pakistani");
                    if (st) {
                        System.out.println("out2");

                        Candidate cand = new Candidate(Integer.parseInt(cid.getText()), cname.getText(), politicalparty.getValue(), st, napa.getValue());
                        //this ftn is to take values from the interface and pass them to the ph
                        System.out.println("out3");

                        boolean addstatus = ems.addCandidate(cand, area.getValue());
                        if (addstatus) {
                            //added successfully
                            showPopUPAddedCand();
                        }
                    } else {
                        System.out.println("Candidate is not eligible");
                        showErrorMessage("Candidate is not eligible");
                    }
                }else{
                    showErrorMessage("Duplicate ID");
                }
            }else{
                //incorrect data types
                showErrorMessage("Incorrect Datatype");
            }
        }
        else{
            System.out.println("Empty data fields");
            showErrorMessage("Empty Fields!");
        }
    }

    void initializingMethod(){
        System.out.println("Initializinggggg");
        loadParty_AreaName();
        ObservableList<String> national = FXCollections.observableArrayList(
                "Pakistani", "Other", "Dual");
        nationality.setItems(national);

        ObservableList<String> np = FXCollections.observableArrayList(
                "National Assembly", "Provincial Assembly");
        napa.setItems(np);
        this.contentPane= new AnchorPane();
    }

    void loadParty_AreaName(){
        System.out.println("Loadinggggg");
        ArrayList<String> party = ems.getPartyNames();
        ObservableList<String> np = FXCollections.observableArrayList(party);
        politicalparty.setItems(np);

        ArrayList<String> ar = ems.getAreaID();
        ObservableList<String> array = FXCollections.observableArrayList(ar);
        area.setItems(array);

        System.out.println("Loading completedd");

    }

    boolean checkForEmptyFields() {
        if (cid.getText().isEmpty() || cname.getText().isEmpty() || age.getText().isEmpty() || cnic.getText().isEmpty()) {
            return false;
        }

        if (area.getValue() == null || nationality.getValue() == null || napa.getValue() == null || politicalparty.getValue() == null) {
            return false;
        }

        return true;
    }
    boolean checkForIncorrectDataType(){
        if (!cname.getText().matches("[a-zA-Z ]+")) {
            System.out.println("Invalid name. Only letters and spaces are allowed.");
            return false;
        }

        if (!cid.getText().matches("\\d+")) {
            System.out.println("Invalid ID. It should be an integer.");
            return false;
        }

        if (!age.getText().matches("\\d+")) {
            System.out.println("Invalid age. It should be an integer.");
            return false;
        }

        if (!cnic.getText().matches("\\d{5}-\\d{7}-\\d")) {
            System.out.println("Invalid CNIC. It should follow the format xxxxx-xxxxxxx-x.");
            return false;
        }
        return true;
    }

    void showPopUPAddedCand() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(DEMO.class.getResource("/fxmlFiles/PopUps/NewCandidateAdded.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 460, 230);
        Stage stage = new Stage();
        stage.setTitle("PopUp");

        // After loading the FXML, get the controller and set the ElectionManagementSystem
        NewCandidateController controller = fxmlLoader.getController();
        // Set the primary stage in the controller
        controller.setPrimaryStage(stage);
        stage.setScene(scene);
        stage.show();
    }



    void showErrorMessage(String msg) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(DEMO.class.getResource("/fxmlFiles/PopUps/ErrorMessage.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 404, 180);
        Stage stage = new Stage();
        stage.setTitle("PopUp");

        // After loading the FXML, get the controller and set the ElectionManagementSystem
        ErrorMessageController controller = fxmlLoader.getController();
        // Set the primary stage in the controller
        controller.setMessageLabel(msg);
        controller.setPrimaryStage(stage);
        stage.setScene(scene);
        stage.show();
    }

    boolean isIdInUse() {
        List<Candidate> candidates = ems.getAllCand();

        for (Candidate cand : candidates) {
            if (cand.getCid()== Integer.parseInt(cid.getText())) {
                System.out.println("Duplicate ID found: " + cand.getCid());
                return true;
            }
        }
        System.out.println("Duplicate ID not found ");
        return false;
    }


    public void returnToMenu(MouseEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxmlFiles/AdminControlled/AdminMenu.fxml"));
            AnchorPane addCandidatePane = loader.load();
            AdminMenuController controller = loader.getController();

            if (controller != null) {
                controller.setElectionManagementSystem(this.ems);
                controller.setPrimaryStage(this.stage);
            }
            if (this.stage.getScene() == null) {
                Scene scene = new Scene(addCandidatePane);
                this.stage.setScene(scene);
            } else {
                this.stage.getScene().setRoot(addCandidatePane);
            }

            this.stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
