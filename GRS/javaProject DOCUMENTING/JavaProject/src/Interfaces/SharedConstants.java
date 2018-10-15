package Interfaces;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * This interface is a medium of connection between  server and client.
 * they exchanges command and requests and recognises them with help of this interface.
 * It is a common interface for server and client.
 */
public interface SharedConstants {

    /**************************boolean*********************************/

    int FALSE = 0;
    int TRUE  = 1;




    /*************************INVALID*********************************/

    String INVALID_NID          = "INVALID_NID";
    String INVALID_USER_NAME    = "INVALID_USER_NAME";
    String INVALID_PASSWORD     = "INVALID_PASSWORD";
    String INVALID_VILLAGE_NAME = "INVALID_VILLAGE_NAME";
    String SuccessLogin         ="SuccessLogin";
    String FailedLogin          ="FailedLogin";
    String ACCOUNT_INFO_VERIFIED = "Provided info were confirmed.";
    String ACCOUNT_CREATED = "New account successfully created.";
    String NID_VALID = "NID exists.";
    String ACCOUNT_EXISTS = "Account already exists for this NID.";
    String PROBLEM_INSERTED = "insertion successful.";
    String PROBLEM_INSERTION_FAILED = "insertion failed.";

    String CURRENT_PASSWORD_MISMATCH = "Current Password Didn't Match.";

    String USER_NAME_UNAVAILABLE = "This user name is not available";
    String PASSWORD_UPDATED = "Password was updated successfully.";
    String USER_NAME_UPDATED = "User name was updated successfully";
    String PASSWORD_UPDATE_FAILED = "Password update failed.";
    String USER_NAME_UPDATE_FAILED = "user name update failed.";
    String DELETE_ACCOUNT = "delete account.";
    String ACCOUNT_DELETED = "account deleted.";
    String ACTION_SUCCESS = "action success.";
    String ACTION_FAILED = "action failed.";

    String SUCCESS_LOGIN_AS_ELITE = " login success as elite users.";
    String NoDataInYourVillage = "no data in your village ";
    String NoHistory = "no history ";
    String SendTreeViewData = "send tree view data";




    /**********************SIGH UP PAGE LOADING**********************/

    int NewUserSignUp      =200;
    int UserSettingsChange =202;




    /**************Sending User's Button Action To Server***********/

    String HomeButtonPressed                   ="HomeButtonPressed";
    String MyVillageButtonPressedNormal        ="MyVillageButtonPressedNormal";
    String MyVillageButtonPressedElite         ="MyVillageButtonPressedElite";
    String MyVillageButtonPressed              ="MyVillageButtonPressed";
    String MyHistoryButtonPressed              ="MyHistoryButtonPressed";
    String NotificationsButtonPressed          ="NotificationsButtonPressed";
    String LogoutButtonPressed                 ="LogoutButtonPressed";
    String CreateAnAccountButtonPressed        ="CreateAnAccountButtonPressed";
    String SubmitButtonAtSignInPressed         ="SubmitButtonAtSignInPressed";
    String SubmitButtonAtSettingsChangePressed ="SubmitButtonAtSettingsChangePressed";
    String SubmitButtonToComplainPressed       ="SubmitButtonToComplainPressed";
    String ComplainBoxButtonPressed            ="ComplainBoxButtonPressed ";
    String StatisticsButtonPressed             ="StatisticsButtonPressed";
    String SuccessStoryButtonPressed           ="SuccessStoryButtonPressed";

    String NoProblemsSubmitted = "NoProblemsSubmitted";
    String GiveVote="GiveVote";
    String UndoVote="UndoVote";
    String WORKING_STATUS_CHANGE_ACTION="WORKINGSTATUSCHANGEACTION";


    /********************ObservableListOfProblemType******************/

    ObservableList<String> observableListOfProblemType = FXCollections.observableArrayList("Road and Highway", "Health", "Natural Disaster", "Electricity", "Gas", "Water","Social Security","Education","Sanitation","Others");
    ObservableList<String> observableListOfProblemScope = FXCollections.observableArrayList("Village","Union","Upajilla","District");
    ObservableList<String> observableListOfWorkingStatus = FXCollections.observableArrayList("pending","working","completed");





}
