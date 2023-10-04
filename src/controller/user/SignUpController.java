package controller.user;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.AccessibleRole;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;


import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;

public class SignUpController implements Initializable {

    @FXML
    private Button signUpButton;

    @FXML
    private TextField tf_name;

    @FXML
    private TextField tf_email;

    @FXML
    private PasswordField pf_password;

    @FXML
    private TextField tf_password;

    @FXML
    private PasswordField pf_cf_password;

    @FXML
    private TextField tf_cf_password;

    @FXML
    private CheckBox check_showPassword;

    @FXML
    private BorderPane signupPane;

    @FXML
    private Label alertLabel;

    @FXML
    public void signUpClicked(MouseEvent event) throws IOException {


        // check email format
        boolean format = false;
        for (int i = 0; i < tf_email.getText().length(); i++) {
            if (tf_email.getText().charAt(i) == '@') {
                format = true;
                break;
            }
        }

        if (!tf_name.getText().trim().isEmpty()
                && !tf_email.getText().trim().isEmpty()
                && !pf_password.getText().trim().isEmpty()
                && pf_cf_password.getText().equals(pf_password.getText())
                && format)
        {
            alertLabel.setText("");
            signupPane.setTop(null);
            BorderPane profile = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/user/VerificationV1.fxml")));
            signupPane.setCenter(profile);

        }
        else {
            System.out.println("Please fill in all information!");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.show();
            alert.close();

            if (!format) {
                alertLabel.setText("Your email is wrong format!");
            }
            else {
                alertLabel.setText("Please fill all information to sign up correctly!");
            }
            alertLabel.setTextFill(Color.RED);
        }
    }

    @FXML
    public void loginSwitchClicked(MouseEvent event) throws IOException {
        signupPane.setTop(null);
        BorderPane profile = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/user/Login.fxml")));
        signupPane.setCenter(profile);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        // show passwords
        pf_password.textProperty().bindBidirectional(tf_password.textProperty());
        pf_cf_password.textProperty().bindBidirectional(tf_cf_password.textProperty());
        check_showPassword.setSelected(false);

        check_showPassword.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                tf_password.toFront();
                tf_cf_password.toFront();
            } else {
                pf_password.toFront();
                pf_cf_password.toFront();
            }
        });

    }

}
