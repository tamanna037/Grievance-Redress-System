package Users;

import CommonClass.AlertClass;
import Interfaces.SharedConstants;
import NetworkRelatedClass.*;
import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.util.ArrayList;


/**this class reads the data sent to the client from server in a thread
 *
 * this class reads objects from output stream and cast them
 * in appropriate class object. the objects gives data to client
 * from database as requested
 * this class implements Runnable and whenever a new object of this class is created a new thread starts.
 *
 **/

public class ReadThreadClient implements Runnable, SharedConstants {

    //this contains socket through which server and cient is connected
    private NetworkUtil nc;
    //reference to the UserMain of the running application
    private UserMain userMain;
    //through this object, different methods of AlertClass are called
    private AlertClass alert = new AlertClass();
    //saves the server's reply after client requests for any data.
    private String serverReply ="null";


    // this constructor of this class takes the references of the usermain and networkUtil Class
    public ReadThreadClient(NetworkUtil nc,UserMain userMain) {
        this.nc = nc;
        this.userMain=userMain;
        openThread();
    }



    //new thread opens
    public void openThread()
    {
        Thread thr = new Thread(this);
        thr.start();
    }


    public void run() {
        try {
            while(true) {

                Object object=nc.read();

                if(object!= null) {

                 //According to type of object,differnt type cast are done here
                    if(object instanceof String)
                    {
                        String string=(String)object;

                        if(string.equals(SuccessLogin)){


                            /**if "sign in" is successful, server sends SuccessLogin..
                            *after this confirmation,users are allowed to go to the home page
                             */

                            System.out.println("client" + SuccessLogin);
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                try {

                                    userMain.setLoginAsElite(false);
                                    userMain.showUserHomePage();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                        }

                        else if(string.equals(SUCCESS_LOGIN_AS_ELITE)){

                            /**if "sign in" is successful and the user is Elite, server sends SUCCESS_LOGIN_AS_ELITE..
                            * after this confirmation,elite Users are allowed to go to the home page
                            */
                            System.out.println("login as elite.");
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    userMain.setLoginAsElite(true);
                                    try {
                                        userMain.showUserHomePage();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });

                        }

                        else if(string.equals(FailedLogin)){

                            /**if "sign in" is unsuccessful , server sends FailedLogin
                            *after this declination,Users are not allowed to go to the home page
                            */
                            System.out.println(FailedLogin);
                            setServerReply(FailedLogin);
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    alert.showErrorAlert("Login Failed. Your Username or password is incorrect");
                                }
                            });

                        }

                        else if(string.equals(INVALID_NID)){

                            /**
                             * if given NID is invalid, gives an alert to user
                             *
                             */
                            System.out.println(INVALID_NID);
                            setServerReply(INVALID_NID);
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    alert.showErrorAlert("Invalid NID");
                                }
                            });

                        }

                        else if(string.equals(INVALID_USER_NAME)){

                            /**i
                             * f username is invalid or used before by another user, gives an alert to user
                             *
                             */

                            System.out.println(INVALID_USER_NAME);
                            setServerReply(INVALID_USER_NAME);
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    alert.showErrorAlert("This Username is already used");
                                }
                            });

                        }

                        else if(string.equals(INVALID_VILLAGE_NAME)) {

                            /**
                             * if village is invalid, gives an alert to user
                             *
                             */
                            System.out.println(INVALID_VILLAGE_NAME);
                            setServerReply(INVALID_VILLAGE_NAME);
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    alert.showErrorAlert("Invalid Village Name");
                                }
                            });
                        }

                        else if(string.equals(NoDataInYourVillage)) {

                            /**
                             * if there is no data in user's village,server sends this
                             *
                             */
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    Platform.runLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            userMain.setProblemsOfVillagesStatistics(null);
                                        }
                                    });
                                    System.out.println(NoDataInYourVillage);
                                    Alert alert = new Alert(Alert.AlertType.ERROR);
                                    alert.setTitle("Data Not found");
                                    alert.setHeaderText("No Data in your area");
                                    alert.showAndWait();

                                }
                            });

                        }

                        else if(string.equals(NoHistory)) {

                            /**
                             * if there is no previous activity of user,server sends this
                             *
                             */

                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {

                                    System.out.println(NoHistory);
                                    Alert alert = new Alert(Alert.AlertType.ERROR);
                                    alert.setTitle("Data Not found");
                                    alert.setHeaderText("No history to show");
                                    alert.showAndWait();

                                }
                            });

                        }

                        else if(string.equals(NoProblemsSubmitted)) {

                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {

                                    System.out.println("noProblems submitted");
                                    userMain.getUserHomePageController().setVillageProblemsInTextArea(null);
                                }
                            });

                        }

                        else if(string.equals(ACCOUNT_CREATED)){

                            /**
                             * if "sign up" is successful, server sends ACCOUNT_CREATED
                             * after this confirmation,users are allowed to go to the home page
                             */
                            System.out.println(ACCOUNT_CREATED);

                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    try{

                                        userMain.showUserHomePage();
                                        alert.showConfirmationAlert("You have successfully created your account");
                                    }catch (Exception e)
                                    {
                                        e.printStackTrace();
                                    }

                                }
                            });


                        }

                        else if(string.equals(ACCOUNT_EXISTS)){

                            /**if in "sign up" account already exists in given NID, server sends ACCOUNT_EXISTS
                            *  gives an alert to users
                             *  */

                            System.out.println(ACCOUNT_EXISTS);
                            setServerReply(ACCOUNT_EXISTS);
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    try{

                                        alert.showErrorAlert("Account Already Exists");
                                    }catch (Exception e)
                                    {
                                        e.printStackTrace();
                                    }

                                }
                            });


                        }

                        else if(string.equals(CURRENT_PASSWORD_MISMATCH))
                        {
                            /**
                             * if given password is incorrect while signing in , gives an error alert
                             *
                             */
                            System.out.println(CURRENT_PASSWORD_MISMATCH);
                            setServerReply(CURRENT_PASSWORD_MISMATCH);
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    try{

                                        alert.showErrorAlert("Given password is not correct");
                                    }catch (Exception e)
                                    {
                                        e.printStackTrace();
                                    }

                                }
                            });
                        }

                    }//string end


                    else if(object instanceof UserAddress)
                    {
                        /**
                         * user's full address to set to userMain
                         *
                         */
                        UserAddress userAddress = (UserAddress)object;

                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                userMain.setUserAddress(userAddress.getVillage(),userAddress.getUpazilla(),
                                        userAddress.getUnionCouncil(),userAddress.getNID());
                                System.out.println("upazilla" + userMain.getUserAddress().getUpazilla() + "village"+userMain.getUserAddress().getVillage()+userMain.getUserAddress().getNID());

                            }
                        });
                    }



                    else if(object instanceof EliteUsersProfile)
                    {
                        /**
                         * Elite user's full address to set to userMain
                         *
                         */
                        EliteUsersProfile eliteUsersProfile = (EliteUsersProfile)object;

                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                userMain.setEliteUsersProfile(eliteUsersProfile);
                                System.out.println(eliteUsersProfile.getUsername());

                            }
                        });
                    }

                    //arraylist start
                    else if(object instanceof ArrayList)
                    {
                        ArrayList arrayList=(ArrayList)object;


                        if(arrayList.get(0) instanceof Problems)
                        {
                            //user's all submitted problem to set in history text area
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    userMain.getUserHomePageController().setHistoryInTextArea(arrayList);
                                }
                            });
                        }

                        else if(arrayList.get(0) instanceof ProblemsOfVillage)
                        {
                            /**
                             * all problems of user's village / elite Users's working area
                             */

                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    if(userMain.isLoginAsElite()) {
                                        userMain.getUserHomePageController().showEliteTableView(arrayList);
                                        userMain.setEliteTableArrayList(arrayList);
                                    }
                                    else
                                        userMain.setProblemsOfVillagesStatistics(arrayList);
                                        userMain.getUserHomePageController().setVillageProblemsInTextArea(arrayList);
                                }
                            });


                        }else if(arrayList.get(0) instanceof DistrictData){

                            /**
                             * all problems of user's District
                             */
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    userMain.getUserHomePageController().showTreeViewUser(arrayList);

                                }
                            });

                        }

                    }//arraylist end





                }
            }
        } catch(Exception e) {
            System.out.println (e);
        }
        nc.closeConnection();
    }

    public String  getServerReply(){
        return serverReply;
    }
    public void setServerReply(String serverReply) {
        this.serverReply = serverReply;
    }
}




