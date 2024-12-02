package votix.controllers.AdminControllers;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import votix.controllers.MainPageController;
import votix.services.AdminElectionManagementSystem;
import votix.services.PersistenceHandler;

public class AdminMenuController {

    @FXML
    public Button viewLogs;
    @FXML
    public Button monitorSystems;
    @FXML
    public Button logout1;
    @FXML
    public ImageView logout2;
    @FXML
    private Button viewCandidate;
    @FXML
    private Button addCand;
    @FXML
    private Button viewForm;
    @FXML
    private Button viewreport;
    @FXML
    private Button viewStaff;
    @FXML
    private Button pollingStaff;
    @FXML
    private Button staffUpdation;
    @FXML
    private Button viewresult;
    @FXML
    private AnchorPane contentPane;

    private AdminElectionManagementSystem ems;
    private Stage primaryStage;
    private PersistenceHandler ph;


    public void setElectionManagementSystem(AdminElectionManagementSystem electionManagementSystem) {
        this.ems = electionManagementSystem;
        if (ems != null) {
            System.out.println("ems set in Admin menu: " + (ems != null));  // Debugging line
        } else {
            System.out.println("ems set in admin menu: null");
        }
    }
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    // Method to set the database connection
    public void setConnection(PersistenceHandler ph) {
        this.ph = ph;
    }

    public void viewCandidateBtn() {
        setActiveButton(viewCandidate);
    }

    public void addCandBtn() {
        setActiveButton(addCand);
    }

    public void viewFormBtn() {
        setActiveButton(viewForm);
    }

    public void viewReportBtn() {
        setActiveButton(viewreport);
    }

    public void viewStaffBtn() {
        setActiveButton(viewStaff);
    }

    public void viewresultBtn() {
        setActiveButton(viewresult);
    }

    public void pollingStaffBtn() {
        setActiveButton(pollingStaff);
    }

    public void staffUpdationBtn() {
        setActiveButton(staffUpdation);
    }

    public void viewLogsBtn() {
        setActiveButton(viewLogs);
    }

    public void monitorSystemsBtn() {
        setActiveButton(monitorSystems);
    }


