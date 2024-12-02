package votix.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import votix.services.PersistenceHandler;

import java.io.IOException;

public class MainPageController {

    public Button about;
    public AnchorPane contentPane;
    @FXML
    private Button admin;

    @FXML
    private Button staff;

    private Stage primaryStage;
    private PersistenceHandler ph;

    public void setPrimaryStage(Stage stage) {

        this.primaryStage = stage;
        System.out.println("Primary stage set in main page controller: " + primaryStage);

    }

    // This method handles the "ADMIN" button click
    @FXML
    private void handleAdminLogin(MouseEvent event) throws IOException {
        System.out.println("Admin button clicked!");
        loadLoginScene("admin");
        /*try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxmlFiles/login.fxml"));
            Scene adminScene = new Scene(loader.load(), 600, 400);
            primaryStage.setScene(adminScene);  // Set the new scene
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    // This method handles the "STAFF" button click
    @FXML
    private void handleStaffLogin(MouseEvent event) throws IOException {
        System.out.println("Staff button clicked!");
        loadLoginScene("staff");
       /* try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxmlFiles/login.fxml"));
            Scene staffScene = new Scene(loader.load(), 600, 400);
            primaryStage.setScene(staffScene);  // Set the new scene
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }
    @FXML
    private void handleAbout(MouseEvent event)  {

        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxmlFiles/about.fxml"));
            AnchorPane aboutt = loader.load();
            AboutController Controller = loader.getController();
            Controller.setPh(this.ph);


            contentPane.getChildren().setAll(aboutt);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    private void loadLoginScene(String role) throws IOException {
        System.out.println("Loading login scene for role: " + role);  // Debugging line
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxmlFiles/login.fxml"));
        if (loader == null) {
            System.out.println("Failed to find login.fxml at /fxmlFiles/login.fxml");
        }
        AnchorPane loginScreen = loader.load();



        LoginController loginController = loader.getController();
        if (loginController != null) {
            System.out.println("Setting PersistenceHandler in login controller");
            loginController.setRole(role);
            loginController.setph(ph);
            loginController.setPrimaryStage(this.primaryStage);
        } else {
            System.out.println("LoginController is null!");  // Debugging line
        }

        System.out.println("Setting the scene to primaryStage");
        contentPane.getChildren().setAll(loginScreen);

    }


    public void setph(PersistenceHandler p) {
        this.ph = p;
    }
}
