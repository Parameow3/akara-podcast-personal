/*-----------------------------------------------------------------------------------------
 * NAME : DiscoverController.java
 * VER  : v0.1
 * PROJ : Akara
 * CODE CLEAN? : Yes
 *-----------------------------------------------------------------------------------------
 *                      H      I      S      T      O      R      Y
 *-----------------------------------------------------------------------------------------
 *   DATE        AUTHOR         DESCRIPTION
 * ----------  --------------  ------------------------------------------------------------
 * 2022-06-24   Nuth Vireak     creation
 * ----------  --------------  ------------------------------------------------------------
 * 2022-08-03   Nuth Vireak     Modification
 *---------------------------------------------------------------------------------------*/

package controller.discover;

import com.github.javafaker.Faker;
import controller.main.MainFormController;
import controller.main.PodcastHboxController;
import controller.main.PodcastVboxController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;
import model.Podcast;
import model.RecentlyPlayed;
import model.api.desktop.model.AccessToken;
import model.api.desktop.model.Credential;
import model.api.desktop.model.RefreshToken;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;


public class DiscoverController implements Initializable {

    //------------------------------------------------------------------------------------
    // fields declaration                                                               |
    //------------------------------------------------------------------------------------

    @FXML
    private BorderPane borderPane;


    @FXML
    private HBox recentlyPlayedContainer;

    @FXML
    private HBox popularPodcastContainer;

    @FXML
    private HBox topPodcastInGamingContainer;

    @FXML
    private HBox topPodcastInTechnologyContainer;

    @FXML
    private HBox topPodcastInHistoryContainer;

    @FXML
    private HBox topPodcastInComedyContainer;

    @FXML
    private HBox topPodcastInProgrammingLanguageContainer;


    @FXML
    private Label seeAllPopularPodcast;

    @FXML
    private Label seeAllTopPodcastInGaming;

    @FXML
    private Label seeAllTopPodcastInTechnology;

    @FXML
    private Label seeAllTopPodcastInHistory;

    @FXML
    private Label seeAllTopPodcastInComedy;

    @FXML
    private Label seeAllTopPodcastInProgrammingLanguage;


    public static List<RecentlyPlayed> recentlyPlayed;
    public static List<Podcast> popularPodcast;
    public static List<Podcast> topPodcastInGaming;
    public static List<Podcast> topPodcastInTechnology;
    public static List<Podcast> topPodcastInHistory;
    public static List<Podcast> topPodcastInComedy;
    public static List<Podcast> topPodcastInProgrammingLanguage;


    // anchor pane
    private AnchorPane popularPane;
    private AnchorPane topGamingPane;
    private AnchorPane topTechPane;
    private AnchorPane topHistoryPane;
    private AnchorPane topComedyPane;
    private AnchorPane topProgrammingPane;

    //------------------------------------------------------------------------------------
    //  Methods declaration                                                              |
    //------------------------------------------------------------------------------------

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        // always update recentlyPlayed panel
        recentlyPlayed = new ArrayList<>(getRecentlyPlayed());
        recentlyPlayedContainer.getChildren().clear();
        try {
            for (RecentlyPlayed podcast : recentlyPlayed) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/view/main/podcastHbox.fxml"));

                HBox hBox = fxmlLoader.load();

                PodcastHboxController podcastHboxController = fxmlLoader.getController();
                podcastHboxController.setData(podcast);

                recentlyPlayedContainer.getChildren().add(hBox);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // one init is enough
        if (!MainFormController.init) {
            System.out.println("Do it!");
            // Initialize the lists of podcasts to be displayed in the Discover page

            popularPodcast = new ArrayList<>(getPopularPodcast());
//            topPodcastInGaming = new ArrayList<>(getTopPodcastInGaming());
//            topPodcastInTechnology = new ArrayList<>(getTopPodcastInTechnology());
//            topPodcastInHistory = new ArrayList<>(getTopPodcastInHistory());
//            topPodcastInComedy = new ArrayList<>(getTopPodcastInComedy());
//            topPodcastInProgrammingLanguage = new ArrayList<>(getTopPodcastInProgrammingLanguage());

            try {

                for (Podcast podcast : MainFormController.podcastList) {
                    popularPane = getAnchorPane(podcast);
                    popularPodcastContainer.getChildren().add(popularPane);
                }

//                for (Podcast podcast : MainFormController.podcastList) {
//                    topGamingPane = getAnchorPane(podcast);
//                    topPodcastInGamingContainer.getChildren().add(topGamingPane);
//                }
//
//                for (Podcast podcast : MainFormController.podcastList) {
//                    topTechPane = getAnchorPane(podcast);
//                    topPodcastInTechnologyContainer.getChildren().add(topTechPane);
//                }
//
//                for (Podcast podcast : MainFormController.podcastList) {
//                    topHistoryPane = getAnchorPane(podcast);
//                    topPodcastInHistoryContainer.getChildren().add(topHistoryPane);
//                }
//
//                for (Podcast podcast : MainFormController.podcastList) {
//                    topComedyPane = getAnchorPane(podcast);
//                    topPodcastInComedyContainer.getChildren().add(topComedyPane);
//                }
//
//                for (Podcast podcast : MainFormController.podcastList) {
//                    topProgrammingPane = getAnchorPane(podcast);
//                    topPodcastInProgrammingLanguageContainer.getChildren().add(topProgrammingPane);
//                }

            } catch (IOException e) {
                e.printStackTrace();
            }


            MainFormController.init = true;
        }

    }



