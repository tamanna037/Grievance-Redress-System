package NetworkRelatedClass;

import java.io.Serializable;

/**
 * This class is used to send user's unionCouncil,upazilla & village to user from server
 */
public class DistrictData implements Serializable{

    private String village;
    private String unionCouncil;
    private String upazilla;

    public DistrictData(String village, String unionCouncil, String upazilla) {
        this.village = village;
        this.unionCouncil = unionCouncil;
        this.upazilla = upazilla;
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
    public void setUnionCouncil(String unionCouncil) {
        this.unionCouncil = unionCouncil;
    }

    public String getUpazilla() {
        return upazilla;
    }
    public void setUpazilla(String upazilla) {
        this.upazilla = upazilla;
    }
}
