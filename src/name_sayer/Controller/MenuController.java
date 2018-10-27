package name_sayer.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import name_sayer.Main;

import java.io.IOException;

public class MenuController {

    @FXML
    private Button helpButton;
    @FXML
    private Button quitButton;
    @FXML
    private Button settingsButton;
    @FXML
    private Button startButton;

    @FXML
    private void onClick(ActionEvent e) {
        if (e.getSource().equals(startButton)){
            //Change to practice scene
            try {
                Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("name_sayer/View/Practice.fxml"));
                Scene practiceList = new Scene(root, 1200, 800);
                Main.ChangeScene(practiceList);
            } catch(IOException ex) {

            }
        } else if (e.getSource().equals(settingsButton)){
            LoadSettings();
        } else if (e.getSource().equals(quitButton)){
        	quitConfirmation();
        } else if (e.getSource().equals(helpButton)){
            //Load help menu
            try {
                Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("name_sayer/HelpMenu/Help1.fxml"));
                Scene helpMenu = new Scene(root, 1200, 800);
                Main.ChangeScene(helpMenu);
            } catch(IOException ex1) {

            }
        }
    }
    
    private void quitConfirmation() {
        //Confirmation to quit
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you wish to quit NameSayer?", ButtonType.YES, ButtonType.NO);
        alert.getDialogPane().getStylesheets().add(getClass().getResource("dialogs.css").toExternalForm());
        alert.getDialogPane().getStyleClass().add("alertDialog");
        if (alert.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
            //Quit
            System.exit(1);
        }
    }

    private void LoadSettings() {
        //Change to settings screen
        try {
            Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("name_sayer/View/SettingsScreen.fxml"));
            Scene micTest = new Scene(root, 1200, 800);
            Main.ChangeScene(micTest);
        } catch(IOException ex) {

        }
    }

    @FXML
    private void onKeyReleased(KeyEvent keyEvent) {
        if (keyEvent.getCode().equals(KeyCode.ESCAPE)){
            quitConfirmation();
        }
    }
}
