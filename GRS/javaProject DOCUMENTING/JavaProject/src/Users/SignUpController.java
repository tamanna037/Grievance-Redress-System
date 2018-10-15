package Users;

import DatabasePackage.Database;
import CommonClass.AlertClass;
import NetworkRelatedClass.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import Interfaces.SharedConstants;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Optional;

/**
 * It is the controller class of "signUp.fxml".
 * It takes necessary data from client to create an account.
 * It has a "create Account" button.
 * After pressing the button it connects client to server and sends username and password.
 * This scene is also loaded to change settings or password.
 * It has a "submitChanges"  button and sends new password to server.
 *
 */
public class SignUpController implements SharedConstants {

    public Button createAnAccount;
    public Button deleteAccount;
    public Button submitChanges;

    public ToggleButton signUpAtSignUp;
    public ToggleButton signInAtSignUp;
    public ToggleButton backButton;

    public Label usernameLabelSignUp;
    public Label passwordLabelSignUp;
    public Label nidNumberLabelSignUp;
    public Label villageLabelSignUp;
    public Label retypePasswordLabelSignUp;

    public TextField nidNumberSignUp;
    public TextField usernameSignUp;
    public TextField villageSignUp;
    public PasswordField retypePasswordSignUp;
    public PasswordField passwordSignUp;


    private UserMain userMain;

    /**
     * To  know whether the user wants to sign up or change settings , check is used
     *
     */
    private int check;
    private NetworkUtil clientNetworkUtil;
    private NewUserData newUserData;
    private String userName;
    private String password;
    private String retypePassword;
    private String village;
    private String nid;
    private AlertClass alert = new AlertClass();


    //loads the signIn.fxml page
    public void signInAtSignUpAction(ActionEvent actionEvent) {
        try {
            userMain.showSignInPage();
        }catch (Exception e)
        {
            System.out.println("Error loading SignIn page");
        }

    }

    /**connects client to server and sends username and password.
     *
     * @param actionEvent
     */
    public void createAnAccountAction(ActionEvent actionEvent) {
        try {
            connecting();
            nid = nidNumberSignUp.getText();
            userName = usernameSignUp.getText();
            password = passwordSignUp.getText();
            retypePassword = retypePasswordSignUp.getText();
            village = villageSignUp.getText().toLowerCase();
            userMain.setUsername(userName);

            if (userName.length()==0 || nid.length()==0 || password.length()==0 || village.length()==0 ) alert.showErrorAlert("Please Fill Up all the fields");
            else if(nid.length()!=17) alert.showErrorAlert("Invalid NID.");
            else if(userName.length()>20)alert.showErrorAlert("Too long username.");
            else if(password.length()<5) alert.showErrorAlert("Password less than 5 character.");
            else if(!retypePassword.equals(password))   alert.showErrorAlert("Password not matched");
            else
            {
                newUserData=new NewUserData(nid,userName,password,village);
               // clientNetworkUtil.write(newUserData);
                new WriteThreadClient(clientNetworkUtil,newUserData);
                new ReadThreadClient(clientNetworkUtil,userMain);
            }
        }catch (Exception e)
        {
            System.out.println("Error loading Home page");
        }

    }

    /**
     * return user to userHomePage from settings Change Page
     * @param actionEvent
     */
    public void backButtonAction(ActionEvent actionEvent) {
        try {
            new WriteThreadClient(userMain.getClientNetworkUtil(),SendTreeViewData);
            userMain.showUserHomePage();
            userMain.getUserHomePageController().setUsername(userMain.getUsername());



        }catch (Exception e)
        {
            System.out.println("Error loading Home page");
        }
    }



