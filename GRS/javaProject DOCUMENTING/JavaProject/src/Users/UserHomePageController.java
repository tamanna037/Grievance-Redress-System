package Users;

import CommonClass.AlertClass;
import NetworkRelatedClass.*;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import Interfaces.SharedConstants;import javafx.beans.value.ObservableValue;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * It is the controller class of "userHomePage.fxml".
 * It is a platform for normal user to make complain about their area with or without an attachment
 * and for elite user to change the working status.
 * Users can see all the problems of their district and can vote on problems of their area
 * Users can change settings if they want
 * Users can see their previous history and profile
 * Users can see the statistics of their village based on vote on a particular problem type
 */
public class UserHomePageController implements SharedConstants {

    //treeView of all area of district
    public TreeView<String> treeView=new TreeView();


    //the table of user's own village problems
    public TableView tableView;
    //the table of other village problems
    public TableView othersTableView;
    //the table for elite user's own working area problems
    public TableView eliteUsersTableView;

    //for selection of submitted problem type
    public ChoiceBox problemTypeChoiceBox;
    //for selection of the scope of submitted problem type
    public ChoiceBox problemScopeChoiceBox;

    //here user's history is shown
    public TextArea historyTextArea;
    //here user's profile is shown
    public TextArea profileTextArea;
    //here problem description is written
    public TextArea homeTextArea;

    //here problem's summary is written
    public TextField complainSummaryTextField;
    //here filePath is shown
    public TextField filePath;

    public ToggleButton uploadFile;
    public ToggleButton historyButton;
    public ToggleButton myVillageButton;
    public ToggleButton complainBoxButton;
    public ToggleButton successStoriesButton;

    public ButtonBar villageButtonBar;
    public Button saveButton;

    public Label homePageLabel;
    public AnchorPane HomePage;


    //list for user's own village problems
    private ArrayList<ProblemsOfVillage> arrayList = new ArrayList<>();
    private ObservableList<ProblemsOfVillage> observableList= FXCollections.observableArrayList();

    //list for elite user's own working area
    private ArrayList<ProblemsOfVillage> arraylistOfElite=new ArrayList<>();
    private ObservableList<ProblemsOfVillage> observableListForElite= FXCollections.observableArrayList();

    //by default, problem Type is road and highway
    private String selectedProblemType="Road and Highway";
    //by default, problem scope is village
    private String selectedProblemScope="Village";

    //after changing the working status by elite user, selected working status is stored
    private String selectedWorkingStatus;
    private String selectedTreeView;
    private String fileName;
    private String username;

    private UserMain userMain;
    //check whether the user is normal or not
    private boolean isNormal;
    private NetworkUtil ClientNetworkUtil;

    //boolean needed so that tableColumns do not add again and again after any insertion in table
    private boolean initTreeViewTable = true;
    private boolean othersTreeViewTable=true;
    private boolean villageButtonPress = false;
    private boolean homeButtonPressForElite = false;
    private boolean initEliteTableView = true;





    /**
     * loads a scene to make complain for normal user and for elite user it shows a table
     * of user's own working area
     *
     * @param actionEvent
     *
     *
     */
    @FXML
    public void HomeButtonAction(ActionEvent actionEvent) {

        if(!isNormal) {
            String[] request = new String[3];
            request[0] = HomeButtonPressed;
            request[1] = userMain.getEliteUsersProfile().getDesignation();
            request[2] = userMain.getEliteUsersProfile().getWorkingArea();
            new WriteThreadClient(userMain.getClientNetworkUtil(), request);
            homeButtonPressForElite = true;
            othersTableView.setVisible(false);
            eliteUsersTableView.setVisible(true);
        }
        try {
            isNormal=!userMain.isLoginAsElite();
            if(isNormal()) normalUsersHomePageView();
            else eliteUsersHomePageView();


        }catch (Exception e)
        {
            System.out.println("Error loading Home page");
        }
    }


    /**
     * load normal user's scene
     */
    public void normalUsersHomePageView()
    {
        homeTextArea.setWrapText(true);
        saveButton.setText("Submit");
        homeButtonPressForElite = false;
        eliteUsersTableView.setVisible(false);
        villageButtonBar.setVisible(false);
        villageButtonPress = false;
        problemTypeChoiceBox.setVisible(true);
        filePath.setVisible(true);
        uploadFile.setVisible(true);
        problemTypeChoiceBox.setItems(observableListOfProblemType);
        problemScopeChoiceBox.setVisible(true);
        problemScopeChoiceBox.setItems(observableListOfProblemScope);
        tableView.setVisible(false);
        homeTextArea.setVisible(true);
        saveButton.setVisible(true);
        complainSummaryTextField.setVisible(true);
        selectionOfChoiceBox();
        othersTableView.setVisible(false);
        profileTextArea.setVisible(false);
        historyTextArea.setVisible(false);

    }

