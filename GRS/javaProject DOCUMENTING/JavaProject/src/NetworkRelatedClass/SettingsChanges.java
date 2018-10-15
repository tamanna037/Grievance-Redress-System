package NetworkRelatedClass;

import java.io.Serializable;

/**
 * This class is used to send data of user willing to change settings from user to server
 */
public class SettingsChanges implements Serializable{
    private String newUserName;
    private String currentUserName;


    private String newPassword;
    private String currentPassword;
    private boolean isLoginElite;

    public SettingsChanges(String newUserName, String currentUserName, String newPassword, String currentPassword,boolean isLoginElite) {
        this.newUserName = newUserName;
        this.currentUserName = currentUserName;
        this.newPassword = newPassword;
        this.currentPassword = currentPassword;
        this.setIsLoginElite(isLoginElite);
    }

    public String getNewUserName() {
        return newUserName;
    }
    public void setNewUserName(String newUserName) {
        this.newUserName = newUserName;
    }

    public String getCurrentUserName() {
        return currentUserName;
    }
    public void setCurrentUserName(String currentUserName) {
        this.currentUserName = currentUserName;
    }

    public String getNewPassword() {
        return newPassword;
    }
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getCurrentPassword() {
        return currentPassword;
    }
    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public boolean isLoginElite() {
        return isLoginElite;
    }
    public void setIsLoginElite(boolean isLoginElite) {
        this.isLoginElite = isLoginElite;
    }
}