    public void settingsChange()
    {
        /**
         * if check is NewUserSignUp, it creates necessary visualization to sign Up
         */
        if(check==NewUserSignUp)
        {
            signInAtSignUp.setVisible(true);
            signUpAtSignUp.setVisible(true);

            nidNumberSignUp.setVisible(true);
            nidNumberLabelSignUp.setVisible(true);

            usernameSignUp.setVisible(true);
            usernameLabelSignUp.setVisible(true);

            passwordSignUp.setVisible(true);
            passwordLabelSignUp.setVisible(true);

            retypePasswordSignUp.setVisible(true);
            retypePasswordLabelSignUp.setVisible(true);

            createAnAccount.setVisible(true);
            submitChanges.setVisible(false);
            deleteAccount.setVisible(false);
            backButton.setVisible(false);
        }

        /**
         * if check is UserSettingsChange, it creates necessary visualization to change settings
         *
         */
        else if(check==UserSettingsChange)
        {
            signInAtSignUp.setVisible(false);
            signUpAtSignUp.setVisible(false);

            nidNumberSignUp.setVisible(false);
            nidNumberLabelSignUp.setVisible(false);

            usernameSignUp.setVisible(false);
            usernameLabelSignUp.setVisible(false);

            passwordSignUp.setVisible(true);
            passwordLabelSignUp.setVisible(true);

            retypePasswordSignUp.setVisible(true);
            retypePasswordLabelSignUp.setVisible(true);

            villageLabelSignUp.setVisible(false);
            villageSignUp.setVisible(false);

            createAnAccount.setVisible(false);
            submitChanges.setVisible(true);
            deleteAccount.setVisible(false);
            backButton.setVisible(true);

            usernameLabelSignUp.setText("Change Your Username");
            passwordLabelSignUp.setText("Change Your Password");
            //createAnAccount.setText("Submit");

        }

    }

    /*
    * *a check is also passed to know which scene to load
     *
     * @param userMain
     * @param check
     */
    public void setMain(UserMain userMain,int check) {
        this.userMain = userMain;
        this.check=check;
        settingsChange();
    }


    public void signUpAtSignUpAction(ActionEvent actionEvent) {
    }


    public String getUsername()
    {
        return userName;
    }

    public String getPassword()
    {
        return password;
    }

    public void connecting() {
        userMain.setConnectionToServer();
        clientNetworkUtil=userMain.getClientNetworkUtil();
    }

    /**
     * sends new password to server
     *
     * @param actionEvent
     */
    public void submitChangesAction(ActionEvent actionEvent) {
        password = passwordSignUp.getText();
     //   System.out.println(username+password);
        if( password.equals("")){
            alert.showErrorAlert("No Changes To Submit.");
        }
        else if(!password.equals("") && !password.equals(retypePasswordSignUp.getText())){
            alert.showErrorAlert("Password Mismatch. Try Again.");
        }else if(!password.equals("") && password.length()<5){
            alert.showErrorAlert("Password Less Than 5 Character.");
        }else{

            newWindowForPasswordConfirmation();

        }
        usernameSignUp.setText(null);
        passwordSignUp.setText(null);
        retypePasswordSignUp.setText(null);
    }

    /**
     * a new stage is created to confirm password from user
     *
     */
    public void newWindowForPasswordConfirmation()
    {
        String username = usernameSignUp.getText();
        Stage stage = new Stage();
        AnchorPane root = new AnchorPane();
        PasswordField passwordField = new PasswordField();
        Button confirmPassword = new Button("Confirm Current Password");
        VBox vBox = new VBox(15,passwordField,confirmPassword);
        root.getChildren().addAll(vBox);

        confirmPassword.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                if(!passwordField.getText().equals("")){
                    SettingsChanges sc = new SettingsChanges(username,userMain.getUsername(),password,passwordField.getText(),userMain.isLoginAsElite());
                    NetworkUtil nc=userMain.getClientNetworkUtil();
                    //nc.write(sc);
                    new WriteThreadClient(nc,sc);
                    stage.close();
                }
            }
        });

        AnchorPane.setRightAnchor(vBox, 100d);
        AnchorPane.setBottomAnchor(vBox, 150d);
        Scene scene = new Scene(root, 300,300);

        stage.setTitle("Current Password Confirmation");
        stage.setScene(scene);
        stage.show();

    }



}

