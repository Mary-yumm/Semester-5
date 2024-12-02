package votix.controllers.AdminControllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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
import votix.controllers.PopUps.ErrorMessageController;
import votix.controllers.PopUps.NewStaffController;
import votix.models.PollingStaff;
import votix.services.AdminElectionManagementSystem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class addPollingStaffController {
    public AnchorPane contentPane;
    public TextField staffid;
    public TextField staffname;
    public ComboBox<Integer> stationid;
    public TextField username;
    public TextField password;
    public ComboBox<String> assignedrole;
    public Button registerButton;
    public ImageView backArrow;
    private AdminElectionManagementSystem ems;
    private Stage primaryStage;

    public void setElectionManagementSystem(AdminElectionManagementSystem ems, Stage stage){
        this.ems = ems;
        this.primaryStage = stage;
        initializeSystem();
    }
    public void initialize() {
        backArrow.setCursor(Cursor.HAND);
    }

    void initializeSystem(){
        ObservableList<String> role = FXCollections.observableArrayList(
                "Polling Officer", "Presiding Officer", "Assistant Presiding Officer");
        assignedrole.setItems(role);
        loadStationIds();
    }

    void loadStationIds(){
        ArrayList<Integer> list = ems.getStations();
        ObservableList<Integer> np = FXCollections.observableArrayList(list);
        stationid.setItems(np);
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
                controller.setPrimaryStage(this.primaryStage);      // Pass the primaryStage instance
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

    public void AddNewStaff(ActionEvent actionEvent) throws IOException {
        System.out.println("inside ftn");

        boolean check = checkForEmptyFields();
        boolean datatypeCheck = checkForIncorrectDataType();
        if(check) {
            if (datatypeCheck) {
                if (!isInUse()) {
                    System.out.println("out1");

                    PollingStaff staff = new PollingStaff(Integer.parseInt(staffid.getText()), staffname.getText(), (stationid.getValue()), assignedrole.getValue(), username.getText(), password.getText());
                    //this ftn is to take values from the interface and pass them to the ph
                    System.out.println("out3");

                    boolean status = ems.addPollingStaff(staff);
                    if (status) {

                        System.out.println("staff added successfully");
                        showPopUPAddedStaff();
                    }
                }else {
                    showErrorMessage(this.primaryStage, "Duplicate Values!");
                    System.out.println("id in use");
                }
            }else{
                System.out.println("invalid datatype");
                showErrorMessage(this.primaryStage, "Invalid datatype!");

            }
        }else{
            System.out.println("Empty data fields");
            showErrorMessage(this.primaryStage, "Empty data fields!");
        }
    }

    boolean checkForEmptyFields() {
        if (staffid.getText().isEmpty() || staffname.getText().isEmpty() || stationid.getValue()==null || username.getText().isEmpty()  || password.getText().isEmpty()  || assignedrole.getValue()==null) {
            return false;
        }

        return true;
    }
    boolean checkForIncorrectDataType(){
        if (!staffname.getText().matches("[a-zA-Z ]+") || !username.getText().matches("[a-zA-Z0-9]+") || !password.getText().matches("[a-zA-Z0-9]+")) {
            return false;
        }

        if (!staffid.getText().matches("\\d+")) {
            return false;
        }

        return true;
    }

    boolean isInUse() {
        List<PollingStaff> staffs = ems.getPollingStaff();

        for (PollingStaff st : staffs) {
            if (st.getStaffID()== Integer.parseInt(staffid.getText()) || st.getUsername().equals(username.getText() )||st.getPassword().equals(password.getText())  ) {
                return true;
            }
        }
        return false;
    }

    void showErrorMessage(Stage st, String msg) throws IOException {
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
    void showPopUPAddedStaff() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(DEMO.class.getResource("/fxmlFiles/PopUps/NewStaffAdded.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 460, 230);
        Stage stage = new Stage();
        stage.setTitle("PopUp");
        // After loading the FXML, get the controller and set the ElectionManagementSystem
        NewStaffController controller = fxmlLoader.getController();
        // Set the primary stage in the controller
        controller.setPrimaryStage(stage);
        stage.setScene(scene);
        stage.show();
    }
}