    @FXML
    void addCandidate() {
        addCandBtn();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxmlFiles/AdminControlled/addCandidate.fxml"));
            AnchorPane addCandidatePane = loader.load();
            addCandidateController controller = loader.getController();

            // Check if controller is not null and set EMS
            if (controller != null) {
                System.out.println("setting");
                controller.setElectionManagementSystem(this.ems, this.primaryStage);
            } else {
                System.out.println("addCandidateController is null!");  // Debugging line
            }
            contentPane.getChildren().setAll(addCandidatePane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void viewCandidateList() {
        viewCandidateBtn();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxmlFiles/AdminControlled/CandidateList.fxml"));
            AnchorPane addCandidatePane = loader.load();
            candidateListController controller = loader.getController();

            if (controller != null) {
                System.out.println("setting");
                controller.setElectionManagementSystem(this.ems, this.primaryStage);
            } else {
                System.out.println("viewCandidateController is null!");
            }
            contentPane.getChildren().setAll(addCandidatePane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void viewStaffAssignments() {
        viewStaffBtn();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxmlFiles/AdminControlled/staffAssignments.fxml"));
            AnchorPane addCandidatePane = loader.load();
            staffAssignmentsController controller = loader.getController();

            // Check if controller is not null and set EMS
            if (controller != null) {
                System.out.println("setting");
                controller.setElectionManagementSystem(this.ems, this.primaryStage);  // Pass the ems instance
            } else {
                System.out.println("staffAssignmentsController is null!");  // Debugging line
            }
            contentPane.getChildren().setAll(addCandidatePane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void viewElectionResult() throws IOException {
        viewresultBtn();
        // Load the FXML file
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxmlFiles/AdminControlled/ElectionResult.fxml"));
            Pane resultvbox = loader.load();
            ElectionResultController controller = loader.getController();
            // Ensure that the controller is correctly initialized
            if (controller != null) {
                System.out.println("ElectionReportController initialized successfully.");
                controller.set(this.ems, this.primaryStage);  // Pass the ems instance
            } else {
                System.out.println("Error: ElectionReportController is null!");
            }
            contentPane.getChildren().setAll(resultvbox);
        }catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    public void update_DeactivaeStaff() {
        staffUpdationBtn();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxmlFiles/AdminControlled/UpdateDeactiveStaff.fxml"));
            AnchorPane addCandidatePane = loader.load();
            UpdateDeactiveStaffController controller = loader.getController();

            if (controller != null) {
                System.out.println("setting UpdateDeactiveStaffController");
                controller.setElectionManagementSystem(this.ems, this.primaryStage);
            } else {
                System.out.println("UpdateDeactiveStaffController is null!");
            }
            contentPane.getChildren().setAll(addCandidatePane);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void addPollingStaff() {
        pollingStaffBtn();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxmlFiles/AdminControlled/addPollingStaff.fxml"));
            AnchorPane addCandidatePane = loader.load();
            addPollingStaffController controller = loader.getController();

            // Check if controller is not null and set EMS
            if (controller != null) {
                controller.setElectionManagementSystem(this.ems, this.primaryStage);  // Pass the ems instance
            } else {
                System.out.println("add polling staff is null!");  // Debugging line
            }
            contentPane.getChildren().setAll(addCandidatePane);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void viewElectionReport() {
        viewReportBtn();
        // Load the FXML file
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxmlFiles/AdminControlled/ElectionReport.fxml"));
            Pane electionreport = loader.load();
            ElectionReportController controller = loader.getController();
            // Ensure that the controller is correctly initialized
            if (controller != null) {
                System.out.println("ElectionResultController initialized successfully.");
                controller.set(this.ems, this.primaryStage);  // Pass the ems instance
            } else {
                System.out.println("Error: ElectionResultController is null!");
            }

            // Add the loaded FXML content to the content pane
            contentPane.getChildren().setAll(electionreport);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void monitorActiveSystems() {
        monitorSystemsBtn(); // Highlight the button
        try {
            // Load the MonitorActiveSystems.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxmlFiles/AdminControlled/MonitorActiveSystems.fxml"));
            AnchorPane monitorSystemsPane = loader.load();

            // Get the controller of the MonitorActiveSystems.fxml
            MonitorActiveSystemsController controller = loader.getController();

            // Set EMS in the controller
            if (controller != null) {
                System.out.println("Setting EMS in MonitorActiveSystemsController");
                controller.setElectionManagementSystem(this.ems, this.primaryStage); // Pass the EMS instance
            } else {
                System.out.println("MonitorActiveSystemsController is null!");
            }

            // Add the loaded FXML content to the content pane
            contentPane.getChildren().setAll(monitorSystemsPane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    public void viewLogs() {
        viewLogsBtn();
        try {
            // Load the ViewLogs.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxmlFiles/AdminControlled/ViewLog.fxml"));
            AnchorPane viewLogsPane = loader.load();

            // Get the controller of the ViewLogs.fxml
            ViewLogsController controller = loader.getController();

            // Set EMS and PersistenceHandler in the controller
            if (controller != null) {
                System.out.println("Setting EMS and PersistenceHandler in ViewLogsController");
                controller.setEMS(this.ems, this.primaryStage);
            } else {
                System.out.println("ViewLogsController is null!");  // Debugging line
            }

            // Add the loaded FXML content to the content pane
            contentPane.getChildren().setAll(viewLogsPane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    public void viewElectionForm() throws IOException {
        viewFormBtn();
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxmlFiles/AdminControlled/electionForm.fxml"));
            AnchorPane root = loader.load();
            ElectionFormController controller = loader.getController();
            // Ensure that the controller is correctly initialized
            if (controller != null) {
                System.out.println("Election Form Controller initialized successfully.");
                controller.set(this.ems, this.primaryStage);  // Pass the ems instance
            } else {
                System.out.println("Error: Election Form Controller is null!");
            }

            // Add the loaded FXML content to the content pane
            contentPane.getChildren().setAll(root);

        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to set the active button
    private void setActiveButton(Button activeButton) {
        // Remove the 'selected' class from all buttons
        viewStaff.getStyleClass().remove("selected");
        viewForm.getStyleClass().remove("selected");
        addCand.getStyleClass().remove("selected");
        viewCandidate.getStyleClass().remove("selected");
        viewreport.getStyleClass().remove("selected");
        pollingStaff.getStyleClass().remove("selected");
        staffUpdation.getStyleClass().remove("selected");
        viewresult.getStyleClass().remove("selected");
        viewLogs.getStyleClass().remove("selected");
        monitorSystems.getStyleClass().remove("selected");

        // Add the 'selected' class to the clicked button
        activeButton.getStyleClass().add("selected");
    }


    public void logoutFtn(MouseEvent actionEvent) {
        try {
            // Load the previous screen (MainPage.fxml)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxmlFiles/mainpage.fxml"));
            Scene scene = new Scene(loader.load());

            // Get the controller of MainPage and set up necessary bindings again
            MainPageController mainPageController = loader.getController();
            mainPageController.setPrimaryStage(primaryStage);  // Ensure the primaryStage is passed back

            // Set the scene and show the primaryStage
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
