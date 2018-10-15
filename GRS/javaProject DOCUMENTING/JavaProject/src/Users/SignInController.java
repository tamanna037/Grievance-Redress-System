package Users;

import NetworkRelatedClass.NetworkUtil;
import NetworkRelatedClass.SettingsChanges;
import NetworkRelatedClass.UserLoginChecking;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import Interfaces.SharedConstants;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

/**
 * It is the controller class of "signIn.fxml".
 * It has a textfield and a passwordField.
 * It has a also a "submit" button.
 * After pressing the button it connects client to server and sends username and password.
 */
public class SignInController implements SharedConstants  {

    public TextField usernameSignIn;
    public Button submit;
    public PasswordField passwordSignIn;
    public AnchorPane anchorPaneSignIn;

    //object of UserLoginChecking class, takes username and password to send to server.
    private UserLoginChecking userLoginChecking;
    //this contains socket through which server and cient is connected
    private NetworkUtil clientNetworkUtil;
    //refernce to userMain class
    private UserMain userMain;

    private String userName;
    private String password;



    public void signUpAtSignInAction(ActionEvent actionEvent) {
        try {
            // switch to "signUp.fxml"
           userMain.showSignUpPage(NewUserSignUp);
        }catch (Exception e)
        {
            System.out.println("Error loading SignUp page");
        }
    }


    //After pressing the button it connects client to server and sends username and password.
    public void submitAction(ActionEvent actionEvent) {
        try {
            connecting();
            userName = usernameSignIn.getText();
            password = passwordSignIn.getText();
            userMain.setUsername(userName);
            userLoginChecking=new UserLoginChecking(userName,password);
            new WriteThreadClient(clientNetworkUtil,userLoginChecking);
           // clientNetworkUtil.write(userLoginChecking);
            new ReadThreadClient(clientNetworkUtil,userMain);

        }catch (Exception e)
        {
            System.out.println("Error loading Home page");
        }
    }



    public String getUsername()
    {
        return userName;
    }
    public String getPassword() {return password;}
    public AnchorPane getAnchorPaneSignIn() {return anchorPaneSignIn;}

    public void setUserMain(UserMain userMain) {this.userMain = userMain;}
    public NetworkUtil getClientNetworkUtil()
    {
        return clientNetworkUtil;
    }


    public void connecting()
    {
        userMain.setConnectionToServer();
        clientNetworkUtil=userMain.getClientNetworkUtil();
    }


    public void signInAtSignInAction(ActionEvent actionEvent) {
    }



}
