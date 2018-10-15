package DatabasePackage;

import Interfaces.SharedConstants;
import NetworkRelatedClass.EliteUsersProfile;
import NetworkRelatedClass.Problems;
import NetworkRelatedClass.ProblemsOfVillage;
import NetworkRelatedClass.UserAddress;

import javax.xml.crypto.Data;
import java.sql.*;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * This is the medium through which server communicates with database.
 *
 * This class provides several methods to read data according to different
 * need.
 *
 * Only Server has access to this class
 */
public class Database implements SharedConstants {

    //connection with database
    private Connection conn = null;


    /**
     * establishes connection with database
     */
    public Database() {
        try {
            conn = DBConnection.connect();
        }  catch (SQLException e) {
            e.printStackTrace();
        }


    }

    /**
     * This class checks for the existance of the NID in users table.
     * if it exists then returns that account already exists.
     * else it checks for the existance of NID in NIDandName table. It
     * provides all valid NID in our Application Scope. This is important
     * because if NID is not in that table, or account already exists then user doesnot get permission to
     * create a new account.
     * if NID is not in NIDandName table then it returns invalid NID.
     *
     * @param nid users NID provided while creating account
     * @return String that tells server whether NID is valis , invalid or Account already exists for given NID
     */
    public String nidIsValid(String nid) {
        try {
            PreparedStatement statement = conn.prepareStatement("SELECT NID FROM nidandname WHERE NID = ?");
            statement.setString(1,nid);

            ResultSet rs = statement.executeQuery();

            if(rs.next()){
                rs.close();
                statement.close();

                statement = conn.prepareStatement("SELECT NID FROM users WHERE NID = ?");
                statement.setString(1,nid);
                rs = statement.executeQuery();
                if(rs.next()){
                    rs.close();
                    statement.close();
                    System.out.println("database" +
                            ACCOUNT_EXISTS);
                    return ACCOUNT_EXISTS;
                }
                else {
                    System.out.println("database" +
                            NID_VALID);
                    return NID_VALID;
                }

            }

            rs.close();
            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("database" +
                INVALID_NID);
        return INVALID_NID;
    }

    /**
     * This class checks the existance of given username in user table.
     *
     * Existance of username is checked while a user logs in for its validity.
     * And also  if a username already exists, a new user cannot use that username
     * to create his account.
     *
     * @param userName clients username
     * @return validity of that username
     */
    public boolean userNameExists(String userName) {
        try {
            PreparedStatement statement = conn.prepareStatement("SELECT userName FROM users WHERE userName = ?");
            statement.setString(1,userName);

            ResultSet rs = statement.executeQuery();

            if(rs.next()){
                rs.close();statement.close();
                System.out.println("database" +
                       "userNameExists");return true;
            }
            rs.close();statement.close();
        } catch (SQLException e) {
            System.out.println("error in userNameExists");
            e.printStackTrace();
        }
        System.out.println("database" +
                "userNameIsAvailable");
        return false;
    }

    /**
     * the code of District, upazilla, union council, village is provided in NID . THis class
     * checks whether the given village is under the District, upazilla, union council of given NID.
     * @param village village provided by the client while creating new account
     * @param nid NID provided by the client while creating new account
     * @return whether the given village is under the District, upazilla, union council of given NID.
     */
    public boolean villageIsValid(String village, String nid) {
        try {
            PreparedStatement statement = conn.prepareStatement("SELECT village,unionCouncilCode" +
                    ", upazillaCode, districtCode FROM districtdata WHERE village = ?");
            statement.setString(1,village);

            ResultSet rs = statement.executeQuery();

            if(rs.next()){

                if( rs.getString("unionCouncilCode").equals(nid.substring(9,11))
                        && rs.getString("upazillaCode").equals(nid.substring(7,9))
                        && rs.getString("districtCode").equals(nid.substring(4,6))
                        ){
                    rs.close();statement.close();
                    System.out.println("database" +
                            "villageValid");return  true;
                }

            }

            rs.close();
            statement.close();
        } catch (SQLException e) {
            System.out.println("error in villageIsValid.");
            e.printStackTrace();
        }
        System.out.println("database" +
                "village Invalid");
        return false;
    }

    /**
     * searches for username and password in users table in database.
     * checks whether provided password is the current password of given username.
     * it is checked while signing in or changing password
     * @param userName client username
     * @param password
     * @return SuccessLogin username and client account password matches, InvalidUsername is username not found in database
     *         or Invalid Password if password doesnot match the username
     */
    public String signInIsValid(String userName, String password) {

        try {

            PreparedStatement statement = conn.prepareStatement("SELECT userName, password" +
                    " FROM users WHERE userName = ?");
            statement.setString(1,userName);

            ResultSet rs = statement.executeQuery();
            if(rs.next()){
                if(rs.getString("password").equals(password)){
                    rs.close();statement.close();
                    System.out.println("database" +
                            SuccessLogin);return SuccessLogin;
                }
                else {
                    rs.close();statement.close();
                    System.out.println("database" +
                            INVALID_PASSWORD);
                    return INVALID_PASSWORD;
                }
            }
            rs.close();statement.close();
        } catch (SQLException e) {
            System.out.println("error in villageIsValid.");
            e.printStackTrace();
        }
        System.out.println("database" +
                INVALID_USER_NAME);
        return INVALID_USER_NAME;
    }

    /**
     * searches for username and password in eliteusers table in database.
     * checks whether provided password is the current password of given username.
     * it is checked while signing in or changing password.
     * @param userName client username
     * @param password client account password
     * @return SuccessLogin username and password matches, InvalidUsername is username not found in database
     *         or Invalid Password if password doesnot match the username
     */
    public String signInIsValidAsElite(String userName, String password) {

        try {
            PreparedStatement statement = conn.prepareStatement("SELECT userName, password" +
                    " FROM eliteusers WHERE userName = ?");
            statement.setString(1,userName);

            ResultSet rs = statement.executeQuery();
            if(rs.next()){
                if(rs.getString("password").equals(password)){
                    rs.close();statement.close();
                    System.out.println("database" +
                            SuccessLogin);return SuccessLogin;
                }
                else {
                    rs.close();statement.close();
                    System.out.println("database" +
                            "Elite"+ INVALID_PASSWORD);
                    return INVALID_PASSWORD;
                }
            }
            rs.close();statement.close();
        } catch (SQLException e) {
            System.out.println("error in villageIsValid.");
            e.printStackTrace();
        }
        System.out.println("database" +
                INVALID_USER_NAME);
        return INVALID_USER_NAME;
    }

    /**
     * after a new account is created this method inserts the new user data in user table
     * @param nid client nid
     * @param userName client username
     * @param password client password
     * @param village client village
     * @return true if inserting data in database is successful
     */
    public boolean addUserToUsersTable(String nid, String userName, String password, String village) {
        try {
            PreparedStatement statement = conn.prepareStatement("INSERT INTO users "
                    +"VALUES (?, ?, ?, ?)");
            statement.setString(1,nid);
            statement.setString(2,userName);
            statement.setString(3,password);
            statement.setString(4,village);

            int rows = statement.executeUpdate();

            if(rows == 0){
                statement.close();
                return false;
            }
            statement.close();
        } catch (SQLException e) {
            System.out.println("error in villageIsValid.");
            e.printStackTrace();
        }
        return true;
    }

    /**
     * inserts the problem(type, summary, description, file(if sent), posting time, posted by,
     * scope) submitted by client in database .
     * @param problems problems details submitted by the users
     * @return string which tells server if problem was successfully inserted or not
     */
    public String insertProblemsToDatabase(Problems problems) {
        int noOfProblems=0;
        try {
            Statement s = conn.createStatement();
            ResultSet rs= s.executeQuery("select count(*) from problems;");
            if(rs.next()){
                noOfProblems=rs.getInt(1);
                System.out.println("num of rows: "+noOfProblems);

            }

            PreparedStatement statement = conn.prepareStatement("INSERT INTO problems "
                    +"VALUES (?, ?, ?, ?,?,?,?,?,?,?,?)");
            statement.setString(1,problems.getProblemType());
            statement.setString(2,problems.getSummary());
            statement.setString(3,problems.getDescription());
            statement.setString(4,problems.getProblemPostedBy());
            statement.setString(5,problems.getDateTime());
            statement.setString(6,"pending");
            statement.setString(7,problems.getProblemScope());
            statement.setInt(8, 0);
            statement.setInt(9,noOfProblems+1);
            statement.setString(10,"");
           // System.out.println("FIle"+problems.getFileName());
            if(problems.getFileName().equals(null))
            { statement.setString(11,null);
                System.out.println("tjh");}
            else
            { statement.setString(11,"C:\\Stored Data From Client\\"//+String.valueOf(noOfProblems+1)
                        +problems.getFileName());
            System.out.println("dfdjfd:" + problems.getFileName());}

            int rows = statement.executeUpdate();

            if(rows == 0){
                statement.close();
               //System.out.println(PROBLEM_INSERTION_FAILED);
                return PROBLEM_INSERTION_FAILED;
            }
            statement.close();
        } catch (SQLException e) {
            System.out.println("error in insertin problems");
            e.printStackTrace();

        }
        return PROBLEM_INSERTED;
    }

    /**
     * When client requests for its history, server reads it from database through this method.
     *
     * @param clientName client username
     * @return arraylist of all the problems submitted by the given username till now
     */
     public ArrayList giveHistoryDataToClient(String clientName) {

         System.out.println(clientName);
        ArrayList<Problems> history=new ArrayList<>();
        try {
            PreparedStatement statement = conn.prepareStatement("SELECT type, summary,description,postedOn,status,scope,vote" +
                    " FROM problems WHERE postedby =? ");
            statement.setString(1,clientName);
            ResultSet rs = statement.executeQuery();
           int flag=0;
            while (rs.next()) {
                flag=1;
                String problemType=rs.getString("type");
                String problemSummary=rs.getString("summary");
                String problemDescription=rs.getString("description");
                String problemPostedOn=rs.getString("postedOn");
                String problemStatus=rs.getString("scope");
                String problemScope=rs.getString("Status");
                int voteOnProblem=rs.getInt("vote");
                history.add(new Problems(problemType,problemSummary,problemDescription,problemPostedOn,problemStatus,problemScope,voteOnProblem));


                //Display values
               // System.out.print("ID: " + problemType+problemDescription+problemSummary);
            }

            rs.close();
            statement.close();
            if(flag==0)return null;
            return history;
        } catch (SQLException e) {
            System.out.println("error in give history data to client.");
            e.printStackTrace();
        }return null;
    }


    /**
     * When normal user requests for changing password, server updates it in database through this method.
     *
     * @param currentUserName normal user username
     * @param newPassword new password client wants to update
     * @return result of password update, whether it was successful or not
     */
    public String updatePassword(String currentUserName, String newPassword) {
        try {
            PreparedStatement statement = conn.prepareStatement("UPDATE users SET password = ? WHERE userName = ?");
            statement.setString(1,newPassword);
            statement.setString(2,currentUserName);

            int rows = statement.executeUpdate();

            if(rows == 0){
                statement.close();
                //System.out.println(PASSWORD_UPDATE_FAILED);
                return PASSWORD_UPDATE_FAILED;
            }
            else{
                statement.close();
               // System.out.println(PASSWORD_UPDATED);
                return PASSWORD_UPDATED;
            }
        } catch (SQLException e) {
            System.out.println("error in password update");
            e.printStackTrace();
        }
        return PASSWORD_UPDATE_FAILED;
    }

    /**
     * When elite user requests for changing password, server updates it in database through this method.
     *
     * @param currentUserName elite user username
     * @param newPassword new password client wants to update
     * @return result of password update, whether it was successful or not
     */
    public String updatePasswordForElite(String currentUserName, String newPassword) {

        try {
            PreparedStatement statement = conn.prepareStatement("UPDATE eliteusers SET password = ? WHERE userName = ?");
           // System.out.println(newPassword);
            statement.setString(1,newPassword);
            statement.setString(2,currentUserName);

            int rows = statement.executeUpdate();

            if(rows == 0){
                statement.close();
               // System.out.println(PASSWORD_UPDATE_FAILED);
                return PASSWORD_UPDATE_FAILED;
            }
            else{
                statement.close();
              //  System.out.println(PASSWORD_UPDATED);
                return PASSWORD_UPDATED;
            }
        } catch (SQLException e) {
            System.out.println("error in password update");
            e.printStackTrace();
        }
        return PASSWORD_UPDATE_FAILED;
    }


    /**
     * When normal user requests for his/ he profile info, server reads it from database through this method.
     *
     * @param username user username
     * @return profile info of given username(username, village , union council, upazilla ,
     *          district in a object form)
     */

    public UserAddress getAllInfoFromUsername(String username){
        try {
            PreparedStatement statement = conn.prepareStatement("SELECT * FROM users, districtdata WHERE users.userName = ?"
                    +" and users.village = districtdata.village");

            statement.setString(1,username);

            ResultSet rs = statement.executeQuery();

            if(rs.next()){
                UserAddress userAddress = new UserAddress(username,rs.getString("village"),rs.getString("unionCouncil")
                        ,rs.getString("upazilla"),rs.getString("NID"));

                rs.close();
                statement.close();
                return userAddress;
            }
            rs.close();statement.close();
            System.out.println("null returned in user all info");
            return  null;
        } catch (SQLException e) {
            System.out.println("error in get all info from username.");
            e.printStackTrace();
        }
        return  null;

    }

    /**
     * Server reads all users profile for its tableViewUser
     *
     * @return profile of all users in a arraylist
     */
    public ArrayList<UserAddress> getAllInfoFromUsername(){
        ArrayList<UserAddress> arrayListOfUserAdress=new ArrayList<>();
        try {
            PreparedStatement statement = conn.prepareStatement("SELECT * FROM users, districtdata"
                    +" WHERE users.village = districtdata.village");

            ResultSet rs = statement.executeQuery();

            while(rs.next()){
                UserAddress userAddress = new UserAddress(rs.getString("userName"),rs.getString("village"),rs.getString("unionCouncil")
                        ,rs.getString("upazilla"),rs.getString("NID"));

                arrayListOfUserAdress.add(userAddress);
            }
            rs.close();statement.close();
            return arrayListOfUserAdress;
        } catch (SQLException e) {
            System.out.println("error in get all info from username.");
            e.printStackTrace();
        }
        return  null;

    }

    /**
     * Server reads all eliteusers profile for its tableViewUser
     *
     * @return profile of all eliteusers in a arraylist
     */
    public ArrayList<EliteUsersProfile> getAllEliteUsers(){
        ArrayList<EliteUsersProfile> arrayList = new ArrayList<>();
        try {
            PreparedStatement statement = conn.prepareStatement("SELECT * FROM eliteusers");

            ResultSet rs = statement.executeQuery();

            while(rs.next()){
                arrayList.add(  new EliteUsersProfile(rs.getString("userName"),rs.getString("NID")
                                ,rs.getString("designation"),rs.getString("workingArea"))
                );

            }
            rs.close();statement.close();
            return  arrayList;
        } catch (SQLException e) {
            System.out.println("error in get all elite users");
            e.printStackTrace();
        }
        return  null;

    }

    /**
     * When eliteuser requests for his/ he profile info, server reads it from database through this method.
     *
     * @param username eliteuser username
     * @return profile info of given username(username, village , union council, upazilla ,
     *          district in a object form)
     */
    public EliteUsersProfile getAllInfoFromUsernameOfElite(String username){
        try {
            PreparedStatement statement = conn.prepareStatement("SELECT * FROM eliteusers WHERE eliteusers.userName = ?");

            statement.setString(1,username);

            ResultSet rs = statement.executeQuery();

            if(rs.next()){
                EliteUsersProfile eliteUsersProfile = new EliteUsersProfile(username,rs.getString("NID")
                        ,rs.getString("designation"),rs.getString("workingArea"));

                rs.close();
                statement.close();
                return eliteUsersProfile;
            }
            rs.close();statement.close();
            System.out.println("null returned in user all info");
            return  null;
        } catch (SQLException e) {
            System.out.println("error in get all info from username.");
            e.printStackTrace();
        }
        return  null;

    }

    /**
     * When user requests for problems details of his/her village,
     * server reads it from database through this method.
     * @param scope the area covered by a problem
     * @param clientName clientname who requested for info
     * @return details of problems of village of given username within the scope
     */
    public ArrayList<ProblemsOfVillage> getProblems(String scope, String clientName){


        try {
            UserAddress userAddress = getAllInfoFromUsername(clientName);
            PreparedStatement statement=null;

            if(scope.equals("village")){
                statement = conn.prepareStatement("select * from problems p join users u on p.postedby =u.userName"
                        +" join districtdata d on d.village=u.village "
                        +"where d.village = ? and p.scope = ?");
                statement.setString(1,userAddress.getVillage());

            } else if(scope.equals("unionCouncil")){
                statement = conn.prepareStatement("select * from problems p join users u on p.postedby =u.userName"
                        +" join districtdata d on d.village=u.village "
                        +"where d.unionCouncil = ? and p.scope = ?");
                statement.setString(1,userAddress.getUnionCouncil());
            }else if(scope.equals("upazilla")){
                statement = conn.prepareStatement("select * from problems p join users u on p.postedby =u.userName"
                        +" join districtdata d on d.village=u.village "
                        +"where d.upazilla = ? and p.scope = ?");
                statement.setString(1,userAddress.getUnionCouncil());
            }
            statement.setString(2,scope);
            ResultSet rs = statement.executeQuery();

            ArrayList<ProblemsOfVillage> problemsOfVillageArrayList=new ArrayList<>();
            int i = 0;
            while(rs.next()){
                i =1;
                String votedByString=rs.getString("votedBy"),voteStatus="not voted";
                StringTokenizer st = new StringTokenizer(votedByString,",");
                while (st.hasMoreTokens()) {
                    if(st.nextToken().equals(clientName)) voteStatus="voted";
                }


                 problemsOfVillageArrayList.add( new ProblemsOfVillage(rs.getString("type"),
                         rs.getString("summary"),rs.getString("description"),rs.getString("postedby"),
                         rs.getString("scope"),
                         rs.getString("postedOn"),rs.getString("status"),String.valueOf(rs.getInt("vote")),String.valueOf(rs.getInt("problemNo")),votedByString,voteStatus));

            }

            if(i==0)
                return  null;
            return  problemsOfVillageArrayList;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * When user requests for problems details of a specific place,
     * server reads it from database through this method.
     * @param scope the area covered by a problem
     * @param placeName name of the place whose problems are to be read
     * @param userName clientname who requested for info
     * @return details of problems of given placename
     */
    public ArrayList<ProblemsOfVillage> getProblemsOnTreeViewSelection(String scope, String placeName, String userName){


        try {



            PreparedStatement statement=null;

            if(scope.equals("village")){
                statement = conn.prepareStatement("select * from problems p join users u on p.postedby =u.userName"
                        +" join districtdata d on d.village=u.village "
                        +"where d.village = ? and p.scope = ?");
                statement.setString(1,placeName);
                statement.setString(2,scope);

            } else if(scope.equals("unionCouncil")){
                statement = conn.prepareStatement("select * from problems p join users u on p.postedby =u.userName"
                        +" join districtdata d on d.village=u.village "
                        +"where d.unionCouncil = ? and p.scope = ?");
                statement.setString(1,placeName);
                statement.setString(2,scope);

            }else if(scope.equals("upazilla")){
                statement = conn.prepareStatement("select * from problems p join users u on p.postedby =u.userName"
                        +" join districtdata d on d.village=u.village "
                        +"where d.upazilla = ? and p.scope = ?");
                statement.setString(1,placeName);
                statement.setString(2,scope);

            }

            ResultSet rs = statement.executeQuery();

            ArrayList<ProblemsOfVillage> problemsOfVillageArrayList=new ArrayList<>();

            while(rs.next()){

                String votedByString=rs.getString("votedBy"),voteStatus="not voted";
                StringTokenizer st = new StringTokenizer(votedByString,",");
                while (st.hasMoreTokens()) {
                    if(st.nextToken().equals(userName)) voteStatus="voted";
                }
                problemsOfVillageArrayList.add( new ProblemsOfVillage(rs.getString("type"),
                        rs.getString("summary"),rs.getString("description"),rs.getString("postedby"),
                        rs.getString("scope"),
                        rs.getString("postedOn"),rs.getString("status"),String.valueOf(rs.getInt("vote")),String.valueOf(rs.getInt("problemNo")),votedByString,voteStatus));

            }
            rs.close();
            statement.close();

            return  problemsOfVillageArrayList;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * When server needs data for problems details of a specific place,
     * server reads it from database through this method.
     * @param scope the area covered by a problem
     * @param placeName name of the place whose problems are to be read
     * @return details of problems of given placename
     */
    public ArrayList<ProblemsOfVillage> getProblemsOnTreeViewSelection(String scope, String placeName){


        try {

            PreparedStatement statement=null;

            if(scope.equals("village")){
                statement = conn.prepareStatement("select * from problems p join users u on p.postedby =u.userName"
                        +" join districtdata d on d.village=u.village "
                        +"where d.village = ? and p.scope = ?");
                statement.setString(1,placeName);
                statement.setString(2,scope);

            } else if(scope.equals("unionCouncil")){
                statement = conn.prepareStatement("select * from problems p join users u on p.postedby =u.userName"
                        +" join districtdata d on d.village=u.village "
                        +"where d.unionCouncil = ? and p.scope = ?");
                statement.setString(1,placeName);
                statement.setString(2,scope);

            }else if(scope.equals("upazilla")){
                statement = conn.prepareStatement("select * from problems p join users u on p.postedby =u.userName"
                        +" join districtdata d on d.village=u.village "
                        +"where d.upazilla = ? and p.scope = ?");
                statement.setString(1,placeName);
                statement.setString(2,scope);

            }

            ResultSet rs = statement.executeQuery();

            ArrayList<ProblemsOfVillage> problemsOfVillageArrayList=new ArrayList<>();

            while(rs.next()){


                problemsOfVillageArrayList.add( new ProblemsOfVillage(rs.getString("type"),
                        rs.getString("summary"),rs.getString("description"),rs.getString("postedby"),
                        rs.getString("scope"),
                        rs.getString("postedOn"),rs.getString("status"),String.valueOf(rs.getInt("vote")),String.valueOf(rs.getInt("problemNo")),rs.getString("votedBy"),"hjhjh"));

            }
            rs.close();
            statement.close();

            return  problemsOfVillageArrayList;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * When client requests for for problems details of district
     * server read it through this method
     * @param username name of client who requested info
     * @return details of problems of district within district scope
     */
    public ArrayList<ProblemsOfVillage> getProblemsOfDistrict(String username){

        try {

            PreparedStatement statement=null;


            statement = conn.prepareStatement("select * from problems p join users u on p.postedby =u.userName"
                        +" join districtdata d on d.village=u.village "
                        +"where p.scope = ? ");
            statement.setString(1,"district");

            ResultSet rs = statement.executeQuery();

            ArrayList<ProblemsOfVillage> problemsOfVillageArrayList=new ArrayList<>();
            while(rs.next()){

                String votedByString=rs.getString("votedBy"),voteStatus="not voted";
                StringTokenizer st = new StringTokenizer(votedByString,",");
                while (st.hasMoreTokens()) {
                    if(st.nextToken().equals(username)) voteStatus="voted";
                }
                problemsOfVillageArrayList.add( new ProblemsOfVillage(rs.getString("type"),
                        rs.getString("summary"),rs.getString("description"),rs.getString("postedby"),
                        rs.getString("scope"),
                        rs.getString("postedOn"),rs.getString("status"),String.valueOf(rs.getInt("vote")),String.valueOf(rs.getInt("problemNo")),votedByString,voteStatus));
            }
            rs.close();
            statement.close();

            return  problemsOfVillageArrayList;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * When server needs data for problems details of district
     * server read it through this method
     * @return details of problems of district within district scope
     */
    public ArrayList<ProblemsOfVillage> getProblemsOfDistrict(){

        try {

            PreparedStatement statement=null;


            statement = conn.prepareStatement("select * from problems p join users u on p.postedby =u.userName"
                    +" join districtdata d on d.village=u.village "
                    +"where p.scope = ? ");
            statement.setString(1,"district");

            ResultSet rs = statement.executeQuery();

            ArrayList<ProblemsOfVillage> problemsOfVillageArrayList=new ArrayList<>();
            while(rs.next()){


                problemsOfVillageArrayList.add( new ProblemsOfVillage(rs.getString("type"),
                        rs.getString("summary"),rs.getString("description"),rs.getString("postedby"),
                        rs.getString("scope"),
                        rs.getString("postedOn"),rs.getString("status"),String.valueOf(rs.getInt("vote")),String.valueOf(rs.getInt("problemNo")),rs.getString("votedBy"),"hgjhj"));
            }
            rs.close();
            statement.close();

            return  problemsOfVillageArrayList;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * When server needs data of all the problems in database for its home table view
     * server read it through this method
     * @return details of all problems
     */
    public ArrayList<ProblemsOfVillage> getAllProblems(){

        try {

            PreparedStatement statement=statement = conn.prepareStatement("select * from problems");
            ResultSet rs = statement.executeQuery();

            ArrayList<ProblemsOfVillage> arrayList=new ArrayList<>();
            while(rs.next()){

                arrayList.add(  new ProblemsOfVillage(rs.getString("type"), rs.getString("summary"),
                        rs.getString("description"),rs.getString("postedby"), rs.getString("scope"),
                        rs.getString("postedOn"),rs.getString("status"),String.valueOf(rs.getInt("vote")),
                        String.valueOf(rs.getInt("problemNo")),rs.getString("votedBy"),"null")

                );
            }
            rs.close();statement.close();

            return arrayList;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * updates vote in a problem in database
     * @param stringArr contains username who gave vote and the no of problem where vote was given
     * @return result whether updating was successful or not
     */
    public String upvateVote(String[] stringArr) {
        try {
            int currentVote;
            String currentVotedBy;
            PreparedStatement readStatement = conn.prepareStatement("SELECT vote, votedBy FROM problems WHERE problems.problemNo = ?");
                readStatement.setInt(1, Integer.parseInt(stringArr[1]));

                ResultSet rs = readStatement.executeQuery();

                if(rs.next()){
                    currentVote=rs.getInt("vote");
                    currentVotedBy=rs.getString("votedBy");
                    readStatement=conn.prepareStatement("UPDATE problems SET vote = ? , votedBy = ? WHERE problemNo = ?");
                    readStatement.setInt(1,currentVote+1);
                    if (currentVote==0) readStatement.setString(2, stringArr[2]);
                    else readStatement.setString(2, currentVotedBy+","+stringArr[2]);
                    readStatement.setInt(3,Integer.parseInt(stringArr[1]));

                    int rows = readStatement.executeUpdate();
                    if(rows!=0){
                        readStatement.close();
                        rs.close();
                        return ACTION_SUCCESS;
                    }
                    readStatement.close();
                    rs.close();
                    return ACTION_FAILED;
                }

                rs.close();readStatement.close();
            return ACTION_FAILED;

            } catch (SQLException e) {
                System.out.println("error in updating vote.");
                e.printStackTrace();
            }
        return ACTION_FAILED;

    }


    /**
     * updates working status in a problem in database
     * @param stringArr contains new working status and the no of problem whose status was changed
     * @return result whether updating was successful or not
     */
    public String upvateWorkingStatus(String[] stringArr) {
        try {
            String currentWorkingStatus;
            PreparedStatement readStatement = conn.prepareStatement("SELECT status FROM problems WHERE problems.problemNo = ?");
            readStatement.setInt(1, Integer.parseInt(stringArr[1]));

            ResultSet rs = readStatement.executeQuery();

            if(rs.next()){
                currentWorkingStatus=rs.getString("status");
                readStatement=conn.prepareStatement("UPDATE problems SET status = ?  WHERE problemNo = ?");
                readStatement.setString(1,stringArr[2]);
                readStatement.setInt(2,Integer.parseInt(stringArr[1]));
               // System.out.println("working Status: "+ stringArr[2]);

                int rows = readStatement.executeUpdate();
                if(rows!=0){
                    readStatement.close();
                    rs.close();
                    return ACTION_SUCCESS;
                }
                readStatement.close();
                rs.close();
                return ACTION_FAILED;
            }

            rs.close();readStatement.close();
            return ACTION_FAILED;

        } catch (SQLException e) {
            System.out.println("error in updating vote.");
            e.printStackTrace();
        }
        return ACTION_FAILED;

    }

}




