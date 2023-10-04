package controller.user;

import controller.main.MainFormController;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;
import model.Podcast;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;


public class LoginController implements Initializable {
    @FXML
    private BorderPane loginPane;

    @FXML
    private Button loginButton;

    @FXML
    private TextField tf_Email;

    @FXML
    private PasswordField pf_password;

    @FXML
    private TextField tf_showPassword;

    @FXML
    private CheckBox checkToShowPassword;

    @FXML
    private Label alertLabel;

    @FXML
    private Button button_forget_password;

    @FXML
    public void loginClicked(MouseEvent event) throws IOException {
        logIn();
    }


    @FXML
    public void signUpClicked(MouseEvent event) throws IOException {
        loginPane.setTop(null);
        BorderPane profile = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/user/SignUp.fxml")));
        loginPane.setCenter(profile);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        // show passwords
        pf_password.textProperty().bindBidirectional(tf_showPassword.textProperty());
        checkToShowPassword.setSelected(false);

        checkToShowPassword.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                tf_showPassword.toFront();
            } else {
                pf_password.toFront();
            }
        });
    }

    private void logIn(){
        String jsonInput = "{" + "\"email\": \"" + tf_Email.getText() + "\"," + "\"password\": \"" + pf_password.getText() + "\"" + "}";
        HttpResponse<JsonNode> response = Unirest.post("https://dev.akarahub.tech/server4/signIn")
                        .body(jsonInput).contentType("application/json").asJson();

        System.out.println(response.getStatus());
        JSONObject credentialJson = response.getBody().getObject();

        boolean error = (boolean) credentialJson.get("error");

        if (error){
            System.out.println("User not found!!");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Email or Password are incorrect!");
            alert.show();
            alert.close();

            alertLabel.setText("Email or Password are incorrect!");
            alertLabel.setTextFill(Color.RED);
        }
        else {
            System.out.println("Login Successful");
            loginPane.setTop(null);
            BorderPane profile;
            try {
                profile = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/user/Profile.fxml")));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            loginPane.setCenter(profile);
        }
    }

    //#region FORGOT_BTN
    @FXML
    public void forgotClicked(MouseEvent mouseEvent) throws IOException {
        loginPane.setTop(null);
        BorderPane forget = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/user/Forget.fxml")));
        loginPane.setCenter(forget);
    }

    @FXML
    public void underLine(MouseEvent mouseEvent){
        button_forget_password.setUnderline(true);
    }

    @FXML
    public void removeUnderLine(MouseEvent mouseEvent){
        button_forget_password.setUnderline(false);
    }

    //#endregion
}
