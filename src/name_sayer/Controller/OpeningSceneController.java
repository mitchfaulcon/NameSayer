package name_sayer.Controller;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import name_sayer.Main;
import name_sayer.HelpMenu.Help;

public class OpeningSceneController {
	@FXML
	private Button beginButton;
	@FXML
	private Button skipButton;
	private Help help = Help.getInstance();

	@FXML
	private void onClick(ActionEvent e){
		//Open help screen for tutorial
		if (e.getSource().equals(beginButton)){
			try {
				Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("name_sayer/HelpMenu/Help1.fxml"));
				Scene help = new Scene(root, 1200, 800);
				Main.ChangeScene(help);
			} catch(IOException ex) {

			}
		} else if (e.getSource().equals(skipButton)) {
			try {
				Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("name_sayer/View/MainMenu.fxml"));
				Scene home = new Scene(root, 1200, 800);
				Main.ChangeScene(home);
				help.createFileOnLaunch();
			} catch(IOException ex) {

			}
		}
	}
}
