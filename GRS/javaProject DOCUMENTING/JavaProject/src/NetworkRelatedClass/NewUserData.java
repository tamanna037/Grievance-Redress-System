package NetworkRelatedClass;

import javafx.beans.property.SimpleStringProperty;

import java.io.Serializable;

/**
 * This class is used to send data of new user willing to create an account from user to server
 */
public class NewUserData implements Serializable {

    private String username;
    private String password;
    private String NID;
    private String village;

    public NewUserData (String NID,String username,String password,String village)   {

        this.NID=NID;
        this.username=username;
        this.password=password;
        this.village=village;

    }

    public void setNID(String NID) {
        this.NID=NID;
    }
    public String getNID() {
        return NID;
    }

    public String getusername() {
        return username;
    }
    public void setusername(String username) {
        this.username=username;
    }

    public void setPassword(String password) {this.password=password;}
    public String getPassword() {
        return password;
    }

    public void setVillage(String village) {
        this.village=village;
    }
    public String getVillage() {
        return village;
    }



}
