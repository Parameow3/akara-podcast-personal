/*-----------------------------------------------------------------------------------------
 * NAME : MainFormController.java
 * VER  : v0.1
 * PROJ : Akara
 * CODE CLEAN? : Yes
 *-----------------------------------------------------------------------------------------
 *                      H      I      S      T      O      R      Y
 *-----------------------------------------------------------------------------------------
 *   DATE        AUTHOR         DESCRIPTION
 * ----------  --------------  ------------------------------------------------------------
 * 2022-06-24   Nuth Vireak     creation
 * ----------  --------------  ---------------------------------------------------------
 * 2022-07-22   Nuth Vireak     Modification
 *---------------------------------------------------------------------------------------*/

package controller.main;

import controller.search.SearchResultController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import kong.unirest.GenericType;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.gson.GsonObjectMapper;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;
import model.Podcast;
import model.api.desktop.model.AccessToken;
import model.api.desktop.model.Credential;
import model.api.desktop.model.RefreshToken;

import java.io.*;
import java.net.URL;
import java.util.*;


public class MainFormController implements Initializable {

    //------------------------------------------------------------------------------------
    // fields declaration                                                                 |
    //------------------------------------------------------------------------------------
    @FXML
    public BorderPane borderPane;

    @FXML
    private BorderPane mainPane;

    @FXML
    private VBox mediaPlayerContainer;

    @FXML
    private Label modeLabel;

    @FXML
    private TextField searchTF;

    public static BorderPane staticMainPane;
    public static Label staticModelLabel;

    public static List<Podcast> podcastList;

    public static boolean init = false;

    private BorderPane discover;
    private BorderPane trending;

    public static BorderPane staticBorderpane;

