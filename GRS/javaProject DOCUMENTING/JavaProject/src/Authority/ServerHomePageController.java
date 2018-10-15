package Authority;

import CommonClass.AlertClass;
import DatabasePackage.DBConnection;
import DatabasePackage.Database;
import NetworkRelatedClass.*;
import Users.WriteThreadClient;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 * This class controls all the components of serverHomePage.fxml . Through this
 * class, server can see info of all users , all elite users and all problems
 * submitted till now. If server wants, it can also see problems of a specific
 * village or union council or upazilla or district.
 * Server also has the privilege to see which users are online.
 *
 * Data needed for that are read from database by calling methods.
 * Server sees those data in table view. With double click, details of problem
 * can be seen in new stage.
 *
 * Four TableView is utilized here to show four different type of data (users, elite users,
 * all problems, problems of specific place. And button are used to switch views.
 *
 * Only server has access to this class
 */
public class ServerHomePageController implements Runnable {


    //shows all problems submiited till now
    public TableView tableViewHomeServer;
    //shows all users data
    public TableView tableViewUsersServer;
    //shows all eliteusers data
    public TableView tableViewEliteUsersServer;
    //shows problems of the selected place
    public TableView tableViewTreeView;
    //shows which users are online
    public ListView listViewServer;
    //shows name of all village, upazilla and union council in our working district in tree view
    public TreeView treeViewServer;

    //pressing this button shows home page with all problems
    public ToggleButton homeServerButton;
    //pressing this button shows all users data
    public ToggleButton usersButton;
    //pressing this button shows all elite users data
    public ToggleButton eliteUsersButton;

    //helps communicating between this controller, and main
    private AuthorityMain authorityMain;
    //list of clients who are online
    private ObservableList<String> clientOnlineObservableList=FXCollections.observableArrayList();
    //list of problems of a specific place. data is inserted here according to the treeview place was selected
    // to show data of that place
    private ObservableList<ProblemsOfVillage> observableList=FXCollections.observableArrayList();
    //list of all users data
    private ObservableList<UserAddress> observableListOfUserAddress=FXCollections.observableArrayList();
    //list of all problems
    private ObservableList<ProblemsOfVillage> observableListOfHome=FXCollections.observableArrayList();
    //list of all elite users data
    private ObservableList<EliteUsersProfile> observableListOfElite=FXCollections.observableArrayList();

    //through this object server reads from database
    private Database database = new Database();
    //name of all village, union council, upazilla, diatrict is saved here to show in treeview
    private ArrayList<DistrictData> districtDataForUsers = new ArrayList<>();

    //determines whether treeViewTable columns were initialized. if true it needs initialization
    private boolean initTreeViewTable = true;
    //determines whether TableViewForUsers columns were initialized. if true it needs initialization
    private boolean initTableViewForUsers=true;
    //determines whether TableViewForEliteUsers columns were initialized. if true it needs initialization
    private boolean initTableViewForEliteUsers=true;
    //determines whether TableViewForHome columns were initialized. if true it needs initialization
    private boolean initTableViewForHome=true;



    /**
     * works after home button is pressed. it shows all problems in database
     */
    @FXML
    public void homeServerButtonAction(ActionEvent actionEvent) {
        showTableViewForHome();
    }

    /**
     * works after users button is pressed. it shows all a table with users NID,
     * username, village, upazilla, union council, district
     */
    @FXML
    public void usersButtonAction(ActionEvent actionEvent) {showTableViewForUsers();}

    /**
     * works after eliteusers button is pressed. it shows all a table with eliteusers NID,
     * username, working area, designation
     */
    @FXML
    public void eliteUsersButtonAction(ActionEvent actionEvent) {
        showTableViewForEliteUsers();
    }

    /**
     * @param authorityMain links this controoler and AuthorityMain
     */
    public void setServerMain(AuthorityMain authorityMain) {this.authorityMain = authorityMain;}

    /**
     * data needed to load treeview is same for server and client .So, server reads this data from database and stores.
     * when a new client successfully sign in or up ,then server immediately gets this data through this method to send
     * to client.
     *
     * @return data needed to load tree view which shows all village, union council, upazilla, district
     *          in hierarchical order
     */
    public ArrayList<DistrictData> getDistrictDataForUsers() {
        return districtDataForUsers;
    }

    /**
     * this controller works in a new thread. the new thread starts in constructor
     */
    public ServerHomePageController() {
        Thread serverNetworkThread = new Thread(this);
        serverNetworkThread.start();
    }

    /**
     * after thread starts running it immediately opens a server socket to accept new client in a loop
     * whenever a new client connects, server starts a new thread to start reading from that specific client
     */
    @Override
    public void run() {
        DBConnection dbConnection = new DBConnection();
        dbConnection.createTable();

        try {
            ServerSocket serverSocket = new ServerSocket(33333);
            while (true) {
                Socket clientSock = serverSocket.accept();
                NetworkUtil clientNetworkUtil = new NetworkUtil(clientSock);
                new ReadThreadServer(clientNetworkUtil,authorityMain);

            }
        } catch (Exception e) {

        }
    }

    /**
     * sets the username of new online client to online list view
     * @param clientName username of the client(normal user) who is online(successfully logged in)
     */
    public void setClientOnlineObservableList(String clientName)
    {
        clientOnlineObservableList.add(clientName);
        listViewServer.setItems(clientOnlineObservableList);
    }

    /**
     * Even if client log in from different device, its name is showed in online list only once.
     * Here it is checked.
     *
     * @param clientName username of the client(normal user) who is online(successfully logged in)
     * @return if the username is already online or not
     */
    public boolean checkOnlineList(String clientName)
    {
        for(String client:clientOnlineObservableList)
        {
            if(client.equals(clientName)) return true;
        }
        return false;
    }

    /**
     *  removes client from online client list after logging out.
     *
     * @param clientName username of the client(normal user) who logged out
     */
    public void takeUserToOffline(String clientName) {
        for (String s:clientOnlineObservableList)
        {
            if(s.equals(clientName)) {
                clientOnlineObservableList.remove(clientName);
                break;
            }
        }
    }

    /**
     * shows tree view after getting data from database. data is set in hierarchical order
     * by this method. it also saves the needed for tree view, which will be needed later
     * to provide to client. It also sets action on selecting a tree cell.
     *
     * After selecting a tree cell, the problems in that area is shown in a table.
     */
    public void showTreeViewServer(){
        Connection c;
        TreeItem<String> rootNode = new TreeItem<String>("Comilla");


        try {
            c = DBConnection.connect();
            String SQL = "Select upazilla, unionCouncil, village From districtdata";
            ResultSet rs = c.createStatement().executeQuery(SQL);

            //TreeItem <String> districtNode=rootNode;
            TreeItem<String> upazillaNode;
            TreeItem <String> unionNode; TreeItem <String> villageNode;
            while (rs.next()) {
                String vill = rs.getString("village");
                String uniCoun = rs.getString("unionCouncil");
                String upaz = rs.getString("upazilla");
                districtDataForUsers.add(new DistrictData(vill,uniCoun,upaz));

                villageNode = new TreeItem<>(vill);
                boolean unionFound = false;
                boolean upazillaFound = false;

                for(TreeItem<String> upazilla: rootNode.getChildren()){
                    if(upazilla.getValue().contentEquals(upaz))
                    {
                        for(TreeItem<String> union: upazilla.getChildren()){
                            if(union.getValue().contentEquals(uniCoun)){
                                union.getChildren().add(villageNode);
                                union.setExpanded(true);

                                unionFound = true;
                                break;
                            }

                        }
                        if(!unionFound){
                            unionNode = new TreeItem<>(uniCoun);
                            upazilla.getChildren().add(unionNode);
                            upazilla.setExpanded(true);
                            unionNode.getChildren().add(villageNode);
                            unionNode.setExpanded(true);
                        }

                        upazillaFound = true;
                        break;
                    }

                }
                if(!upazillaFound){
                    upazillaNode = new TreeItem<String>(upaz);
                    rootNode.getChildren().add(upazillaNode);
                    rootNode.setExpanded(true);
                    unionNode = new TreeItem<>(uniCoun);
                    upazillaNode.getChildren().add(unionNode);
                    upazillaNode.setExpanded(true);
                    unionNode.getChildren().add(villageNode);
                    unionNode.setExpanded(true);
                }



            }


            //tree cell selection action. it gets data of the selected place and sets them in treeViewTableView
            treeViewServer.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {

                @Override
                public void changed(ObservableValue observable, Object oldValue,
                                    Object newValue) {

                    TreeItem<String> selectedItem = (TreeItem<String>) newValue;
                    //System.out.println("Selected Tree cell : " + selectedItem.getValue());

                    if(selectedItem.getValue().equals("Comilla"))
                    {
                        initTableViewTreeView(database.getProblemsOfDistrict());
                    }
                    else if(selectedItem.getParent().getValue().equals("Comilla"))
                    {
                        initTableViewTreeView(database.getProblemsOnTreeViewSelection("upazilla",selectedItem.getValue()));
                    }
                    else if(selectedItem.getParent().getParent().getValue().equals("Comilla"))
                    {
                        initTableViewTreeView(database.getProblemsOnTreeViewSelection("unionCouncil",selectedItem.getValue()));
                    }
                    else if(selectedItem.getParent().getParent().getParent().getValue().equals("Comilla"))
                    {
                        ArrayList arrayList=database.getProblemsOnTreeViewSelection("village",selectedItem.getValue());
                        initTableViewTreeView(arrayList);
                    }

                }});

            treeViewServer.setRoot(rootNode);
        } catch (SQLException e) {
            e.printStackTrace();
        }


            }

    /**
     * initializes columns for tableViewHome which shows all problems of all area till now,
     * and action on double click on table row.
     */
    public void setTableViewInServer()
    {

            TableColumn<ProblemsOfVillage, String> problemTypeCol = new TableColumn<>("Type");
            problemTypeCol.setMinWidth(130);
            problemTypeCol.setCellValueFactory(new PropertyValueFactory<>("problemType"));
            problemTypeCol.setCellFactory(TextFieldTableCell.<ProblemsOfVillage>forTableColumn());

            TableColumn<ProblemsOfVillage, String> summaryCol = new TableColumn<>("Summary");
            summaryCol.setMinWidth(130);
            summaryCol.setCellValueFactory(new PropertyValueFactory<>("summary"));
            summaryCol.setCellFactory(TextFieldTableCell.<ProblemsOfVillage>forTableColumn());

            TableColumn<ProblemsOfVillage, String> descriptionCol = new TableColumn<>("Description");
            descriptionCol.setMinWidth(130);
            descriptionCol.setCellValueFactory(new PropertyValueFactory<>("shortDescription"));
            descriptionCol.setCellFactory(TextFieldTableCell.<ProblemsOfVillage>forTableColumn());

            TableColumn<ProblemsOfVillage, String> problemPostedByCol = new TableColumn<>("Posted By");
            problemPostedByCol.setMinWidth(130);
            problemPostedByCol.setCellValueFactory(new PropertyValueFactory<>("problemPostedBy"));
            problemPostedByCol.setCellFactory(TextFieldTableCell.<ProblemsOfVillage>forTableColumn());

            TableColumn<ProblemsOfVillage, String> dateTimeCol = new TableColumn<>("Posted On");
            dateTimeCol.setMinWidth(130);
            dateTimeCol.setCellValueFactory(new PropertyValueFactory<>("dateTime"));
            dateTimeCol.setCellFactory(TextFieldTableCell.<ProblemsOfVillage>forTableColumn());

            TableColumn<ProblemsOfVillage, String> problemStatusCol = new TableColumn<>("Status");
            problemStatusCol.setMinWidth(130);
            problemStatusCol.setCellValueFactory(new PropertyValueFactory<>("problemStatus"));
            problemStatusCol.setCellFactory(TextFieldTableCell.<ProblemsOfVillage>forTableColumn());

            TableColumn<ProblemsOfVillage, String> voteCol = new TableColumn<>("Vote");
            voteCol.setMinWidth(130);
            voteCol.setCellValueFactory(new PropertyValueFactory<>("vote"));
            voteCol.setCellFactory(TextFieldTableCell.<ProblemsOfVillage>forTableColumn());

            TableColumn<ProblemsOfVillage, String> votedByCol = new TableColumn<>("Voted By");
             votedByCol.setMinWidth(120);
             votedByCol.setCellValueFactory(new PropertyValueFactory<>("votedBy"));
             votedByCol.setCellFactory(TextFieldTableCell.<ProblemsOfVillage>forTableColumn());

        tableViewTreeView.getColumns().addAll(problemTypeCol,summaryCol,descriptionCol,problemPostedByCol,dateTimeCol,problemStatusCol,voteCol);
        tableViewTreeView.setRowFactory(tv -> {
            TableRow<ProblemsOfVillage> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    ProblemsOfVillage p = row.getItem();
                    System.out.println("double click " + p.getDescription());

                    Stage stage = new Stage();
                    stage.setTitle("Problem Details");
                    Group root = new Group();
                    TextArea textArea = new TextArea();
                    textArea.setPrefSize(600, 700);

                    textArea.setWrapText(true);

                    textArea.appendText("Problem Type: " + p.getProblemType() + "\n");
                    textArea.appendText("Posted On: " + p.getDateTime() + "\n");
                    textArea.appendText("Problem Summary:    " + p.getSummary() + "\n\n");
                    textArea.appendText("Problem Description:\n" + p.getDescription() + "\n\n");
                    textArea.appendText("----------------------------------------------------------------------\n\n");


                    ScrollPane scrollPane = new ScrollPane();
                    scrollPane.setContent(textArea);
                    scrollPane.setFitToWidth(true);
                    scrollPane.setPrefWidth(600);
                    scrollPane.setPrefHeight(700);

                    VBox vBox = new VBox();
                    vBox.getChildren().add(scrollPane);
                    root.getChildren().add(vBox);
                    Scene scene = new Scene(root, 600, 700);
                    scene.getStylesheets().add("Users/Style.css");
                    stage.setScene(scene);
                    stage.show();
                }
            });
            return row;
        });
    }


    /**
     * sets the table of problems for selected area in tree view and if table columns are not initialized
     * they are initialized.
     *
     * @param arrayList contains problem details of the area selected in tree view
     */
    public void initTableViewTreeView(ArrayList<ProblemsOfVillage> arrayList)
    {

        try
        {
            tableViewTreeView.setVisible(true);
            tableViewHomeServer.setVisible(false);
            tableViewEliteUsersServer.setVisible(false);
            tableViewUsersServer.setVisible(false);

            int size=observableList.size();
            observableList.addAll(arrayList);

            observableList.remove(0,size);
            if(initTreeViewTable) {

                initTreeViewTable = false;
                setTableViewInServer();
            }
            tableViewTreeView.setItems(observableList);

        }catch (Exception e)
        {
            System.out.println("error in server home control initTableViewTreeView");
        }



    }

    /**
     * initializes columns for tableViewUsers which shows all users data
     */
    public void setUsersTableView()
    {

        TableColumn<UserAddress, String> usernameCol = new TableColumn<>("Username");
        usernameCol.setMinWidth(200);
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        usernameCol.setCellFactory(TextFieldTableCell.<UserAddress>forTableColumn());

        TableColumn<UserAddress, String> NIDCol = new TableColumn<>("NID");
        NIDCol.setMinWidth(200);
        NIDCol.setCellValueFactory(new PropertyValueFactory<>("NID"));
        NIDCol.setCellFactory(TextFieldTableCell.<UserAddress>forTableColumn());


        TableColumn<UserAddress, String> villageCol = new TableColumn<>("Village");
        villageCol.setMinWidth(200);
        villageCol.setCellValueFactory(new PropertyValueFactory<>("village"));
        villageCol.setCellFactory(TextFieldTableCell.<UserAddress>forTableColumn());


        TableColumn<UserAddress, String> unionCol = new TableColumn<>("Union Council");
        unionCol.setMinWidth(200);
        unionCol.setCellValueFactory(new PropertyValueFactory<>("unionCouncil"));
        unionCol.setCellFactory(TextFieldTableCell.<UserAddress>forTableColumn());

        TableColumn<UserAddress, String> upazillaCol = new TableColumn<>("Upazilla");
        upazillaCol.setMinWidth(200);
        upazillaCol.setCellValueFactory(new PropertyValueFactory<>("upazilla"));
        upazillaCol.setCellFactory(TextFieldTableCell.<UserAddress>forTableColumn());



        tableViewUsersServer.getColumns().addAll(NIDCol,usernameCol,villageCol,unionCol,upazillaCol);
    }

    /**
     * sets and shows the table of users and if table columns are not initialized
     * they are initialized.
     * the users data is read form database
     */
    public void showTableViewForUsers(){

        tableViewEliteUsersServer.setVisible(false);
        tableViewUsersServer.setVisible(true);
        tableViewHomeServer.setVisible(false);
        tableViewTreeView.setVisible(false);

        ArrayList<UserAddress> arraylistOfUserAddress=database.getAllInfoFromUsername();
        int size=observableListOfUserAddress.size();

        if(arraylistOfUserAddress!=null)
            observableListOfUserAddress.addAll(arraylistOfUserAddress);
        observableListOfUserAddress.remove(0,size);
        //System.out.println("users table : "+observableListOfUserAddress.get(0).getUsername());

        if(initTableViewForUsers){
            setUsersTableView();
            initTableViewForUsers=false;
        }

        //tableViewUsersServer.setItems(observableList);
        tableViewUsersServer.setItems(observableListOfUserAddress);

    }

    /**
     * initializes columns for tableViewEliteUsers which shows all eliteusers data
     */
    public void setEliteUsersTableView()
    {

        TableColumn<EliteUsersProfile, String> usernameCol = new TableColumn<>("Username");
        usernameCol.setMinWidth(250);
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        usernameCol.setCellFactory(TextFieldTableCell.<EliteUsersProfile>forTableColumn());

        TableColumn<EliteUsersProfile, String> NIDCol = new TableColumn<>("NID");
        NIDCol.setMinWidth(250);
        NIDCol.setCellValueFactory(new PropertyValueFactory<>("NID"));
        NIDCol.setCellFactory(TextFieldTableCell.<EliteUsersProfile>forTableColumn());

        TableColumn<EliteUsersProfile, String> designationCol = new TableColumn<>("Designation");
        designationCol.setMinWidth(250);
        designationCol.setCellValueFactory(new PropertyValueFactory<>("designation"));
        designationCol.setCellFactory(TextFieldTableCell.<EliteUsersProfile>forTableColumn());

        TableColumn<EliteUsersProfile, String> workingAreaCol = new TableColumn<>("Working Area");
        workingAreaCol.setMinWidth(250);
        workingAreaCol.setCellValueFactory(new PropertyValueFactory<>("workingArea"));
        workingAreaCol.setCellFactory(TextFieldTableCell.<EliteUsersProfile>forTableColumn());

        tableViewEliteUsersServer.getColumns().addAll(NIDCol,usernameCol,designationCol,workingAreaCol);
    }

    /**
     * sets and shows the table of eliteusers and if table columns are not initialized
     * they are initialized.
     * the eliteusers data is read form database
     */
    public void showTableViewForEliteUsers(){

        tableViewEliteUsersServer.setVisible(true);
        tableViewUsersServer.setVisible(false);
        tableViewHomeServer.setVisible(false);
        tableViewTreeView.setVisible(false);

        ArrayList<EliteUsersProfile> arraylist=database.getAllEliteUsers();
        int size=observableListOfElite.size();

        if(arraylist!=null)
            observableListOfElite.addAll(arraylist);
        observableListOfElite.remove(0,size);
        //System.out.println("elite users table: "+observableListOfElite.get(0).getUsername());

        if(initTableViewForEliteUsers){
            setEliteUsersTableView();
            initTableViewForEliteUsers=false;
        }

        tableViewEliteUsersServer.setItems(observableListOfElite);

    }

    /**
     * initializes columns for tableViewHome which shows all problems details
     */
    public void setHomeTableView()
    {

        TableColumn<ProblemsOfVillage, String> problemTypeCol = new TableColumn<>("Type");
        problemTypeCol.setMinWidth(150);
        problemTypeCol.setCellValueFactory(new PropertyValueFactory<>("problemType"));
        problemTypeCol.setCellFactory(TextFieldTableCell.<ProblemsOfVillage>forTableColumn());

        TableColumn<ProblemsOfVillage, String> summaryCol = new TableColumn<>("Summary");
        summaryCol.setMinWidth(150);
        summaryCol.setCellValueFactory(new PropertyValueFactory<>("summary"));
        summaryCol.setCellFactory(TextFieldTableCell.<ProblemsOfVillage>forTableColumn());

        TableColumn<ProblemsOfVillage, String> descriptionCol = new TableColumn<>("Description");
        descriptionCol.setMinWidth(150);
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("shortDescription"));
        descriptionCol.setCellFactory(TextFieldTableCell.<ProblemsOfVillage>forTableColumn());

        TableColumn<ProblemsOfVillage, String> problemPostedByCol = new TableColumn<>("Posted By");
        problemPostedByCol.setMinWidth(100);
        problemPostedByCol.setCellValueFactory(new PropertyValueFactory<>("problemPostedBy"));
        problemPostedByCol.setCellFactory(TextFieldTableCell.<ProblemsOfVillage>forTableColumn());

        TableColumn<ProblemsOfVillage, String> dateTimeCol = new TableColumn<>("Posted On");
        dateTimeCol.setMinWidth(150);
        dateTimeCol.setCellValueFactory(new PropertyValueFactory<>("dateTime"));
        dateTimeCol.setCellFactory(TextFieldTableCell.<ProblemsOfVillage>forTableColumn());

        TableColumn<ProblemsOfVillage, String> problemStatusCol = new TableColumn<>("Status");
        problemStatusCol.setMinWidth(100);
        problemStatusCol.setCellValueFactory(new PropertyValueFactory<>("problemStatus"));
        problemStatusCol.setCellFactory(TextFieldTableCell.<ProblemsOfVillage>forTableColumn());

        TableColumn<ProblemsOfVillage, String> voteCol = new TableColumn<>("Vote");
        voteCol.setMinWidth(100);
        voteCol.setCellValueFactory(new PropertyValueFactory<>("vote"));
        voteCol.setCellFactory(TextFieldTableCell.<ProblemsOfVillage>forTableColumn());

        TableColumn<ProblemsOfVillage, String> votedByCol = new TableColumn<>("Voted By");
        votedByCol.setMinWidth(150);
        votedByCol.setCellValueFactory(new PropertyValueFactory<>("votedBy"));
        votedByCol.setCellFactory(TextFieldTableCell.<ProblemsOfVillage>forTableColumn());

        tableViewHomeServer.getColumns().addAll(problemTypeCol,summaryCol,descriptionCol,problemPostedByCol,dateTimeCol,problemStatusCol,voteCol);
        tableViewHomeServer.setRowFactory(tv -> {
            TableRow<ProblemsOfVillage> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    ProblemsOfVillage p = row.getItem();
                    System.out.println("double click " + p.getDescription());

                    Stage stage = new Stage();
                    stage.setTitle("Problem Details");
                    Group root = new Group();
                    TextArea textArea = new TextArea();
                    textArea.setPrefSize(600, 700);

                    textArea.setWrapText(true);

                    textArea.appendText("Problem Type: " + p.getProblemType() + "\n");
                    textArea.appendText("Posted On: " + p.getDateTime() + "\n");
                    textArea.appendText("Problem Summary:    " + p.getSummary() + "\n\n");
                    textArea.appendText("Problem Description:\n" + p.getDescription() + "\n\n");
                    textArea.appendText("----------------------------------------------------------------------\n\n");


                    ScrollPane scrollPane = new ScrollPane();
                    scrollPane.setContent(textArea);
                    scrollPane.setFitToWidth(true);
                    scrollPane.setPrefWidth(600);
                    scrollPane.setPrefHeight(700);

                    VBox vBox = new VBox();
                    vBox.getChildren().add(scrollPane);
                    root.getChildren().add(vBox);
                    Scene scene = new Scene(root, 600, 700);
                    scene.getStylesheets().add("Users/Style.css");
                    stage.setScene(scene);
                    stage.show();
                }
            });
            return row;
        });
    }

    /**
     * sets and shows the table of tableViewHome and if table columns are not initialized
     * they are initialized.
     * the tableViewHome data is read form database
     */
    public void showTableViewForHome(){

        tableViewEliteUsersServer.setVisible(false);
        tableViewUsersServer.setVisible(false);
        tableViewHomeServer.setVisible(true);
        tableViewTreeView.setVisible(false);

        ArrayList<ProblemsOfVillage> arraylist = database.getAllProblems();
        int size=observableListOfHome.size();

        if(arraylist!=null)
            observableListOfHome.addAll(arraylist);
        observableListOfHome.remove(0,size);
        //System.out.println("home table : "+observableListOfHome.get(0).getDescription());

        if(initTableViewForHome){
            setHomeTableView();
            initTableViewForHome=false;
        }

        tableViewHomeServer.setItems(observableListOfHome);

    }
}



