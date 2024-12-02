package votix.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import votix.services.PersistenceHandler;

import java.io.IOException;

public class AboutController {

    public Button BackButton;
    public AnchorPane contentpane;
    private PersistenceHandler ph;
    private Stage primaryStage;

    @FXML
    public void HandleBackButtonAction(MouseEvent actionEvent) {
        try {
            // Load the previous screen (MainPage.fxml)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxmlFiles/MainPage.fxml"));
            AnchorPane menu = loader.load();

            //Get the controller of MainPage and set up necessary bindings again
            MainPageController mainPageController = loader.getController();
            mainPageController.setph(ph);
            mainPageController.setPrimaryStage(primaryStage);  // Ensure the primaryStage is passed back

            contentpane.getChildren().setAll(menu);
            contentpane.requestLayout();  // Request a layout refresh
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // Set the primary stage so we can use it later for scene switching
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
    public PersistenceHandler getPh() {
        return ph;
    }

    public void setPh(PersistenceHandler ph) {
        this.ph = ph;
    }

}