    /**
     * load elite user's scene
     */
    public void eliteUsersHomePageView()
    {

        saveButton.setVisible(false);
        eliteUsersTableView.setVisible(true);
        homeTextArea.setVisible(false);
        othersTableView.setVisible(false);
        profileTextArea.setVisible(false);

        villageButtonBar.setVisible(true);
        successStoriesButton.setVisible(false);
        villageButtonPress = false;
        problemTypeChoiceBox.setVisible(false);
        filePath.setVisible(false);
        uploadFile.setVisible(false);
        problemScopeChoiceBox.setVisible(false);
        tableView.setVisible(false);
        homeTextArea.setVisible(false);
        historyTextArea.setVisible(false);
        complainSummaryTextField.setVisible(false);
        historyButton.setVisible(false);
        myVillageButton.setVisible(false);
        homePageLabel.setVisible(false);

    }
    /**
     * show user's history
     * @param actionEvent
     */
    @FXML
    public void historyButtonAction(ActionEvent actionEvent) {
        historyPageView();
        String[] clientActionInfo = new String [2];
            clientActionInfo[0]=MyHistoryButtonPressed;
            clientActionInfo[1]=userMain.getUsername();
            ClientNetworkUtil=userMain.getClientNetworkUtil();

          new WriteThreadClient(userMain.getClientNetworkUtil(),clientActionInfo)  ;
           // ClientNetworkUtil.write(clientActionInfo);
    }

    /**
     * load scene needed for history page view
     */
    public void historyPageView()
    {

        homeButtonPressForElite = false;
        historyTextArea.setLayoutY(120);
        othersTableView.setVisible(false);
        homeTextArea.setVisible(false);
        historyTextArea.setVisible(true);
        historyTextArea.setPrefHeight(570.0);
        profileTextArea.setVisible(false);
        filePath.setVisible(false);
        uploadFile.setVisible(false);
        historyTextArea.setText("");
        tableView.setVisible(false);
        homePageLabel.setText("My History");
        villageButtonBar.setVisible(false);
        problemTypeChoiceBox.setVisible(false);
        problemScopeChoiceBox.setVisible(false);
        saveButton.setVisible(false);
        complainSummaryTextField.setVisible(false);
        villageButtonPress = false;
    }

    /**
     * set history in textArea
     * @param arrayList
     */
    public void setHistoryInTextArea(ArrayList<Problems> arrayList)
    {
        historyTextArea.setText("");
        for (Problems problems : arrayList) {
            historyTextArea.appendText("Problem Type: " + problems.getProblemType() + "\n");
            historyTextArea.appendText("Posted On: " + problems.getDateTime() + "\n");
            historyTextArea.appendText("Problem Scope: " + problems.getProblemScope() + "     " + "Problem Status: " + problems.getProblemStatus() + "\n");
            historyTextArea.appendText("Problem Summary:    " + problems.getSummary() + "\n");
            historyTextArea.appendText("Problem Description: " + problems.getDescription() + "\n");
            historyTextArea.appendText( "Voted By: "+problems.getVoteOnProblem()+" people \n");
            historyTextArea.appendText("------------------------------------------------------------------------------------------------------\n\n");
        }
    }


    /**show user's profile
     *
     * @param actionEvent
     */
    @FXML
    public void profileButtonAction(ActionEvent actionEvent) {

        profileButtonPageView();

        if(isNormal){
            //normal user's profile
            profileTextArea.setText(   "Username :  "+userMain.getUsername()+'\n');
            profileTextArea.appendText("NID           :  "+userMain.getUserAddress().getNID()+'\n'+'\n');
            profileTextArea.appendText("Village             : "+userMain.getUserAddress().getVillage()+'\n');
            profileTextArea.appendText("Union Council : "+userMain.getUserAddress().getUnionCouncil()+'\n');
            profileTextArea.appendText("Upajilla            : "+userMain.getUserAddress().getUpazilla()+'\n');
            profileTextArea.appendText("District            : "+userMain.getUserAddress().getDistrict()+'\n');

        }
        else
        {
            //elite user's profile
            profileTextArea.setText(   "Username :  "+userMain.getUsername()+'\n');
            profileTextArea.appendText("NID           :  "+userMain.getEliteUsersProfile().getNID()+'\n'+'\n');
            profileTextArea.appendText("Designation  : "+userMain.getEliteUsersProfile().getDesignation()+'\n');
            profileTextArea.appendText("Working Area : "+userMain.getEliteUsersProfile().getWorkingArea()+'\n');
        }

    }

