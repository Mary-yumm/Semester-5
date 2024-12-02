package votix.controllers.AdminControllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import votix.services.AdminElectionManagementSystem;

import java.io.IOException;
import java.util.ArrayList;

public class staffAssignmentsController {

    public AnchorPane contentPane;
    public ImageView backArrow;
    @FXML
    private Button searchbtn;
    @FXML
    private Button removeFilter;
    @FXML
    private VBox staffTable;
    @FXML
    private TextField name;
    private AdminElectionManagementSystem ems;
    private Stage stage;
    @FXML
    private ComboBox<String> area;

    public void setElectionManagementSystem(AdminElectionManagementSystem ems, Stage primaryStage) {
        this.ems = ems;
        this.stage = primaryStage;

        ArrayList<String> ar = ems.getAreaID();
        ObservableList<String> array = FXCollections.observableArrayList(ar);
        area.setItems(array);
        loadStaffAssignments();
    }

    public void initialize() {
        backArrow.setCursor(Cursor.HAND);
    }

    void loadStaffAssignments() {
        ArrayList<Object> list = ems.getStaffAssignments();
        staffTable.setSpacing(10); // Set vertical spacing for rows

        for (Object obj : list) {
            Object[] list1 = (Object[]) obj;

            HBox row = new HBox();
            row.setSpacing(20); // Set horizontal spacing for columns
            row.setPrefHeight(44);
            row.getStyleClass().add("table-row");

            Label idlabel = new Label(list1[0].toString());
            idlabel.getStyleClass().add("table-cell");
            idlabel.setMinWidth(200);
            row.getChildren().add(idlabel);

            Label namelabel = new Label((String) list1[1]);
            namelabel.getStyleClass().add("table-cell");
            namelabel.setMinWidth(330);
            row.getChildren().add(namelabel);

            Label areaidlabel = new Label(list1[3].toString());
            areaidlabel.getStyleClass().add("table-cell");
            areaidlabel.setMinWidth(130);
            row.getChildren().add(areaidlabel);

            Label areaname = new Label((String) list1[4]);
            areaname.getStyleClass().add("table-cell");
            areaname.setMinWidth(350);
            row.getChildren().add(areaname);

            Label stationlabel = new Label(list1[2].toString());
            stationlabel.getStyleClass().add("table-cell");
            stationlabel.setMinWidth(100);
            row.getChildren().add(stationlabel);

            staffTable.getChildren().add(row);
        }
    }

    public void searchByStaff(ActionEvent actionEvent) {
        if (!staffTable.getChildren().isEmpty() && staffTable.getChildren().size() > 1) {
            staffTable.getChildren().remove(1, staffTable.getChildren().size()); // Clear existing rows
        }
        String staffName = name.getText().trim().toLowerCase(); // Get trimmed and lowercase name input
        ArrayList<Object> list = ems.getStaffAssignments();

        for (Object obj : list) {
            Object[] list1 = (Object[]) obj;
            String staffNameFromList = ((String) list1[1]).toLowerCase(); // Staff name from data (in lowercase)

            if (staffNameFromList.contains(staffName)) { // Use contains for partial matches (case-insensitive)
                staffTable.setSpacing(10); // Set vertical spacing for rows
                HBox row = new HBox();
                row.setSpacing(20);
                row.setPrefHeight(44);
                row.getStyleClass().add("table-row");

                // Add labels like in loadStaffAssignments method
                Label idlabel = new Label(list1[0].toString());
                idlabel.getStyleClass().add("table-cell");
                idlabel.setMinWidth(200);
                row.getChildren().add(idlabel);

                Label namelabel = new Label((String) list1[1]);
                namelabel.getStyleClass().add("table-cell");
                namelabel.setMinWidth(330);
                row.getChildren().add(namelabel);

                Label areaidlabel = new Label(list1[3].toString());
                areaidlabel.getStyleClass().add("table-cell");
                areaidlabel.setMinWidth(130);
                row.getChildren().add(areaidlabel);

                Label areaname = new Label((String) list1[4]);
                areaname.getStyleClass().add("table-cell");
                areaname.setMinWidth(350);
                row.getChildren().add(areaname);

                Label stationlabel = new Label(list1[2].toString());
                stationlabel.getStyleClass().add("table-cell");
                stationlabel.setMinWidth(100);
                row.getChildren().add(stationlabel);

                staffTable.getChildren().add(row);
            }
        }
    }


    public void searchByArea(ActionEvent actionEvent) {
        if (!staffTable.getChildren().isEmpty() && staffTable.getChildren().size() > 1) {
            staffTable.getChildren().remove(1, staffTable.getChildren().size()); // Clear existing rows
        }
        String areaValue = area.getValue(); // Get selected area ID
        ArrayList<Object> list = ems.getStaffAssignments();

        for (Object obj : list) {
            Object[] list1 = (Object[]) obj;
            String areaIDFromList = list1[3].toString(); // Area ID from data

            if (areaIDFromList.equals(areaValue)) { // Match area ID
                staffTable.setSpacing(10); // Set vertical spacing for rows

                HBox row = new HBox();
                row.setSpacing(20);
                row.setPrefHeight(44);
                row.getStyleClass().add("table-row");

                // Add labels like in loadStaffAssignments method
                Label idlabel = new Label(list1[0].toString());
                idlabel.getStyleClass().add("table-cell");
                idlabel.setMinWidth(200);
                row.getChildren().add(idlabel);

                Label namelabel = new Label((String) list1[1]);
                namelabel.getStyleClass().add("table-cell");
                namelabel.setMinWidth(330);
                row.getChildren().add(namelabel);

                Label areaidlabel = new Label(list1[3].toString());
                areaidlabel.getStyleClass().add("table-cell");
                areaidlabel.setMinWidth(130);
                row.getChildren().add(areaidlabel);

                Label areaname = new Label((String) list1[4]);
                areaname.getStyleClass().add("table-cell");
                areaname.setMinWidth(350);
                row.getChildren().add(areaname);

                Label stationlabel = new Label(list1[2].toString());
                stationlabel.getStyleClass().add("table-cell");
                stationlabel.setMinWidth(100);
                row.getChildren().add(stationlabel);

                staffTable.getChildren().add(row);
            }
        }
    }


    public void removeFilter(ActionEvent actionEvent) {
        if (!staffTable.getChildren().isEmpty() && staffTable.getChildren().size() > 1) {
            staffTable.getChildren().remove(1, staffTable.getChildren().size());
        }
        area.setValue(null);
        name.setText(null);
        loadStaffAssignments();
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

            contentPane.getChildren().setAll(addCandidatePane);
            contentPane.requestLayout();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
