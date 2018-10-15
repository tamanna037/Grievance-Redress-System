package Authority;



import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**AuthorityMain is a server class which launches the application for
 * the use of authority or admin panel and loads necessary fxml pages for server
 *
 * Server (Authority) has accsess to all data from database and user has no direct access to data.
 * Server will provide data to users.
 * Server has all information related to users and problems submitted by them.
 * it loads a page "serverHomePage.fxml" which helps visualization of the data
 * stored in the database by showing all problems details and user info.
 *
 * This class must be run in the very beginning to initiate the program.
 *
 * Only server has access to this class
 **/

public class AuthorityMain extends Application {
    //the stage where severHomePage.fxml is loaded is saved here
    private Stage stage;
    //the controller which controls the serverHomePage.fxml is kept saved here
    private ServerHomePageController serverHomePageController;

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage=primaryStage;
        showServerHomePage();

        //At the start of the server page, a tree view is also loaded
        serverHomePageController.showTreeViewServer();

    }

    //loads the fxml , creates a scene and set it in stage
    public void showServerHomePage() throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("serverHomePage.fxml"));

        Parent root = loader.load();
        serverHomePageController = loader.getController();
        serverHomePageController.setServerMain(this);
        serverHomePageController.showTableViewForHome();

        Scene scene = new Scene(root, 1600, 700);
        scene.getStylesheets().add("Users/Style.css");

        stage.setMaximized(true);
        stage.setScene(scene);
        stage.setTitle("Together We Change");
        stage.show();

    }


    public ServerHomePageController getServerHomePageController()
    {
        return serverHomePageController;
    }

    public static void main(String[] args) {
        launch(args);

    }
}

