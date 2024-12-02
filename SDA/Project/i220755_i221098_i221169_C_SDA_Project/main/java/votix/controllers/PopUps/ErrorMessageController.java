package votix.controllers.PopUps;

import javafx.scene.control.Label;
import javafx.stage.Stage;

public class ErrorMessageController {
    public Label messageLabel;
    private Stage stage;

    // Set the message label text
    public void setMessageLabel(String msg){
        this.messageLabel.setText(msg);
    }

    // Set the primary stage for closing later if needed
    public void setPrimaryStage(Stage stage) {
        this.stage = stage;
    }

    // Optional: A method to close the popup (if needed)
    public void closePopup() {
        if (stage != null) {
            stage.close();
        }
    }
}
