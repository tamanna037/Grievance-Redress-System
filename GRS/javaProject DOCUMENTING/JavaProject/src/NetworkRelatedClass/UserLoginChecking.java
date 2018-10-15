package NetworkRelatedClass;

import javafx.beans.property.SimpleStringProperty;

import java.io.Serializable;

/**
 * This class is used to send data of user willing to sign in from user to server
 */
public class UserLoginChecking implements Serializable {

    private String username;
    private  String  password;

    public UserLoginChecking(String username,String password)
    {
        this.username=username;
        this.password=password;
    }

    public void setPassword(String newPassword) {
        password=newPassword;
    }
    public String getPassword() {
        return password;
    }

    public String getusername() {
        return username;
    }
    public void setusername(String newUsername) {
        username=newUsername;
    }






}
