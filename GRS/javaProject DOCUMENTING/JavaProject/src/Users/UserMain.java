package Users;

import NetworkRelatedClass.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.ArrayList;

/**UserMain is a client class which launches the application for
 * the use of clients or users and loads necessary fxml pages for them.
 *
 *
 *  There are two kinds of users...
 *  1. "normal" users who can submit their problems of their living area and
 *  2."Elite Users" who hold the power to change the status of any work based on normal user's
 *  submitted problem.they are the elected members by government
 *
 * Users have no direct accsess to any data from database.
 * Problems submitted by users and all information related to them are given to them.
 * it loads 3 pages "userHomePage.fxml","signUp.fxml" and "signIn.fxml" , which helps visualization of the data
 * stored in the database by showing all problems details and user info.
 **/

public class UserMain extends Application {
    //the stage where .fxml files are loaded
    private Stage stage;


    //these are controllers which controls the .fxml  files
    private SignUpController signUpController;
    private SignInController signInController;
    private UserHomePageController userHomePageController;


    //server's IP adreess must be known to a user to set a connection
    private  String serverAddress = "127.0.0.1";
    //server's port number where it opened a socket
    private int serverPort = 33333;
    //this contains socket through which server and cient is connected
    private NetworkUtil clientNetworkUtil;


    //username is stored here after success login for further use
    private String username=null;
    //user's address like his village,union council,upazilla,NID is stored here to make a profile of user
    private UserAddress userAddress=new UserAddress();
    //eliteUser's address like his Designation,working area,NID is stored here to make a profile of eliteuser
    private EliteUsersProfile eliteUsersProfile=null;


    //reference to arrayList of problems of user's village or eliteUser's working area
    private ArrayList<ProblemsOfVillage> problemsOfVillagesStatistics=null;
    //reference to arrayList of problems of user's district
    private ArrayList<DistrictData> treeViewArrayList=null;
    //reference to arrayList of eliteUser's working area
    private ArrayList<ProblemsOfVillage> eliteTableArrayList = null;
    //a boolean to know whether a user is elite or not
    private boolean loginAsElite ;



    @Override
    public void start(Stage primaryStage) throws Exception {
        stage=primaryStage;
        showSignInPage();
    }


    /**
     * loads the fxml , creates a scene for user's "sign up" and set it in stage
     *
     * @param check
     * @throws Exception
     */
    public void showSignUpPage(int check) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("signUp.fxml"));

        Parent root = loader.load();
        signUpController = loader.getController();
        signUpController.setMain(this,check);


        Scene scene = new Scene(root, 1600, 700);
        scene.getStylesheets().add("Users/Style.css");
        stage.setMaximized(true);
        stage.setScene(scene);
        stage.setTitle("SIGN UP");
        System.out.println(1);
        stage.show();

    }

    /** loads the fxml , creates a scene for user's "sign in" and set it in stage
     *
     * @throws Exception
     */
    public void showSignInPage() throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("signIn.fxml"));

        Parent root = loader.load();
        signInController = loader.getController();
        signInController.setUserMain(this);

        Scene scene = new Scene(root, 1600, 700);
        scene.getStylesheets().add("Users/Style.css");
        //signInController.anchorPaneSignIn.autosize();
        signInController.getAnchorPaneSignIn().getBoundsInParent();
       // s.mediaviewwidth().bind(scene.widthProperty());
     //   controller.mediaviewheight().bind(scene.heightProperty().subtract(90));
        stage.setMaximized(true);
        stage.setScene(scene);
        stage.setTitle("SIGN IN");
        stage.show();

    }


    /**loads the fxml , creates a scene for user's home page after success login and set it in stage
     *
     * @throws Exception
     */
    public void showUserHomePage() throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("userHomePage.fxml"));

        Parent root = loader.load();
        userHomePageController = loader.getController();
        userHomePageController.setUserMain(this);
        userHomePageController.selectionOfChoiceBox();

        Scene scene = new Scene(root, 1600, 700);
        scene.getStylesheets().add("Users/Style.css");
        stage.setMaximized(true);
        stage.setScene(scene);
        stage.setTitle("Together We Change");
        stage.show();

    }


    /**
     * this method is to establish the connection between server and client
     */
    public void setConnectionToServer()
    {
        clientNetworkUtil = new NetworkUtil(serverAddress, serverPort);
    }


    //return the reference to the socket of this connection
    public NetworkUtil getClientNetworkUtil()
    {
        return clientNetworkUtil;
    }


    //return reference to controllers
    public SignInController getSignInController()
    {
        return signInController;
    }
    public SignUpController getUignUpController()
    {
        return signUpController;
    }
    public UserHomePageController getUserHomePageController()
    {
        return userHomePageController;
    }


    //set-get method of username
    public void setUsername(String username) {this.username=username;}
    public String getUsername() {return username;}

    //set-get method of normal user's address
    public UserAddress getUserAddress()
    {
        return userAddress;
    }
    public void setUserAddress(String village,String upazilla,String unionCouncil,String NID)
    {
        userAddress.setUpazilla( upazilla);
        userAddress.setUnionCouncil(unionCouncil);
        userAddress.setVillage(village);
        userAddress.setDistrict("Comilla");
        userAddress.setNID(NID);
    }


    public static void main(String[] args) {
        launch(args);
    }

    public void setTreeViewArrayList(ArrayList treeViewArrayList) {
        if(this.treeViewArrayList!= null) {this.treeViewArrayList=null;this.treeViewArrayList = treeViewArrayList;}}
    public ArrayList<DistrictData> getTreeViewArrayList() {
        return treeViewArrayList;
    }

    //set-get method of a boolean to know whether the user is elite or not
    public boolean isLoginAsElite() {
        return loginAsElite;
    }
    public void setLoginAsElite(boolean loginAsElite) {
        this.loginAsElite = loginAsElite;
    }


    //set=get method of eliteUser's all information
    public EliteUsersProfile getEliteUsersProfile()
    {
        return eliteUsersProfile;
    }
    public void setEliteUsersProfile(EliteUsersProfile eliteUsersProfile) {
        if(this.eliteUsersProfile!=null)this.eliteUsersProfile = null;
        this.eliteUsersProfile = eliteUsersProfile;
    }

    //set-get method of eliteUser's working area's roblems
    public void setEliteTableArrayList(ArrayList arrayList) {
       // if(this.eliteTableArrayList != null)eliteTableArrayList = null;
        this.eliteTableArrayList = arrayList;
    }
    public ArrayList getEliteTableArrayList() {
        return eliteTableArrayList;
    }


    //set-get method of user's or eliteUser's area's problems
    public ArrayList<ProblemsOfVillage> getProblemsOfVillagesStatistics() {
        return problemsOfVillagesStatistics;
    }
    public void setProblemsOfVillagesStatistics(ArrayList<ProblemsOfVillage> problemsOfVillagesStatistics) {
     /*  if(this.problemsOfVillagesStatistics !=null){
            this.problemsOfVillagesStatistics=null;*/
        this.problemsOfVillagesStatistics = problemsOfVillagesStatistics;//}
    }

}
