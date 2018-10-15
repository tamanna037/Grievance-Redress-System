package CommonClass;

import javafx.scene.control.Alert;

/**
 * Shows two types of alert: Error Alert and Confirmation alert with the msg
 * set as a parameter.
 *
 * Server and Client both have access to this class.
 */
public class AlertClass {

    /**
     * shows an error alert
     * @param errorMsg this message is showed with alert
     */
    public void showErrorAlert(String errorMsg){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Incorrect Credentials");
        alert.setHeaderText("Incorrect Credentials");
        alert.setContentText(errorMsg);
        alert.showAndWait();
    }

    /**
     * shows an confirmation alert
     * @param confirmationMsg this message is showed with alert
     */
    public void showConfirmationAlert(String confirmationMsg){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("CONFIRMATION");
        alert.setHeaderText("ACTION SUCCESS");
        alert.setContentText(confirmationMsg);
        alert.showAndWait();
    }
}