    //#region PODCAST_ON_DISCOVER



    private List<RecentlyPlayed> getRecentlyPlayed() {
        File recentFile = new File("src/podcastData/recentPlayed.txt");

        List<RecentlyPlayed> recentlyPlayedList = new ArrayList<>();

        if (recentFile.isFile()) {
            try {
                if (new BufferedReader(new FileReader(recentFile)).readLine() == null) {
                    System.out.println("No recently played found!");
                } else {
                    System.out.println("Found!");
                    // check to make sure it's not duplicate
                    FileInputStream fi = new FileInputStream(recentFile);
                    ObjectInputStream oi = new ObjectInputStream(fi);

                    List<RecentlyPlayed> recentlyPlayed = (List<RecentlyPlayed>) oi.readObject();
                    recentlyPlayedList.addAll(recentlyPlayed);

                    for (RecentlyPlayed recentlyPlayed1 : recentlyPlayedList) {
                        System.out.println(recentlyPlayed1.toString());
                    }
                    oi.close();
                    fi.close();
                }

            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        } else { // create a file if it's not exist

            try {
                if (recentFile.createNewFile()) {
                    System.out.println("File created: " + recentFile.getName());
                } else {
                    System.out.println("File already exists.");
                }
            } catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
        }

        return recentlyPlayedList;
    }

    private static List<Podcast> getPopularPodcast() {

        List<Podcast> popularPodcast = new ArrayList<>();

        for (Podcast podcast : MainFormController.podcastList) {
            if (podcast.getViewed() > 5000) {
                if (popularPodcast.size() < 10) {
                    popularPodcast.add(podcast);
                }
            }
        }
        return popularPodcast;
    }

    private List<Podcast> getTopPodcastInGaming() {

        List<Podcast> topPodcastInGaming = new ArrayList<>();

        for (Podcast podcast : MainFormController.podcastList) {
            if (podcast.getPodcastCategoryName().equals("Gaming")) {
                if (topPodcastInGaming.size() < 10) {
                    topPodcastInGaming.add(podcast);
                }
            }
        }
        return topPodcastInGaming;
    }

    private List<Podcast> getTopPodcastInTechnology() {

        List<Podcast> topPodcastInTechnology = new ArrayList<>();

        for (Podcast podcast : MainFormController.podcastList) {
            if (podcast.getPodcastCategoryName().equals("Technology")) {
                if (topPodcastInTechnology.size() < 10) {
                    topPodcastInTechnology.add(podcast);
                }
            }
        }
        return topPodcastInTechnology;
    }

    private List<Podcast> getTopPodcastInHistory() {

        List<Podcast> topPodcastInHistory = new ArrayList<>();

        for (Podcast podcast : MainFormController.podcastList) {
            if (podcast.getPodcastCategoryName().equals("History")) {
                if (topPodcastInHistory.size() < 10) {
                    topPodcastInHistory.add(podcast);
                }
            }
        }
        return topPodcastInHistory;
    }

    private List<Podcast> getTopPodcastInComedy() {

        List<Podcast> topPodcastInComedy = new ArrayList<>();

        for (Podcast podcast : MainFormController.podcastList) {
            if (podcast.getPodcastCategoryName().equals("Comedy")) {
                if (topPodcastInComedy.size() < 10) {
                    topPodcastInComedy.add(podcast);
                }
            }
        }
        return topPodcastInComedy;
    }

    private List<Podcast> getTopPodcastInProgrammingLanguage() {

        List<Podcast> topPodcastInProgrammingLanguage = new ArrayList<>();

        for (Podcast podcast : MainFormController.podcastList) {
            if (podcast.getPodcastCategoryName().equals("Programming")) {
                if (topPodcastInProgrammingLanguage.size() < 10) {
                    topPodcastInProgrammingLanguage.add(podcast);
                }
            }
        }
        return topPodcastInProgrammingLanguage;
    }

    @FXML
    void seeAllClick(MouseEvent event) throws Exception {

        Faker faker = new Faker();

        BorderPane discoverSeeAll = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/discover/DiscoverSeeAll.fxml")));
        MainFormController.staticBorderpane.setCenter(discoverSeeAll);

        if (event.getSource() == seeAllPopularPodcast) {

            System.out.println("See all popular podcasts");
            String title = "Popular Podcasts";
            String description = faker.lorem().paragraph(10);

            System.out.println("Title: " + title);

            setTitleAndDescriptionToDiscoverSeeAllStaticView(title, description);

            DiscoverSeeAllController.setPopularPodcastToView();

        } else if (event.getSource() == seeAllTopPodcastInGaming) {

            System.out.println("See all Top podcast in gaming");
            String title = "Top Podcast in Gaming";
            String description = faker.lorem().paragraph(10);

            System.out.println("Title: " + title);

            setTitleAndDescriptionToDiscoverSeeAllStaticView(title, description);

            DiscoverSeeAllController.setTopPodcastInGamingToView();
        } else if (event.getSource() == seeAllTopPodcastInTechnology) {

            System.out.println("See all Top podcast in Technology");
            String title = "Top Podcast in Technology";
            String description = faker.lorem().paragraph(10);

            System.out.println("Title: " + title);

            setTitleAndDescriptionToDiscoverSeeAllStaticView(title, description);

            DiscoverSeeAllController.setTopPodcastInTechnologyToView();
        } else if (event.getSource() == seeAllTopPodcastInHistory) {

            System.out.println("See all Top podcast in History");
            String title = "Top Podcast in History";
            String description = faker.lorem().paragraph(10);

            System.out.println("Title: " + title);

            setTitleAndDescriptionToDiscoverSeeAllStaticView(title, description);

            DiscoverSeeAllController.setTopPodcastInHistoryToView();
        } else if (event.getSource() == seeAllTopPodcastInComedy) {

            System.out.println("See all Top podcast in Comedy");
            String title = "Top Podcast in Comedy";
            String description = faker.lorem().paragraph(10);

            System.out.println("Title: " + title);

            setTitleAndDescriptionToDiscoverSeeAllStaticView(title, description);

            DiscoverSeeAllController.setTopPodcastInComedyToView();
        } else if (event.getSource() == seeAllTopPodcastInProgrammingLanguage) {

            System.out.println("See all Top podcast in Programming Language");
            String title = "Top Podcast in Programming Language";
            String description = faker.lorem().paragraph(10);

            System.out.println("Title: " + title);

            setTitleAndDescriptionToDiscoverSeeAllStaticView(title, description);

            DiscoverSeeAllController.setTopPodcastInProgrammingLanguageToView();
        }
    }

    //#endregion

    private void setTitleAndDescriptionToDiscoverSeeAllStaticView(String title, String description) {

        DiscoverSeeAllController.setTitleDiscoverSeeAllStatic(title);
        DiscoverSeeAllController.setDescriptionSeeAllStatic(description);
    }

    private AnchorPane getAnchorPane(Podcast podcast) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/view/main/podcastVbox.fxml"));

        AnchorPane anchorPane = fxmlLoader.load();
        PodcastVboxController podcastVboxController = fxmlLoader.getController();
        podcastVboxController.setData(podcast);
        return anchorPane;
    }

} // end of class DiscoverController
