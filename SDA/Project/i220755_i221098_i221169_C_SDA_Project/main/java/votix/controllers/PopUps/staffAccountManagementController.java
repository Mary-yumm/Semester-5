package votix.controllers.PopUps;

import javafx.scene.control.Label;
import javafx.stage.Stage;

public class staffAccountManagementController {
    public Label labelText;
    Stage stage;
    public void setLabel(String msg){
        labelText.setText(msg);
    }
    public void setPrimaryStage(Stage stage) {
        this.stage = stage;
    }

}
