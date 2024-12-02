package votix.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import votix.controllers.AdminControllers.AdminMenuController;
import votix.controllers.PollingPC.*;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.Objects;

import votix.services.AdminElectionManagementSystem;
import votix.services.PersistenceHandler;
import votix.services.PollingPCElectionManagementSystem;

public class LoginController {

    //@FXML
    public Button LoginButton;
    public AnchorPane contentPane;

    //private Label roleLabel;

    private String role;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;
    private PersistenceHandler ph;

    private Stage primaryStage;  // To hold the primary stage

    public void setPEMS(PollingPCElectionManagementSystem PEMS) {
        this.PEMS = PEMS;
    }

    private PollingPCElectionManagementSystem PEMS;
    private AdminElectionManagementSystem AEMS;


    // Add this method to set the primary stage
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
        System.out.println("Primary stage set in login controller: " + primaryStage);
    }

    public void setConnection(PersistenceHandler ph){
        this.ph = ph;
    }


    public void setRole(String role) {this.role = role;}

    public String getRole() {return role;}

    @FXML
    private void handleLogin() {
        System.out.println("Login button clicked!");


        if (Objects.equals(getRole(), "staff")) {
            System.out.println("Login button for staff clicked!");

            staffLogin();
        }
        else if (Objects.equals(getRole(), "admin")) {
            adminLogin();
        }
        else
            System.out.println("Error finding a login page");

    }


    private void staffLogin()
    {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String mac_address = getCurrentMacAddress();

        try {
            PEMS = new PollingPCElectionManagementSystem();
            PEMS.setPersistenceHandler(ph);
            // Use the dbHandler to verify staff credentials and MAC address
            if (PEMS.authorizePollingStaff(username, password,mac_address)) {
                PEMS.setSystemActive(PEMS.getSystemID());
                PEMS.initializeArrays();

                // Load PollingPc.fxml and switch to it on successful login
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxmlFiles/PollingPC/PollingPc.fxml"));
                AnchorPane menu = fxmlLoader.load();
                // Get the controller for PollingPC and set necessary dependencies
                PollingPcController pollingController = fxmlLoader.getController();
                pollingController.setElectionManagementSystem(PEMS);

                Screen screen = Screen.getPrimary();
                double screenWidth = screen.getVisualBounds().getWidth();
                double screenHeight = screen.getVisualBounds().getHeight();
                primaryStage.setWidth(screenWidth);   // Set 80% of screen width
                primaryStage.setHeight(screenHeight); // Set 80% of screen height
                primaryStage.centerOnScreen();


                // Switch the scene
                if (primaryStage != null) {
                    pollingController.setPrimaryStage(this.primaryStage);
                } else {
                    System.out.println("Primary stage is null.");
                }
                contentPane.getChildren().setAll(menu);
            } else {
                System.out.println("Invalid credentials or unauthorized device.");
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void adminLogin()
    {
        String username = usernameField.getText();
        String password = passwordField.getText();
        try {
            AEMS = new AdminElectionManagementSystem();
            AEMS.setPersistenceHandler(ph);
            // Use the dbHandler to verify staff credentials and MAC address
            if (AEMS.authorizeAdmin(username, password)) {
                // Load PollingPc.fxml and switch to it on successful login
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxmlFiles/AdminControlled/AdminMenu.fxml"));
                AnchorPane menu = fxmlLoader.load();

                // Get the controller for PollingPC and set necessary dependencies
                AdminMenuController controller = fxmlLoader.getController();
                //AdminElectionManagementSystem ad = new AdminElectionManagementSystem(ph);

                //controller.setConnection(ph);


                // Switch the scene
                if (primaryStage != null) {
                    controller.setElectionManagementSystem(AEMS);
                    controller.setPrimaryStage(this.primaryStage);
                } else {
                    System.out.println("Primary stage is null.");
                }
                contentPane.getChildren().setAll(menu);

            } else {System.out.println("Invalid credentials");}

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String getCurrentMacAddress() {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();

            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();

                // Skip loopback and down interfaces
                if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                    continue;
                }

                // Match Wi-Fi adapter by its name or description (Windows typically uses "Wi-Fi")
                String displayName = networkInterface.getDisplayName().toLowerCase();
                if (displayName.contains("wlan0") || displayName.contains("wi-fi") || displayName.contains("wireless")) {
                    byte[] mac = networkInterface.getHardwareAddress();

                    if (mac != null) {
                        StringBuilder macAddress = new StringBuilder();
                        for (byte macByte : mac) {
                            macAddress.append(String.format("%02X:", macByte));
                        }
                        // Remove the trailing colon
                        if (macAddress.length() > 0) {
                            macAddress.setLength(macAddress.length() - 1);
                        }

                        System.out.println("Wi-Fi MAC Address: " + macAddress);
                        return macAddress.toString();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Unable to retrieve Wi-Fi MAC address.");
        return null;
    }

    public void setph(PersistenceHandler ph) {
        System.out.println("in ph login");
        this.ph=ph;
    }

    public void handleBackButtonAction(javafx.scene.input.MouseEvent mouseEvent) {
        try {
            // Load the previous screen (MainPage.fxml)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxmlFiles/MainPage.fxml"));
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