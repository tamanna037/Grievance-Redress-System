package Authority;



import Interfaces.SharedConstants;
import NetworkRelatedClass.*;
import DatabasePackage.Database;
import javafx.application.Platform;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

/**this class reads the data sent from the client in a thread
 *
 * this class reads objects from output stream and cast them
 * in appropriate class object. the objects contains data from client
 * with request. this class examines the objects and determines whether the data
 * from the client is needed to be saved in database or client is requesting info.
 * if its a request, then server writes appropriate data to the client in anew thread
 * or else its saves data in database.
 * this class implements Runnable and whenever a new object of this class is created a new thread starts.
 *
 * Only server has access to this class
 *
 */

public class ReadThreadServer implements Runnable,SharedConstants {

	//this contains socket through which server and cient is connected
	private NetworkUtil nc;
	//reference to the AuthorityMain of the running application
	private AuthorityMain authorityMain;
	//reference to the serverHomePageControoler of the running application
	private ServerHomePageController serverHomePageController;
	//through this object, the methods of database class are called
	private Database database = new Database();

	private String username = null;
	//stores the problems of a specific place after reading from database
	private ArrayList<ProblemsOfVillage> problemsOfVillageArrayList;



	/**
	 * @param nc contains the connection socket between server and a specific client
	 * @param authorityMain helps this class to maintain connection with other class
	 */

	public ReadThreadServer(NetworkUtil nc, AuthorityMain authorityMain) {
		this.nc = nc;
		this.authorityMain = authorityMain;
		serverHomePageController = authorityMain.getServerHomePageController();
		openThread();
	}

	/**
	 * starts a new thread
	 */

	public void openThread() {
		Thread thr = new Thread(this);
		thr.start();
	}


