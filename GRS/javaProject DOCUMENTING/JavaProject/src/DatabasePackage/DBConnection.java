package DatabasePackage;

import java.sql.*;

/**
 * Establishes connection with database
 * It checks whether table exists or not
 * if not,then it create table
 *
 * Only server has access to this class
 */
public class DBConnection {

    //connection to database
    private static Connection conn;
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost/";
    static final String databaseName = "java_project";
    //database url of this server's data
    private static String url = "jdbc:mysql://localhost:3306/"+databaseName;
    //database username
    private static String user = "root";
    //database password
    private static String pass = "13";

    /**
     * establishes connection with database
     * @return
     * @throws SQLException
     */

    public DBConnection(){
        Connection connection = null;
        Statement statement = null;
        try{

            Class.forName(JDBC_DRIVER);
            connection= DriverManager.getConnection(DB_URL, user, pass);
            statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
            statement.executeUpdate("CREATE DATABASE "+databaseName);
            //createTable(connection);

            statement.close();
            connection.close();
        }catch (SQLException sqlException) {
            if (sqlException.getErrorCode() == 1007) {
                // Database already exists error
                System.out.println(sqlException.getMessage());
            } else {
                // Some other problems, e.g. Server down, no permission, etc
                sqlException.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            // No driver class found!
        }
        // close statement & connection
    }

    public void createTable(){
        Connection conn = null;
        Statement stmt = null;
        try{
            //STEP 2: Register JDBC driver
            Class.forName("com.mysql.jdbc.Driver");

            //STEP 3: Open a connection
            System.out.println("Connecting to a selected database...");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/"+databaseName, user, pass);
            System.out.println("Connected database successfully...");

            //STEP 4: Execute a query
            System.out.println("Creating table in given database...");

            DatabaseMetaData metadata = conn.getMetaData();
            ResultSet resultSet;
            resultSet = metadata.getTables(null, null, "districtdata", null);
            if(resultSet.next()){
                System.out.println("districtdata table exists.");
            }else{
                Statement statement = conn.createStatement();
                String sql = "CREATE TABLE districtdata " +
                        "(village VARCHAR(20) not NULL, " +
                        " unionCouncil VARCHAR(20) not NULL, " +
                        " upazilla VARCHAR(2) not NULL, " +
                        " upazillaCode VARCHAR(2) not NULL, " +
                        " districtCode VARCHAR(2) not NULL " +
                        ")"
                        ;
                statement.executeUpdate(sql);
                statement.close();
            }

            resultSet = metadata.getTables(null, null, "eliteusers", null);
            if(resultSet.next()){
                System.out.println("eliteusers table exists.");
            }else{
                Statement statement = conn.createStatement();
                String sql = "CREATE TABLE eliteusers " +
                        "(NID VARCHAR(17) not NULL PRIMARY KEY, " +
                        " userName VARCHAR(20) not NULL UNIQUE, " +
                        " password VARCHAR(40) not NULL, " +
                        " designation VARCHAR(20) not NULL, " +
                        " workingArea VARCHAR(20) not NULL " +
                        ")";
                statement.executeUpdate(sql);
                statement.close();
            }

            resultSet = metadata.getTables(null, null, "users", null);
            if(resultSet.next()){
                System.out.println("users table exists.");
            }else{
                Statement statement = conn.createStatement();
                String sql = "CREATE TABLE users " +
                        "(NID VARCHAR(17) not NULL PRIMARY KEY, " +
                        " userName VARCHAR(20) not NULL UNIQUE , " +
                        " password VARCHAR(40) not NULL, " +
                        " village VARCHAR(20) not NULL " +
                        ")";
                statement.executeUpdate(sql);
                statement.close();
            }

            resultSet = metadata.getTables(null, null, "problems", null);
            if(resultSet.next()){
                System.out.println("problems table exists.");
            }else{
                Statement statement = conn.createStatement();
                String sql = "CREATE TABLE prob " +
                        "(type VARCHAR(20) not NULL , " +
                        " summary VARCHAR(100) not NULL, " +
                        " description VARCHAR(4000) not NULL, " +
                        " postedby VARCHAR(20) not NULL, " +
                        " postedOn DATETIME not NULL, " +
                        " status VARCHAR(20) not NULL, " +
                        " scope VARCHAR(20) not NULL, " +
                        " vote INT not NULL, " +
                        " problemNo INT not NULL PRIMARY KEY, " +
                        " votedBy VARCHAR(2000) not NULL, " +
                        " filePath VARCHAR(20) " +


                        ")";
                statement.executeUpdate(sql);
                statement.close();
            }

            resultSet = metadata.getTables(null, null, "nidandname", null);
            if(resultSet.next()){
                System.out.println("nidand name table exists.");
            }else{
                Statement statement = conn.createStatement();
                String sql = "CREATE TABLE nidandname " +
                        "(NID VARCHAR(17) not NULL PRIMARY KEY, " +
                        " name VARCHAR(40) not NULL " +


                        ")";
                statement.executeUpdate(sql);
                statement.close();
            }

            resultSet.close();conn.close();

            System.out.println("Created table in given database...");

        }catch(SQLException se){
            //Handle errors for JDBC
            se.printStackTrace();
        }catch(Exception e){
            //Handle errors for Class.forName
            e.printStackTrace();
        }
    }



    public static Connection connect() throws SQLException {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (ClassNotFoundException cnfe) {
            System.err.println("Error: " + cnfe.getMessage());
        } catch (InstantiationException ie) {
            System.err.println("Error: " + ie.getMessage());
        } catch (IllegalAccessException iae) {
            System.err.println("Error: " + iae.getMessage());
        }

        conn = DriverManager.getConnection(url, user, pass);
        return conn;
    }

    /**
     * @return reference of the connection created with the database
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        if (conn != null && !conn.isClosed())
            return conn;
        connect();
        return conn;

    }

    public static void main(String[] args) {
        DBConnection db = new DBConnection();
        db.createTable();
    }
}