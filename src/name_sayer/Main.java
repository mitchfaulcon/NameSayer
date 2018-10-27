package name_sayer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;


public class Main extends Application {

	private static Stage _stage;
	private Scene Menu;



	@Override
	public void start(Stage primaryStage) throws Exception{
		
		CreateFilesPlayedTxt();
		
		//set event handler for when user presses 'x' button on stage.
		primaryStage.setOnCloseRequest(evt -> {
			// prevent window from closing
			evt.consume();

			// execute own shutdown procedure
			shutdown(primaryStage);
		});
		
		if (checkFirstLaunch()) {
			Parent root = FXMLLoader.load(getClass().getResource("View/OpeningScene.fxml"));
			Menu = new Scene(root, 1200, 800);

		} else {


			//load practice GUI
			Parent root = FXMLLoader.load(getClass().getResource("View/MainMenu.fxml"));
			Menu = new Scene(root,1200, 800);
		}
		
		primaryStage.setTitle("NameSayer");

		primaryStage.setScene(Menu);

		primaryStage.show();
		primaryStage.setResizable(false);


		_stage = primaryStage;


	}

	//change scene
	public static void ChangeScene(Scene scene) {
		_stage.setScene(scene);
		_stage.sizeToScene();
	}

	public static void main(String[] args) {
		launch(args);
	}


	//double check user wants to close NameSayer when they press 'x' button of stage.
	private void shutdown(Stage mainWindow) {
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you wish to quit NameSayer?", ButtonType.YES, ButtonType.NO);
		alert.getDialogPane().getStylesheets().add(getClass().getResource("Controller/dialogs.css").toExternalForm());
		alert.getDialogPane().getStyleClass().add("alertDialog");
		if (alert.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
			//Quit
			System.exit(1);
		}
	}

	//this method returns true if it is the user's first time launching the programme
	//this indicates that the user has not gone throught the tutorial
	private boolean checkFirstLaunch() {
		File launchFile = new File(".ProgrammeLaunch.txt");
		return !launchFile.exists();
	}
	
	//create FilesPlayed.txt file
	private void CreateFilesPlayedTxt() {
		try	{
			FileWriter fw = new FileWriter(".FilesPlayed.txt", true);
			
			fw.close();
			
		} catch (IOException e) {
		}
	}
}