	/**
	 * This method reads data sent by the client ,whose NetworkUtil was provided and
	 * casts them appropriately to get the original object data.
	 *
	 * Object type read from output stream is examined and new data provided by the client
	 * is inserted into database. If client requests for some data server writes it in the
	 * stream. Server differentiates the requests and data from their types.
	 *
	 */
	public void run() {
		try {
			while (true) {
				Object object = nc.read();

				if (object != null) {

					if (object instanceof NewUserData) {

						NewUserData newUserData = (NewUserData) object;
						setUsername(newUserData.getusername());

						newUserDataVerification(newUserData.getNID(), newUserData.getusername(),
								newUserData.getPassword(), newUserData.getVillage());

					}
					else if (object instanceof UserLoginChecking) {

						UserLoginChecking userLoginChecking = (UserLoginChecking) object;
						setUsername(userLoginChecking.getusername());
						userLoginDataVerification(userLoginChecking.getusername(), userLoginChecking.getPassword());


					}

					else if (object instanceof Problems) {
						Problems problems = (Problems) object;
					//	System.out.println("complain : " + problems.getDescription());
						insertProblemsToDatabase(problems);
					} else if (object instanceof FileClass) {
						FileClass fileClass = (FileClass) object;
						//System.out.println("got new file : " + fileClass.getFileName());


						String filePath = "C:\\Stored Data From Client\\" + fileClass.getFileName();
						byte[] myByteArray = fileClass.getMyByteArray();
						FileOutputStream fos = new FileOutputStream(filePath);
						BufferedOutputStream bos = new BufferedOutputStream(fos);
						bos.write(myByteArray, 0, myByteArray.length);
						//System.out.println("file reading finished.");
						bos.close();


					} else if (object instanceof ArrayList) {

						ArrayList arrayList = (ArrayList) object;
						if (arrayList.get(0) instanceof FileClass) {
							String filePath = "C:\\Stored Data From Client\\" + ((FileClass) arrayList.get(0)).getFileName();
							FileOutputStream fos = new FileOutputStream(filePath);

							int n;
							byte[] buffer = new byte[8192];
							for (n = 0; n < arrayList.size(); n++)
								fos.write(((FileClass) arrayList.get(0)).getMyByteArray());
						}

					}

					else if (object instanceof String[]) {

						String[] stringArr = ((String[]) object);
						if (stringArr[0].equals(MyVillageButtonPressed)) {

							problemsOfVillageArrayList = database.getProblems("village", stringArr[1]);
							if(problemsOfVillageArrayList==null) {
								System.out.println(NoDataInYourVillage);
								new WriteThreadServer(nc, NoDataInYourVillage);
							}
							else
								new WriteThreadServer(nc, problemsOfVillageArrayList);
							//nc.write(problemsOfVillageArrayList);
						}  else if (stringArr[0].equals(MyHistoryButtonPressed)) {
							ArrayList<Problems> problemsArray = database.giveHistoryDataToClient(stringArr[1]);
							if(problemsArray==null) new WriteThreadServer(nc,NoHistory);
							//nc.write(problemsArray);
							else new WriteThreadServer(nc, problemsArray);
						} else if (stringArr[0].equals(LogoutButtonPressed)) {
							System.out.println(nc);
							nc.closeConnection();
							//System.out.println(nc);
							Platform.runLater(new Runnable() {
								@Override
								public void run() {
									serverHomePageController.takeUserToOffline(stringArr[1]);
								}
							});
						} else if (stringArr[0].equals("village") || stringArr[0].equals("upazilla") || stringArr[0].equals("unionCouncil")) {

						//	System.out.println("tree view selection data: " + stringArr[0] + " " + stringArr[1]);

							problemsOfVillageArrayList = database.getProblemsOnTreeViewSelection(stringArr[0], stringArr[1], stringArr[2]);

							if (problemsOfVillageArrayList.isEmpty())
							//nc.write(NoProblemsSubmitted);
							{
								new WriteThreadServer(nc, NoProblemsSubmitted);
							} else {
								new WriteThreadServer(nc, problemsOfVillageArrayList);
								//nc.write(problemsOfVillageArrayList);
							}
						} else if (stringArr[0].equals("district")) {

							problemsOfVillageArrayList = database.getProblemsOfDistrict(stringArr[2]);
							if (problemsOfVillageArrayList.isEmpty()) {
								//nc.write(NoProblemsSubmitted);
								new WriteThreadServer(nc, problemsOfVillageArrayList);
							} else {
								new WriteThreadServer(nc, problemsOfVillageArrayList);
								//nc.write(problemsOfVillageArrayList);
							}

						}
						else if (stringArr[0].equals(GiveVote)) {
							//System.out.println("vote given by " + stringArr[2]);
							 database.upvateVote(stringArr);

						}

						else if(stringArr[0].equals(WORKING_STATUS_CHANGE_ACTION))
						{
							database.upvateWorkingStatus(stringArr);
						}
						else if(stringArr[0].equals(HomeButtonPressed)){

							String designation = stringArr[1];String place = stringArr[2];String scope=null;

							if(designation.equals("deputy commissioner")){
								ArrayList<ProblemsOfVillage> arrayList= database.getProblemsOfDistrict();
								nc.write(arrayList);
							}else{
								if(designation.equals("uno")){
									scope = "upazilla";
								}else if(designation.equals("chairman")){
									scope = "unionCouncil";
								}else if(designation.equals("member")){
									scope = "village";
								}
								ArrayList<ProblemsOfVillage> arrayList = database.getProblemsOnTreeViewSelection(scope,place);
								nc.write(arrayList);
							}
						}

					}

					else if (object instanceof SettingsChanges) {
						SettingsChanges sc = (SettingsChanges) object;
						//System.out.println("object:"+sc.getNewPassword());
						changeSettings(sc);

					}
					else if(object instanceof String){
						String s = (String) object;
						if(s.equals(SendTreeViewData)){
							nc.write(serverHomePageController.getDistrictDataForUsers());
						}
					}



				}
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		nc.closeConnection();
	}


	/**
	 * After the problems submitted by the client was read, this method inserts those problems
	 * in database through Database class. It then checks whether action was successful.
	 * then server informs the action result (success or failure) to the client.
	 *
	 * @param problems problems details submitted by the client
	 */
	private void insertProblemsToDatabase(Problems problems) {

		String res = database.insertProblemsToDatabase(problems);
	//	System.out.println("problem inserting res: " + res);

		if (res.equals(PROBLEM_INSERTED)) {
			new WriteThreadServer(nc, PROBLEM_INSERTED);
			//nc.write(PROBLEM_INSERTED);

		} else
			new WriteThreadServer(nc, PROBLEM_INSERTION_FAILED);
		//nc.write(PROBLEM_INSERTION_FAILED);
	}

	/**
	 * checks the validity of the username and password sent by client by calling method from
	 * database.
	 *
	 * If login data matches any data from user table or eliteuser table, client is permitted to see
	 * application homepage. To show that, required data of total district, upazilla,
	 * union council, village is sent to client. Client's full identity (NID, village, unioncouncil,
	 * upazilla, district) is sent as client profile.
	 * If client is eliteuser, then all the problems of his working area is also sent.
	 * Also, for successful login of normal user, client is showed online in server home page.
	 * If login data doesn't match, then whether username was invalid or password mismatched, is informed
	 * to client.
	 *
	 * @param userName
	 * @param password
	 */
	private void userLoginDataVerification(String userName, String password) {
		String res = database.signInIsValid(userName, password);
		if (res == SuccessLogin) {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {

					if (!serverHomePageController.checkOnlineList(userName))
						serverHomePageController.setClientOnlineObservableList(userName);
				}
			});
		//	System.out.println(SuccessLogin);
			nc.write(SuccessLogin);
			//new WriteThreadServer(nc,SuccessLogin);
			//new WriteThreadServer(nc,SuccessLogin);
			if (serverHomePageController.getDistrictDataForUsers() != null) {

				//new WriteThreadServer(nc,serverHomePageController.getDistrictDataForUsers());
				nc.write(serverHomePageController.getDistrictDataForUsers());
				UserAddress userAddress = database.getAllInfoFromUsername(userName);

				try {
					Thread.sleep(20);
					//nc.write();
					new WriteThreadServer(nc, userAddress);
					//nc.write(userAddress);
				} catch (Exception e) {
					System.out.println(e);
				}

			}
			/*UserAddress userAddress = database.getAllInfoFromUsername(userName);
			System.out.println("upazilla" + userAddress.getUpazilla() + "village"+userAddress.getVillage());

			nc.write(userAddress);*/
		} else if (res == INVALID_PASSWORD) {
			new WriteThreadServer(nc, FailedLogin);
			//	nc.write(INVALID_USER_NAME);
		} else if (res == INVALID_USER_NAME) {
			res = database.signInIsValidAsElite(userName, password);
			if (res == SuccessLogin) {
				System.out.println(serverHomePageController.getDistrictDataForUsers().get(0).getVillage());

				Platform.runLater(new Runnable() {
					@Override
					public void run() {

						if (!serverHomePageController.checkOnlineList(userName))
							serverHomePageController.setClientOnlineObservableList(userName);
					}
				});
				nc.write(SUCCESS_LOGIN_AS_ELITE);
				nc.write(serverHomePageController.getDistrictDataForUsers());


//
				EliteUsersProfile eliteUsersProfile = database.getAllInfoFromUsernameOfElite(userName);
				try {
					//Thread.sleep(2000);
					//nc.write();
					new WriteThreadServer(nc, eliteUsersProfile);
				}	//nc.write(userAddress);
				 catch (Exception e) {
					System.out.println(e);
				}
				String scope = null;
				if(eliteUsersProfile.getDesignation().equals("deputy commissioner")){
					scope = "district";

					ArrayList<ProblemsOfVillage> arrayList= database.getProblemsOfDistrict();
				//	System.out.println("elite district "+arrayList.get(0).getDescription());
					nc.write(arrayList);
				}else{
					if(eliteUsersProfile.getDesignation().equals("uno")){
						scope = "upazilla";
					}else if(eliteUsersProfile.getDesignation().equals("chairman")){
						scope = "unionCouncil";
					}else if(eliteUsersProfile.getDesignation().equals("member")){
						scope = "village";
					}
					ArrayList<ProblemsOfVillage> arrayList = database.getProblemsOnTreeViewSelection(scope,eliteUsersProfile.getWorkingArea());
					nc.write(arrayList);
				//	System.out.println("elite non-district "+arrayList.get(0).getDescription());

				}

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}


			} else {
				new WriteThreadServer(nc, FailedLogin);
			}
		}
		else if(res == INVALID_PASSWORD ){
			new WriteThreadServer(nc,INVALID_PASSWORD);
			//nc.write(INVALID_PASSWORD);
		}
	}

	/**
	 * If a new aacount is created, data provided by the user is checked. If given NID is valid,
	 * and NID matches given village and no previously created account from this NID exists, and client
	 * provides a unique username, client is given permission to open the account.
	 *
	 * If account previously exists, or NID and village does not match, username is not unique, client
	 * is informed the type of error occured.
	 *
	 * if account is successfully created, server sends required data of total district, upazilla,
	 * union council, village. Client's full identity (NID, village, unioncouncil,
	 * upazilla, district) is sent as client profile.
	 *
	 * @param nid provided by client while creating account
	 * @param userName provided by client while creating account
	 * @param password provided by client while creating account
	 * @param village clients village provided by client while creating account
	 */

	private void newUserDataVerification(String nid, String userName, String password, String village) {

		String res = creatingAccountIsValid(nid, userName, password, village);
		if (res == ACCOUNT_INFO_VERIFIED) {
			if (database.addUserToUsersTable(nid, userName, password, village)) {
				//System.out.println(ACCOUNT_CREATED);
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						if (!serverHomePageController.checkOnlineList(userName))
							serverHomePageController.setClientOnlineObservableList(userName);
					}
				});

				nc.write(ACCOUNT_CREATED);
				//new WriteThreadServer(nc,ACCOUNT_CREATED);

				if (serverHomePageController.getDistrictDataForUsers() != null)
					nc.write(serverHomePageController.getDistrictDataForUsers());

				UserAddress userAddress = database.getAllInfoFromUsername(userName);
				//nc.write(userAddress);
				new WriteThreadServer(nc, userAddress);
			}
		} else if (res == INVALID_NID) {
			//System.out.println(INVALID_NID);
			new WriteThreadServer(nc, INVALID_NID);
			//nc.write(INVALID_NID);
		} else if (res == ACCOUNT_EXISTS) {
			//System.out.println(ACCOUNT_EXISTS);
			//nc.write(ACCOUNT_EXISTS);
			new WriteThreadServer(nc, ACCOUNT_EXISTS);
		} else if (res == INVALID_USER_NAME) {
			//System.out.println(INVALID_USER_NAME);
			//nc.write(INVALID_USER_NAME);
			new WriteThreadServer(nc, INVALID_USER_NAME);
		} else if (res == INVALID_VILLAGE_NAME) {
			//System.out.println(INVALID_VILLAGE_NAME);
			//nc.write(INVALID_VILLAGE_NAME);
			new WriteThreadServer(nc, INVALID_VILLAGE_NAME);
		}
	}

	private String creatingAccountIsValid(String nid, String userName,
										  String password, String village) {


		if (database.nidIsValid(nid) == INVALID_NID) return INVALID_NID;
		if (database.nidIsValid(nid) == ACCOUNT_EXISTS) return ACCOUNT_EXISTS;
		if (database.userNameExists(userName)) return INVALID_USER_NAME;
		if (!database.villageIsValid(village, nid)) return INVALID_VILLAGE_NAME;

		//System.out.println("server read thread" + ACCOUNT_INFO_VERIFIED);
		return ACCOUNT_INFO_VERIFIED;
	}


	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * If previous password matches, new password is saved into database.
	 * Else cliented is informed of wrong previous password and no update is done.
	 * @param sc this contains the type of client(elite or normal), the new password client want to use,
	 *           and the previous password
	 */

	private void changeSettings(SettingsChanges sc) {

		String currentUserName = sc.getCurrentUserName();
		String newUserName = sc.getNewUserName();
		String currentPassword = sc.getCurrentPassword();
		String newPassword = sc.getNewPassword();
		//System.out.println("new:"+newPassword);
		boolean isLoginElite=sc.isLoginElite();
		//System.out.println("change " +currentPassword+ currentUserName+ isLoginElite);
		//database.signInIsValid(currentUserName, currentPassword).equals(SuccessLogin)  && !
		if (!sc.isLoginElite()) {
			if (!newPassword.equals("")) {
				if (database.updatePassword(currentUserName, newPassword).equals(PASSWORD_UPDATED)) {
					new WriteThreadServer(nc, PASSWORD_UPDATED);
					//nc.write(PASSWORD_UPDATED);
				} else {
					new WriteThreadServer(nc, PASSWORD_UPDATE_FAILED);
					//	nc.write(PASSWORD_UPDATE_FAILED);
				}
			}

		}

		else if(isLoginElite){
			//System.out.println("changing");
			if (!newPassword.equals("")) {
				if (database.updatePasswordForElite(currentUserName, newPassword).equals(PASSWORD_UPDATED)) {
					new WriteThreadServer(nc, PASSWORD_UPDATED);
					//nc.write(PASSWORD_UPDATED);
				} else {
					new WriteThreadServer(nc, PASSWORD_UPDATE_FAILED);
					//	nc.write(PASSWORD_UPDATE_FAILED);
				}
			}

		}
		else{

			new WriteThreadServer(nc, CURRENT_PASSWORD_MISMATCH);
			//nc.write(CURRENT_PASSWORD_MISMATCH);
		}
	}
}