    //------------------------------------------------------------------------------------
    //  Methods declarations                                                             |
    //------------------------------------------------------------------------------------

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){



        // one init per start
        if (!init){
            System.out.println("do it!");
//            DataInitializer dataInitializer = new DataInitializer();
            podcastList = getAllPodcast();

        }
        staticMainPane = mainPane;
        staticModelLabel = modeLabel;
        staticBorderpane = borderPane;

        VBox mediaPlayer;

        try {
            mediaPlayer = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/main/MediaPlayer.fxml")));
            discover = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/discover/Discover.fxml")));
            trending = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/trending/Trending.fxml")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // set the Discover VBox to the center of the BorderPane of the MainForm
        borderPane.setCenter(discover);

        // set the MediaPlayer VBox to the bottom of the BorderPane of the MainForm
        mediaPlayerContainer.getChildren().add(mediaPlayer);
    }

    //#region CREDENTIAL
    private AccessToken getAccessToken(RefreshToken refreshToken) {
        File accessFile = new File("src/podcastData/accessToken.txt");
        AccessToken accessToken = new AccessToken();

        if (accessFile.isFile()) { // if file is existed
            try {
                if (new BufferedReader(new FileReader(accessFile)).readLine() == null) {
                    // post access and get response
                    // request access
                    HttpResponse<JsonNode> response = Unirest.post("https://dev.akarahub.tech/server4/akara/desktop/access/token").header("Authorization", "bearer " + refreshToken.getRefreshToken()).asJson();

                    JSONObject refreshJson = response.getBody().getObject();

                    accessToken.setMessage(refreshJson.get("message").toString());
                    accessToken.setAccessToken(refreshJson.get("accessToken").toString());

                    // write to file
                    try {
                        FileOutputStream fileOutputStream = new FileOutputStream(accessFile);
                        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);

                        // write chav
                        objectOutputStream.writeObject(accessToken);

                        objectOutputStream.close();
                        fileOutputStream.close();

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    System.out.println("Access Existed");
                    // check to make sure it's not duplicate
                    FileInputStream fi = new FileInputStream(accessFile);
                    ObjectInputStream oi = new ObjectInputStream(fi);

                    accessToken = (AccessToken) oi.readObject();
                    oi.close();
                    fi.close();
                }
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        } else { // create a file if it's not exist

            try {
                if (accessFile.createNewFile()) {
                    System.out.println("File created: " + accessFile.getName());
                } else {
                    System.out.println("File already exists.");
                }
            } catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }

            // post access and get response
            // request access
            HttpResponse<JsonNode> response = Unirest.post("https://dev.akarahub.tech/server4/akara/desktop/access/token").header("Authorization", "bearer " + refreshToken.getRefreshToken()).asJson();

            JSONObject refreshJson = response.getBody().getObject();

            System.out.println(refreshJson.toString());
            accessToken.setMessage(refreshJson.get("message").toString());
            accessToken.setAccessToken(refreshJson.get("accessToken").toString());

            // write to file
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(accessFile);
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);

                // write chav
                objectOutputStream.writeObject(accessToken);

                objectOutputStream.close();
                fileOutputStream.close();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return accessToken;
    }

    private RefreshToken getRefreshToken(Credential credential) {
        File refreshFile = new File("src/podcastData/refreshToken.txt");
        RefreshToken refreshToken = new RefreshToken();
        String jsonInput = "{" + "    \"grantType\": \"credential\"," + "    \"clientId\": \"" + credential.getClientId() + "\"," + "    \"clientSecret\": \"" + credential.getClientSecret() + "\"," + "    \"scope\": \"desktop\"" + "}";

        if (refreshFile.isFile()) { // if file is existed
            try {
                if (new BufferedReader(new FileReader(refreshFile)).readLine() == null) {
                    // post refresh and get response
                    // request refresh
                    HttpResponse<JsonNode> response = Unirest.post("https://dev.akarahub.tech/server4/akara/token/refresh").body(jsonInput).contentType("application/json").asJson();

                    JSONObject refreshJson = response.getBody().getObject();

                    System.out.println(refreshJson.toString());
                    refreshToken.setRefreshToken(refreshJson.get("refreshToken").toString());

                    // write to file
                    try {
                        FileOutputStream fileOutputStream = new FileOutputStream(refreshFile);
                        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);

                        // write chav
                        objectOutputStream.writeObject(refreshToken);

                        objectOutputStream.close();
                        fileOutputStream.close();

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    System.out.println("Refresh Existed");
                    // check to make sure it's not duplicate
                    FileInputStream fi = new FileInputStream(refreshFile);
                    ObjectInputStream oi = new ObjectInputStream(fi);

                    refreshToken = (RefreshToken) oi.readObject();
                    oi.close();
                    fi.close();
                }
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        } else { // create a file if it's not exist

            try {
                if (refreshFile.createNewFile()) {

                    System.out.println("File created: " + refreshFile.getName());
                } else {
                    System.out.println("File already exists.");
                }
            } catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }

            // post refresh and get response
            // request refresh
            HttpResponse<JsonNode> response = Unirest.post("https://dev.akarahub.tech/server4/akara/token/refresh").body(jsonInput).contentType("application/json").asJson();

            JSONObject refreshJson = response.getBody().getObject();

            refreshToken.setRefreshToken(refreshJson.get("refreshToken").toString());

            // write to file
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(refreshFile);
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);

                // write chav
                objectOutputStream.writeObject(refreshToken);

                objectOutputStream.close();
                fileOutputStream.close();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return refreshToken;
    }

    // getCredential
    private Credential getCredential() {
        File credentialFile = new File("src/podcastData/credential.txt");
        Credential credential = new Credential();
        if (credentialFile.isFile()) { // if file is existed
            try {
                if (new BufferedReader(new FileReader(credentialFile)).readLine() == null) {
                    // post credential and get response
                    // request credential
                    HttpResponse<JsonNode> response = Unirest.post("https://dev.akarahub.tech/server4/akara/credential").asJson();

                    JSONObject credentialJson = response.getBody().getObject();

                    boolean error = (boolean) credentialJson.get("error");

                    // get message
                    String message = (String) credentialJson.get("message");

                    // get data pocket ( client ID and Client Secret )
                    JSONObject data = (JSONObject) credentialJson.get("data");

                    credential.setClientId(data.get("clientId").toString());
                    credential.setClientSecret(data.get("clientSecret").toString());

                    // write to file
                    try {
                        FileOutputStream fileOutputStream = new FileOutputStream(credentialFile);
                        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);

                        // write chav
                        objectOutputStream.writeObject(credential);

                        objectOutputStream.close();
                        fileOutputStream.close();

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    System.out.println("Credential Existed");
                    // check to make sure it's not duplicate
                    FileInputStream fi = new FileInputStream(credentialFile);
                    ObjectInputStream oi = new ObjectInputStream(fi);

                    credential = (Credential) oi.readObject();
                    oi.close();
                    fi.close();
                }
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        } else { // create a file if it's not exist

            try {
                if (credentialFile.createNewFile()) {
                    System.out.println("File created: " + credentialFile.getName());
                } else {
                    System.out.println("File already exists.");
                }
            } catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }

            // post credential and get response
            // request credential
            HttpResponse<JsonNode> response = Unirest.post("https://dev.akarahub.tech/server4/akara/credential").asJson();

            JSONObject credentialJson = response.getBody().getObject();

            // get error
            boolean error = (boolean) credentialJson.get("error");

            // get data pocket ( client ID and Client Secret )
            JSONObject data = (JSONObject) credentialJson.get("data");

            credential.setClientId(data.get("clientId").toString());
            credential.setClientSecret(data.get("clientSecret").toString());
            // write to file
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(credentialFile);
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);

                // write chav
                objectOutputStream.writeObject(credential);

                objectOutputStream.close();
                fileOutputStream.close();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return credential;
    }

    //#endregion

    private List<Podcast> getAllPodcast() {
        HttpResponse<JsonNode> response =
                Unirest.get("https://dev.akarahub.tech/discover/podcast/list/listallpodcast")
                        .header("Authorization", "bearer " + getAccessToken(getRefreshToken(getCredential())).getAccessToken()).asJson();
        JSONObject credentialJson = response.getBody().getObject();
        List<Podcast> podcasts = new ArrayList<>();

        boolean error = (boolean) credentialJson.get("error");

        if (error){
            File accessFile = new File("src/podcastData/accessToken.txt");
            if (accessFile.delete()) {
                System.out.println("Deleted the file: " + accessFile.getName());
                return getAllPodcast();
            } else {
                System.out.println("Failed to delete the file.");
            }
        }
        else {
//            System.out.println(credentialJson.get("data"));
            GenericType<List<Podcast>> listType = new GenericType<>(){};
            GsonObjectMapper gson = new GsonObjectMapper();
            podcasts = gson.readValue(credentialJson.get("data").toString(), listType);


        }


        podcasts.forEach(x-> System.out.println(x.getTitle()));
        return podcasts;
    }

    @FXML
    private void communityClicked(MouseEvent event) throws IOException {

        VBox community = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/additional/Community.fxml")));
        community.setAlignment(Pos.CENTER);
        borderPane.setCenter(community);
    }

    @FXML
    private void discoverClick(MouseEvent event) throws IOException {
        FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/discover/Discover.fxml")));
        borderPane.setCenter(discover);
    }

    @FXML
    private void favoriteClick(MouseEvent event) throws IOException {

        BorderPane favorite = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/additional/Favorite.fxml")));
        borderPane.setCenter(favorite);
    }

    @FXML
    private void playlistClick(MouseEvent event) throws IOException {

        BorderPane playlist = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/playlist/Playlist.fxml")));
        borderPane.setCenter(playlist);

    }

    @FXML
    private void trendingClicked(MouseEvent event) throws IOException {
        borderPane.setCenter(trending);
    }

    @FXML
    public void profileClicked(MouseEvent event) throws IOException {

        BorderPane login = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/user/Login.fxml"))); // create a Profile VBox object to store the Profile VBox in the FXML file
        // check that user login or not
//        if (DbUtils.getRetrievedID() != 0) {
//            login = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/user/Profile.fxml")));
//        }
//        else {
//            login = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/user/Login.fxml")));
//            modeClicked(event);
//        }

        // set the Profile VBox to the center of the BorderPane of the MainForm
        borderPane.setCenter(login);

    }

    @FXML
    public void feedbackClicked(MouseEvent event) throws IOException {

        VBox feedback = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/main/Feedback.fxml")));
        borderPane.setCenter(feedback);
    }

    @FXML
    public void searchClicked(MouseEvent event) throws IOException {

        ScrollPane search = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/search/Search.fxml")));
        borderPane.setCenter(search);
    }

    @FXML
    void searchChange(KeyEvent event) throws IOException{
        if (!searchTF.getText().trim().equals("")){
            ScrollPane search = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/search/SearchResult.fxml")));
            borderPane.setCenter(search);

            // string search begin
            // SearchResultController.stringSearch(searchTF.getText().trim());

            // test string split search
            SearchResultController.stringSplitSearch(searchTF.getText().trim());
        }
        else{
            ScrollPane search = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/search/Search.fxml")));
            borderPane.setCenter(search);
        }
    }

    @FXML
    public void modeClicked(MouseEvent event) {

        // user can't change anything unless they log in
//        if (DbUtils.getRetrievedID() != 0) {
//            DbUtils.updateTheme(event, DbUtils.getRetrievedID());
//        }
//        if (!DbUtils.isRetrievedTheme()) {
//            setDarkMode();
//        } else {
//            setLightMode();
//        }
    }


    // get podcast by ID from list


    public static void setLightMode() {
        staticMainPane.getStylesheets().remove((Objects.requireNonNull(MainFormController.class.getResource("/css/darkMode.css"))).toString());
        staticMainPane.getStylesheets().add((Objects.requireNonNull(MainFormController.class.getResource("/css/style.css"))).toString());
        staticModelLabel.setText("Light Mode");
        System.out.println("Light");
    }

    public static void setDarkMode() {
        staticMainPane.getStylesheets().add((Objects.requireNonNull(MainFormController.class.getResource("/css/style.css"))).toString());
        staticMainPane.getStylesheets().add((Objects.requireNonNull(MainFormController.class.getResource("/css/darkMode.css"))).toString());
        staticModelLabel.setText("Dark Mode");
        System.out.println("Dark");
    }
}