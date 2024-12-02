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
import votix.controllers.PopUps.staffAccountManagementController;
import votix.models.PollingStaff;
import votix.services.AdminElectionManagementSystem;

import java.io.IOException;
import java.util.ArrayList;

public class UpdateDeactiveStaffController {
    public AnchorPane contentPane;
    public Button backbtn;
    public TextField username;
    public TextField password;
    public ComboBox<Integer> stationid;
    public Button updateAccount;
    public Button deactivateAccount;
    public ComboBox<Integer> staffId;
    public Button ActivateAccount;
    public ImageView backArrow;
    Stage primaryStage;
    AdminElectionManagementSystem ems;

    public void setElectionManagementSystem(AdminElectionManagementSystem ems, Stage stage){
        this.ems = ems;
        this.primaryStage = stage;
        loadIds();
    }
    public void initialize() {
        backArrow.setCursor(Cursor.HAND);
    }

    void loadIds(){
        ArrayList<Integer> list = ems.getStations();
        ObservableList<Integer> np = FXCollections.observableArrayList(list);
        stationid.setItems(np);

        ArrayList<PollingStaff> pollingStaff = ems.getPollingStaff();
        ArrayList<Integer> list2 = new ArrayList<>();
        for(PollingStaff ps: pollingStaff){
            list2.add(ps.getStaffID());
        }

        ObservableList<Integer> id = FXCollections.observableArrayList(list2);
        staffId.setItems(id);
    }
    public void UpdateAccount(ActionEvent actionEvent) throws IOException {
        ems.updatePollingStaff(staffId.getValue(), username.getText(), password.getText(),stationid.getValue());
        de_activatePopup("Account updated successfully");
    }

    public void activateAccount(ActionEvent actionEvent) throws IOException {
        ems.managePollingStaff("activate",staffId.getValue());
        de_activatePopup("Account activated");

    }

    public void deactivateAccount(ActionEvent actionEvent) throws IOException {
        ems.managePollingStaff("deactivate",staffId.getValue());
        de_activatePopup("Account deactivated");
    }

    private void de_activatePopup(String msg) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(DEMO.class.getResource("/fxmlFiles/PopUps/staffAccountManagement.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 404, 201);
        Stage stage = new Stage();
        stage.setTitle("PopUp");

        staffAccountManagementController controller = fxmlLoader.getController();
        controller.setLabel(msg);
        controller.setPrimaryStage(stage);
        stage.setScene(scene);
        stage.show();
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
}
