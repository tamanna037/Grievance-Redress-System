package NetworkRelatedClass;

import java.io.Serializable;

/**
 * This class is used to send normal user's all information to user from server
 */
public class UserAddress implements Serializable{
    private String username;
    private String village;
    private String unionCouncil;
    private String upazilla;
    private String district;
    private String NID;

    public UserAddress(){}


    public UserAddress( String username,String village, String unionCouncil, String upazilla,String NID) {
        this.username=username;
        this.village = village;
        this.unionCouncil = unionCouncil;
        this.upazilla = upazilla;
        this.setNID(NID);
    }


    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getVillage() {
        return village;
    }
    public void setVillage(String village) {
        this.village = village;
    }

    public String getUnionCouncil() {
        return unionCouncil;
    }
    public void setUnionCouncil(String unoinCouncil) {
        this.unionCouncil = unoinCouncil;
    }

    public String getUpazilla() {
        return upazilla;
    }
    public void setUpazilla(String upazilla) {
        this.upazilla = upazilla;
    }

    public String getDistrict() {
        return district;
    }
    public void setDistrict(String district) {
        this.district = district;
    }

    public String getNID() {
        return NID;
    }
    public void setNID(String NID) {
        this.NID = NID;
    }
}
