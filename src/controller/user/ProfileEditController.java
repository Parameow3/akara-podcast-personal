package controller.user;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class ProfileEditController implements Initializable {

    @FXML
    private BorderPane profileEditPane;

    @FXML
    private Button saveButton;

    @FXML
    private TextField name_tf;


    @FXML
    public void saveClicked(MouseEvent event) throws IOException {
        profileEditPane.setTop(null);
        BorderPane profileEdit = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/user/Profile.fxml")));
        profileEditPane.setCenter(profileEdit);
    }

    @FXML
    private void uploadButtonClicked(ActionEvent event){

        FileChooser fileChooser = new FileChooser();

        // set the title of the displayed file dialog
        fileChooser.setTitle("Choose your Image");

        // set the initial directory for the displayed file dialog
        // user.home refers to the path of the user directory
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        // gets the extension filters used in the displayed file dialog
        fileChooser.getExtensionFilters().clear(); // removes all the elements from this list
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"));

        // set the selected file or null if no file has been selected
        File file = fileChooser.showOpenDialog(null); // show a new file open dialog

        if (file != null) {
            // return the absolute pathname string of this abstract pathname
            // URI that represent this abstract pathname
        }
        else {
            System.out.println("A file is invalid!");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