    /**
     * loads necessary scene for profile
     */
    public void profileButtonPageView()
    {
       //System.out.println("profile btn pressed");
        eliteUsersTableView.setVisible(false);
        tableView.setVisible(false);
        othersTableView.setVisible(false);
        profileTextArea.setVisible(true);
        historyTextArea.setVisible(false);
        homeTextArea.setVisible(false);
        villageButtonPress = false;
        homeButtonPressForElite = false;


        homePageLabel.setText("Profile");
        complainSummaryTextField.setVisible(false);
        problemTypeChoiceBox.setVisible(false);
        problemScopeChoiceBox.setVisible(false);
        villageButtonBar.setVisible(false);
        saveButton.setVisible(false);
        filePath.setVisible(false);
        uploadFile.setVisible(false);
    }

    /**go to setting change page
     *
     * @param actionEvent
     */
    @FXML
    public void settingsButtonAction(ActionEvent actionEvent) {
        try {
            villageButtonPress = false;
            homeButtonPressForElite = false;

            userMain.showSignUpPage(UserSettingsChange);
        }catch (Exception e)
        {
            System.out.println("Error loading settings page");
        }
    }

    /**close the connection between server and client
     *
     * @param actionEvent
     */
    @FXML
    public void logoutButtonAction(ActionEvent actionEvent) {
        try {
            villageButtonPress = false;
            homeButtonPressForElite = false;

            String[] clientActionInfo = new String [2];
            clientActionInfo[0]=LogoutButtonPressed;
            clientActionInfo[1]=userMain.getUsername();
            WriteThreadClient writeThreadClient=new WriteThreadClient(userMain.getClientNetworkUtil(),clientActionInfo)  ;

            userMain.showSignInPage();
            i=1;
            while(true)
            {
                if(!writeThreadClient.thr.isAlive()){
                    userMain.getClientNetworkUtil().closeConnection();break;}
            }

        }catch (Exception e)
        {
            System.out.println("Error loading SignIn page");
        }
    }

    @FXML
    public void successStoriesButtonAction(ActionEvent actionEvent) {
    }

