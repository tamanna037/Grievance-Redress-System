package NetworkRelatedClass;

import java.io.Serializable;

/**
 * Created by MiNNiE on 12/17/2015.
 */
public class EliteUsersProfile implements Serializable{

    //This class is used to send elite user's information like NID,designation,working area to elite user from server
    private String username;
    private String NID;
    private String designation;
    private String workingArea;
    public EliteUsersProfile(String username,String NID,String designation,String workingArea)
    {
        this.username=username;
        this.NID=NID;
        this.designation=designation;
        this.workingArea=workingArea;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getNID() {
        return NID;
    }
    public void setNID(String NID) {
        this.NID = NID;
    }

    public String getDesignation() {
        return designation;
    }
    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getWorkingArea() {
        return workingArea;
    }
    public void setWorkingArea(String workingArea) {
        this.workingArea = workingArea;
    }
}