    /** show user's own village statistics or elite user's own working area's statistics based on
     * a particular problem type
     * @param actionEvent
     */
    @FXML
    public void statisticsButtonAction(ActionEvent actionEvent) {
        Stage stage = new Stage();
        Scene scene = new Scene(new Group());
        stage.setTitle("Village Problem Statistics");
        stage.setWidth(500);
        stage.setHeight(500);
        stage.setTitle("Village Problems Statistics");
        ArrayList<ProblemsOfVillage> problemsOfVillageStatistics;
        if (isNormal)
            problemsOfVillageStatistics = userMain.getProblemsOfVillagesStatistics();
        else problemsOfVillageStatistics = userMain.getEliteTableArrayList();
        Hashtable<String, Integer> voteCountHashtable = new Hashtable<>();
        if (problemsOfVillageStatistics != null) {
            for (ProblemsOfVillage p : problemsOfVillageStatistics) {
                String key = p.getProblemType();
                int val = Integer.parseInt(p.getVote());
                if (!voteCountHashtable.containsKey(p.getProblemType())) {
                    voteCountHashtable.put(key, val);
                } else {
                    voteCountHashtable.put(key, val + voteCountHashtable.get(p.getProblemType()).intValue());
                }
            }

            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
            for (String s : observableListOfProblemType) {
                if (voteCountHashtable.containsKey(s))
                    pieChartData.add(new PieChart.Data(s, voteCountHashtable.get(s)));
                else pieChartData.add(new PieChart.Data(s, 0));
            }

            PieChart chart = new PieChart(pieChartData);
            chart.setTitle("Current Year 2015");

            ((Group) scene.getRoot()).getChildren().add(chart);
            stage.setScene(scene);
            stage.show();

        }

        else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Data Not found");
            alert.setHeaderText("No statistics to show");
            alert.showAndWait();
        }

    }


    /**submits problems to server and sends file attached by user
     *
     * @param actionEvent
     */
    @FXML
    public void saveButtonAction(ActionEvent actionEvent) {


        boolean emptyText=true;
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime = sdf.format(date);
        villageButtonPress = false;
        homeButtonPressForElite = false;
        AlertClass alertClass=new AlertClass();

        Problems problems = null;
        String filepath = filePath.getText();

        if(complainSummaryTextField.getText().equals("")|| homeTextArea.getText().equals(""))
        {
            emptyText=false;
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invliad Action");
            alert.setHeaderText("Write a summary or description");
            alert.showAndWait();

        }

        /**
         * It can send file below 25 MB
         */
        else if (!filepath.equals("")) {

            File myFile = new File(filepath);
                if(myFile.length()<64*1024) {
                    //new WriteThreadClient(userMain.getClientNetworkUtil(), new FileClass(fileName, filePath.getText()));
                    userMain.getClientNetworkUtil().write(new FileClass(fileName, filePath.getText()));
                    problems = new Problems(selectedProblemType, complainSummaryTextField.getText(),
                            homeTextArea.getText(), userMain.getUsername(), currentTime, selectedProblemScope, filepath);

                }

                else if(myFile.length()<=25*1024*1024){

                    try {
                        byte[] byteArray = new byte[8*1024];
                        FileInputStream fis = null;
                        fis = new FileInputStream(myFile);
                        BufferedInputStream bis = new BufferedInputStream(fis);
                        int n = -1;
                        ArrayList<FileClass> fileArrayList = new ArrayList<>();
                        while((n = bis.read(byteArray))>-1)
                        {
                            fileArrayList.add( new FileClass(fileName, byteArray));
                        }
                        userMain.getClientNetworkUtil().write(new FileClass(fileName, filePath.getText()));
                        problems = new Problems(selectedProblemType, complainSummaryTextField.getText(),
                                homeTextArea.getText(), userMain.getUsername(), currentTime, selectedProblemScope, filepath);
                        alertClass.showConfirmationAlert("Yoy have successfully submitted your problem");
                        userMain.getClientNetworkUtil().write(problems);

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

            }

            else
            {

                emptyText=false;
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Invliad Action");
                alert.setHeaderText("You cannot upload more than 25 MB");
                alert.showAndWait();

            }

            filePath.setText(null);
            fileName = null;



        }

        else {

            problems = new Problems(selectedProblemType, complainSummaryTextField.getText(),
                    homeTextArea.getText(), userMain.getUsername(), currentTime, selectedProblemScope, null);
            alertClass.showConfirmationAlert("You have successfully submitted your problem");
        }
            userMain.getClientNetworkUtil().write(problems);



        if(emptyText)
        {
        complainSummaryTextField.setText(null);
        homeTextArea.setText(null);}

    }

    public void setUserMain(UserMain userMain) {
        this.userMain = userMain;
        isNormal=!userMain.isLoginAsElite();

        if(isNormal) normalUsersHomePageView();
        else {
            eliteUsersHomePageView();
            homeButtonPressForElite = true;
            othersTableView.setVisible(false);
        }

    }



    public void setUsername(String username) {
        this.username = username;
    }
    public String getSelectedTreeView()
    {
        return selectedTreeView;
    }


    /**show user's village problems table
     *
     * @param actionEvent
     */
    @FXML
    public void myVillageButtonAction(ActionEvent actionEvent) {
        villageButtonPageView();
        String[] clientActionInfo = new String [2];

        clientActionInfo[0]=MyVillageButtonPressed;
        clientActionInfo[1]=userMain.getUsername();
        ClientNetworkUtil=userMain.getClientNetworkUtil();
        //System.out.println("my village btn pressed"+clientActionInfo[0] + clientActionInfo[1] + ClientNetworkUtil);

        new WriteThreadClient(userMain.getClientNetworkUtil(),clientActionInfo)  ;

    }

    /**
     * loads necessary scene for village table
     */
    public void villageButtonPageView()
    {
        successStoriesButton.setVisible(false);
        homeTextArea.setVisible(false);
        othersTableView.setVisible(false);
        historyTextArea.setVisible(false);
        tableView.setVisible(true);
        villageButtonPress = true;

        homeTextArea.setText("");
        homePageLabel.setText("My Village");
        complainSummaryTextField.setVisible(false);
        filePath.setVisible(false);
        uploadFile.setVisible(false);
        problemTypeChoiceBox.setVisible(false);
        problemScopeChoiceBox.setVisible(false);
        villageButtonBar.setVisible(true);
        saveButton.setVisible(false);
        profileTextArea.setVisible(false);
        if(!isNormal())
        {
            complainBoxButton.setVisible(false);
            complainSummaryTextField.setVisible(false);
            problemTypeChoiceBox.setVisible(false);
            problemScopeChoiceBox.setVisible(false);
            // ClientNetworkUtil.write(MyVillageButtonPressedElite);
        }
    }

    /**
     * set Problems in village Table
     * @param arrayList
     */
    public void setVillageProblemsInTextArea(ArrayList<ProblemsOfVillage> arrayList)
    {
        this.arrayList = arrayList;

        try
        {
            saveButton.setVisible(false);
            profileTextArea.setVisible(false);
            historyTextArea.setVisible(false);
            tableView.setVisible(true);
            homeTextArea.setVisible(false);
            homePageLabel.setVisible(false);
            problemTypeChoiceBox.setVisible(false);
            problemScopeChoiceBox.setVisible(false);
            complainSummaryTextField.setVisible(false);
            filePath.setVisible(false);
            uploadFile.setVisible(false);


            int size=observableList.size();
            if(arrayList!=null)
            observableList.addAll(arrayList);
                observableList.remove(0,size);

            if(villageButtonPress||selectedTreeView.equals(userMain.getUserAddress().getVillage()) || selectedTreeView.equals(userMain.getUserAddress().getUpazilla()) || selectedTreeView.equals(userMain.getUserAddress().getUnionCouncil()) || selectedTreeView.equals(userMain.getUserAddress().getDistrict()))
            {
               othersTableView.setVisible(false);
                tableView.setVisible(true);
                if(initTreeViewTable) {

                    initTreeViewTable = false;
                    setTableView();
                }

                tableView.setItems(observableList);
                tableView.getSelectionModel().clearSelection(0);


            }
            else
            {
                tableView.setVisible(false);
                othersTableView.setVisible(true);
                if(othersTreeViewTable)
                {
                    othersTreeViewTable=false;
                    setOthersTableView();
                }
                othersTableView.getSelectionModel().clearSelection();
                othersTableView.setItems(observableList);

            }



        }catch (Exception e)
        {
            System.out.println("error in setVillageProblemsInTextArea");
        }

    }


    /**create village problem's table
     *
     */
    public void setTableView()
    {
        TableColumn<ProblemsOfVillage, String> problemTypeCol = new TableColumn<>("Type");
        problemTypeCol.setMinWidth(120);
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
        problemPostedByCol.setMinWidth(150);
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
        voteCol.setMinWidth(120);
        voteCol.setCellValueFactory(new PropertyValueFactory<>("vote"));
        voteCol.setCellFactory(TextFieldTableCell.<ProblemsOfVillage>forTableColumn());

        TableColumn<ProblemsOfVillage, String> votedByCol = new TableColumn<>("Voted By");
        votedByCol.setMinWidth(100);
        votedByCol.setCellValueFactory(new PropertyValueFactory<>("votedBy"));
        votedByCol.setCellFactory(TextFieldTableCell.<ProblemsOfVillage>forTableColumn());

        TableColumn<ProblemsOfVillage, String> voteStatusCol = new TableColumn<>("Vote Status");
        voteStatusCol.setMinWidth(100);
        voteStatusCol.setCellValueFactory(new PropertyValueFactory<>("voteStatus"));
        voteStatusCol.setCellFactory(TextFieldTableCell.<ProblemsOfVillage>forTableColumn());


        TableColumn<ProblemsOfVillage, String> actionCol = new TableColumn<>("Give Vote");
        actionCol.setMaxWidth(170);
        actionCol.setCellValueFactory(new PropertyValueFactory<>("action"));

        Callback<TableColumn<ProblemsOfVillage, String>, TableCell<ProblemsOfVillage, String>> cellFactory =
                new Callback<TableColumn<ProblemsOfVillage, String>, TableCell<ProblemsOfVillage, String>>() {
                    @Override
                    public TableCell call( final TableColumn<ProblemsOfVillage, String> param ) {
                        final TableCell<ProblemsOfVillage, String> cell = new TableCell<ProblemsOfVillage, String>() {
                            Button btn = new Button("vote");
                            @Override
                            public void updateItem(String item, boolean empty) {
                                super.updateItem( item, empty );
                                if (empty) {
                                    setGraphic(null);
                                    setText(null);
                                }
                                else {
                                    String[] voteAction=new String [3];
                                    // action of 'Select' button click
                                    btn.setOnAction((ActionEvent event) -> {

                                                if (arrayList.get(getIndex()).getVoteStatus().equals("not voted")) {

                                                    voteAction[0]=GiveVote;
                                                    voteAction[1]=arrayList.get(getIndex()).getProblemNo();
                                                    voteAction[2]=userMain.getUsername();
                                                 //   new WriteThreadClient(userMain.getClientNetworkUtil(),voteAction);
                                                     userMain.getClientNetworkUtil().write(voteAction);
                                                    ProblemsOfVillage p=arrayList.get(getIndex());
                                                    int newVote=Integer.parseInt(p.getVote())+1;
                                                    p.setVote(String.valueOf(newVote));
                                                    p.setVoteStatus("voted");
                                                    if(p.getVotedBy().length()==0)  p.setVotedBy(userMain.getUsername());
                                                    else p.setVotedBy(p.getVotedBy()+","+userMain.getUsername());

                                                    int size=observableList.size();
                                                    observableList.addAll(arrayList);

                                                    observableList.remove(0,size);
                                                    tableView.setItems(observableList);

                                                    //btn.setText("voted");
                                                } else if (arrayList.get(getIndex()).getVoteStatus().equals("voted")) {
                                                    Alert alert = new Alert(Alert.AlertType.ERROR);
                                                    alert.setTitle("Invliad Action");
                                                    alert.setHeaderText("You Already Voted This Problem.");
                                                    //alert.setContentText("");
                                                    alert.showAndWait();
                                                }
                                            }
                                    );
                                    setGraphic(btn);
                                    setText(null);
                                }
                            }
                        };
                        return cell;
                    }
                };
        actionCol.setCellFactory(cellFactory);

        tableView.getColumns().addAll(problemTypeCol,summaryCol,descriptionCol,problemStatusCol,voteCol,problemPostedByCol,dateTimeCol,voteStatusCol, actionCol);

        tableView.setRowFactory( tv -> {
            TableRow<ProblemsOfVillage> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
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
                    scrollPane.setPrefWidth(560);
                    scrollPane.setPrefHeight(600);

                    VBox vBox = new VBox();
                    vBox.getChildren().add(scrollPane);
                    root.getChildren().add(vBox);
                    Scene scene = new Scene(root, 560, 600);
                    scene.getStylesheets().add("Users/Style.css");
                    stage.setScene(scene);
                    stage.show();
                }
            });
            return row ;
        });


    }

    /**create other village problem's table
     *
     */
    private void setOthersTableView() {

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
        problemPostedByCol.setMinWidth(150);
        problemPostedByCol.setCellValueFactory(new PropertyValueFactory<>("problemPostedBy"));
        problemPostedByCol.setCellFactory(TextFieldTableCell.<ProblemsOfVillage>forTableColumn());

        TableColumn<ProblemsOfVillage, String> dateTimeCol = new TableColumn<>("Posted On");
        dateTimeCol.setMinWidth(150);
        dateTimeCol.setCellValueFactory(new PropertyValueFactory<>("dateTime"));
        dateTimeCol.setCellFactory(TextFieldTableCell.<ProblemsOfVillage>forTableColumn());

        TableColumn<ProblemsOfVillage, String> problemStatusCol = new TableColumn<>("Status");
        problemStatusCol.setMinWidth(150);
        problemStatusCol.setCellValueFactory(new PropertyValueFactory<>("problemStatus"));
        problemStatusCol.setCellFactory(TextFieldTableCell.<ProblemsOfVillage>forTableColumn());

        TableColumn<ProblemsOfVillage, String> voteCol = new TableColumn<>("Vote");
        voteCol.setMinWidth(150);
        voteCol.setCellValueFactory(new PropertyValueFactory<>("vote"));
        voteCol.setCellFactory(TextFieldTableCell.<ProblemsOfVillage>forTableColumn());

        TableColumn<ProblemsOfVillage, String> votedByCol = new TableColumn<>("Voted By");
        votedByCol.setMinWidth(100);
        votedByCol.setCellValueFactory(new PropertyValueFactory<>("votedBy"));
        votedByCol.setCellFactory(TextFieldTableCell.<ProblemsOfVillage>forTableColumn());


        othersTableView.getColumns().addAll(problemTypeCol,summaryCol,descriptionCol,problemStatusCol,voteCol,problemPostedByCol,dateTimeCol);
        othersTableView.setRowFactory(tv -> {
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
                    scrollPane.setPrefWidth(560);
                    scrollPane.setPrefHeight(600);

                    VBox vBox = new VBox();
                    vBox.getChildren().add(scrollPane);
                    root.getChildren().add(vBox);
                    Scene scene = new Scene(root, 560, 600);
                    scene.getStylesheets().add("Users/Style.css");
                    stage.setScene(scene);
                    stage.show();
                }
            });
            return row;
        });
    }


    /**create elite user's working area table
     *
     */
    public void setEliteTableView()
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
        problemPostedByCol.setMinWidth(150);
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
        voteCol.setMinWidth(150);
        voteCol.setCellValueFactory(new PropertyValueFactory<>("vote"));
        voteCol.setCellFactory(TextFieldTableCell.<ProblemsOfVillage>forTableColumn());

        TableColumn<ProblemsOfVillage, String> votedByCol = new TableColumn<>("Voted By");
        votedByCol.setMinWidth(100);
        votedByCol.setCellValueFactory(new PropertyValueFactory<>("votedBy"));
        votedByCol.setCellFactory(TextFieldTableCell.<ProblemsOfVillage>forTableColumn());

        TableColumn<ProblemsOfVillage, String> voteStatusCol = new TableColumn<>("Vote Status");
        voteStatusCol.setMinWidth(100);
        voteStatusCol.setCellValueFactory(new PropertyValueFactory<>("voteStatus"));
        voteStatusCol.setCellFactory(TextFieldTableCell.<ProblemsOfVillage>forTableColumn());


        TableColumn<ProblemsOfVillage, String> actionCol = new TableColumn<>("Change Status");
        actionCol.setCellValueFactory(new PropertyValueFactory<>("action"));


        Callback<TableColumn<ProblemsOfVillage, String>, TableCell<ProblemsOfVillage, String>> cellFactory =
                new Callback<TableColumn<ProblemsOfVillage, String>, TableCell<ProblemsOfVillage, String>>() {
                    @Override
                    public TableCell call( final TableColumn<ProblemsOfVillage, String> param ) {
                        final TableCell<ProblemsOfVillage, String> cell = new TableCell<ProblemsOfVillage, String>() {
                            ChoiceBox<String> choiceBox=new ChoiceBox<>(observableListOfWorkingStatus);
                            int i=1;
                            @Override
                            public void updateItem(String item, boolean empty) {
                                super.updateItem( item, empty );
                                if (empty) {
                                    setGraphic(null);
                                    setText(null);
                                }
                                else {

                                    String[] workingStatusChangeAction =new String[3];
                                    choiceBox.getSelectionModel().clearSelection();
                                    choiceBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
                                        @Override
                                        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                                            selectedWorkingStatus = newValue;


                                            if(i==1){
                                            workingStatusChangeAction[1] = observableListForElite.get(getIndex()).getProblemNo();
                                            workingStatusChangeAction[0] = WORKING_STATUS_CHANGE_ACTION;
                                            workingStatusChangeAction[2] = selectedWorkingStatus;
                                            new WriteThreadClient(userMain.getClientNetworkUtil(), workingStatusChangeAction);
                                            ProblemsOfVillage p = observableListForElite.get(getIndex());
                                            p.setProblemStatus(selectedWorkingStatus);
                                          //  System.out.println(p.getProblemStatus());

                                            int size = arraylistOfElite.size();

                                            observableListForElite.addAll(arraylistOfElite);
                                            observableListForElite.remove(0, size);
                                            eliteUsersTableView.setItems(observableListForElite);
                                                tableView.setVisible(false);othersTableView.setVisible(false);
                                            }

                                            i++;


                                    }

                                });

                                    i=1;

                                    setGraphic(choiceBox);
                                    setText(null);
                                }
                            }
                        };
                        return cell;
                    }
                };
        actionCol.setCellFactory(cellFactory);
        eliteUsersTableView.getColumns().addAll(problemTypeCol,summaryCol,descriptionCol,problemStatusCol,voteCol, actionCol,problemPostedByCol,dateTimeCol);
        eliteUsersTableView.setRowFactory(tv -> {
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
                    scrollPane.setPrefWidth(560);
                    scrollPane.setPrefHeight(600);

                    VBox vBox = new VBox();
                    vBox.getChildren().add(scrollPane);
                    root.getChildren().add(vBox);
                    Scene scene = new Scene(root, 560, 600);
                    scene.getStylesheets().add("Users/Style.css");
                    stage.setScene(scene);
                    stage.show();
                }
            });
            return row;
        });
    }


    /**create a treeView of all area in the district
     *
     * @param arrayList
     */
    public void showTreeViewUser(ArrayList<DistrictData> arrayList) {

        TreeItem<String> rootNode = new TreeItem<String>("Comilla");
        TreeItem<String> upazillaNode;
        TreeItem <String> unionNode;
        TreeItem <String> villageNode;


        for(DistrictData dd : arrayList){
            String vill = dd.getVillage();
            String uniCoun = dd.getUnionCouncil();
            String upaz = dd.getUpazilla();

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
                unionNode = new TreeItem<>(uniCoun);
                upazillaNode.getChildren().add(unionNode);
                upazillaNode.setExpanded(true);
                unionNode.getChildren().add(villageNode);
                unionNode.setExpanded(true);

            }



        }

        rootNode.setExpanded(true);
        treeView.setRoot(rootNode);

        treeView.getSelectionModel().selectedItemProperty().addListener( new ChangeListener() {

            @Override
            public void changed(ObservableValue observable, Object oldValue,
                                Object newValue) {

                villageButtonPress = false;
                homeButtonPressForElite = false;
                eliteUsersTableView.setVisible(false);
                profileTextArea.setVisible(false);

                TreeItem<String> selectedItem = (TreeItem<String>) newValue;
                selectedTreeView=selectedItem.getValue();

                String[] request = new String[3];
                if(selectedItem.getValue().equals("Comilla"))
                {
                    request[0]="district";
                    request[1]=selectedItem.getValue();
                    request[2]= userMain.getUsername();
                    new WriteThreadClient(userMain.getClientNetworkUtil(),request);
                    //ClientNetworkUtil.write(clientActionInfo);

                }
                else if(selectedItem.getParent().getValue().equals("Comilla"))
                {
                    request[0]="upazilla";
                    request[1]=selectedItem.getValue();
                    request[2]= userMain.getUsername();
                    new WriteThreadClient(userMain.getClientNetworkUtil(),request);

                }

                else if(selectedItem.getParent().getParent().getValue().equals("Comilla"))

                {
                    request[0]="unionCouncil";
                    request[1]=selectedItem.getValue();
                    request[2]= userMain.getUsername();

                    new WriteThreadClient(userMain.getClientNetworkUtil(),request);

                }

                else if(selectedItem.getParent().getParent().getParent().getValue().equals("Comilla"))
                {
                    request[0]="village";
                    request[1]=selectedItem.getValue();
                    request[2]= userMain.getUsername();

                    new WriteThreadClient(userMain.getClientNetworkUtil(),request);
                    //    ClientNetworkUtil.write(clientActionInfo);

                }
            }

        });
    }

    /**
     * working with choicebox selection
     */
    public void selectionOfChoiceBox()
    {
        problemTypeChoiceBox.getSelectionModel().selectFirst();
        problemTypeChoiceBox.getSelectionModel().selectedItemProperty()
                .addListener((ObservableValue observable,
                              Object oldValue, Object newValue) -> {
                    selectedProblemType=(String)newValue;


                });

        problemScopeChoiceBox.getSelectionModel().selectFirst();
        problemScopeChoiceBox.getSelectionModel().selectedItemProperty()
                .addListener((ObservableValue observable,
                              Object oldValue, Object newValue) -> {
                    selectedProblemScope=(String)newValue;


                });

    }
    private  int i=1;


    /**
     * insert data to eliteTableView
     * @param arrayList
     */
    public void showEliteTableView(ArrayList<ProblemsOfVillage> arrayList){
        if(i==1)
        {this.arraylistOfElite=arrayList;i=0;}

        tableView.setVisible(false);
        profileTextArea.setVisible(false);
        historyTextArea.setVisible(false);
        homeTextArea.setVisible(false);

        int size=observableListForElite.size();

        if(arrayList!=null) {
            observableListForElite.addAll(arrayList);
        }
        observableListForElite.remove(0,size);
        System.out.println("inside elite table ");

        if(homeButtonPressForElite || selectedTreeView.equals(userMain.getEliteUsersProfile().getWorkingArea()))
        {
            System.out.println("inside elite table home before false");
            homeButtonPressForElite = false;
            othersTableView.setVisible(false);
            eliteUsersTableView.setVisible(true);
            System.out.println("inside elite table home");
            if(initEliteTableView) {

                initEliteTableView= false;
                setEliteTableView();
            }
            eliteUsersTableView.getSelectionModel().clearSelection();
            eliteUsersTableView.setItems(observableListForElite);
            othersTableView.setVisible(false);
        }
        else
        {
            System.out.println("inside elite table others");
            eliteUsersTableView.setVisible(false);
            othersTableView.setVisible(true);

            if(othersTreeViewTable)
            {
                othersTreeViewTable=false;
                setOthersTableView();
            }

            othersTableView.getSelectionModel().clearSelection();
            othersTableView.setItems(observableListForElite);
        }


    }

    /**
     * sends file to server
     * @param actionEvent
     */
    public void uploadFileAction(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open File");
        Stage stage = new Stage();
        File file = fileChooser.showOpenDialog(stage);
        String path = file.getAbsolutePath();

        filePath.setText(path);
        fileName = file.getName();

    }



    public boolean isNormal() {
        return isNormal;
    }
    public void setIsNormal(boolean isNormal) {
        this.isNormal = isNormal;
    }
}

