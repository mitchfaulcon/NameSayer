package name_sayer.Controller;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import name_sayer.Main;
import name_sayer.Model.AudioRating;
import name_sayer.Model.FilenameMaps;
import name_sayer.Model.Settings;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SettingsScreenController implements Initializable {

	@FXML
	private Button clearRatingsButton;
	@FXML
	private Button chooseDatabaseButton;
	@FXML
	private Button deleteAttemptsButton;
	@FXML
	private Slider volumeSlider;
	@FXML
	private RadioButton loop1Times;
	@FXML
	private RadioButton loop2Times;
	@FXML
	private RadioButton loop3Times;
	@FXML
	private RadioButton loop4Times;
	@FXML
	private RadioButton loop5Times;
	@FXML
	private ToggleGroup loopOption;
	@FXML
	private Button homeButton;
	@FXML
	private Label databaseLabel;
	@FXML
	private ProgressBar progressBar;
	private TargetDataLine targetRecordLine;
	private Service<Void> _bgThread;
	Boolean stopCapture = false;
	private Settings settings = Settings.getInstance();
	private AudioRating audioRating = AudioRating.getInstance();
	private FilenameMaps filenameMaps = FilenameMaps.getInstance();

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		//Set the selected toggle according to the last selection
		setToggle();

		//Set the database Label according to the current database
		setDatabaseLabel();

		//Change slider to last selected volume
		volumeSlider.setValue(settings.getVolume());

		//set an audio format
		AudioFormat format = new AudioFormat(44100f, 16, 1, true, false);
		final int bufferByteSize = 2048;
		try {
			//open and start the target data line
			targetRecordLine = AudioSystem.getTargetDataLine(format);
			targetRecordLine.open(format, bufferByteSize);
			targetRecordLine.start();
		} catch(LineUnavailableException e) {
			return;
		}


		//start new background thread
		_bgThread = new Service<Void>() {

			@Override
			protected Task<Void> createTask() {
				return new Task<Void>() {
					@Override
					protected Void call() throws Exception {
						int level = 0;
						byte tempBuffer[] = new byte[6000];
						try {
							//infinite loop until scene is changed
							while (!stopCapture) {
								if (targetRecordLine.read(tempBuffer, 0, tempBuffer.length) > 0) {
									//calculate the RSM level
									level = calculateRMSLevel(tempBuffer);
									updateProgress(level,70);
								}
							}
							targetRecordLine.close();
						} catch (Exception e) {

						}
						return null;
					}
				};
			}
		};
		//bind the progress bar to the background thread
		progressBar.progressProperty().bind(_bgThread.progressProperty());

		_bgThread.start();

	}

	@FXML
	private void volumeChanged(MouseEvent mouseEvent) {
		settings.changeVolume(volumeSlider.getValue());
	}

	@FXML
	private void loopOptionChanged(ActionEvent actionEvent) {
		Toggle selected = loopOption.getSelectedToggle();
		String selectedToString = selected.toString();
		if (selectedToString.contains("loop1Times")){
			settings.changeTimesToLoop(1);
		} else if (selectedToString.contains("loop2Times")){
			settings.changeTimesToLoop(2);
		} else if (selectedToString.contains("loop3Times")){
			settings.changeTimesToLoop(3);
		} else if (selectedToString.contains("loop4Times")){
			settings.changeTimesToLoop(4);
		} else if (selectedToString.contains("loop5Times")){
			settings.changeTimesToLoop(5);
		}
	}

	@FXML
	private void onClick(ActionEvent e){
		if (e.getSource().equals(homeButton)){
			changeToMainMenu();
		} else if (e.getSource().equals(chooseDatabaseButton)){
			//Window to choose new name database
			DirectoryChooser directoryChooser = new DirectoryChooser();
			directoryChooser.setTitle("Choose Name Database");
			File selectedDirectory = directoryChooser.showDialog(chooseDatabaseButton.getScene().getWindow());
			if (selectedDirectory!=null){
				settings.changeDirectory(selectedDirectory.getAbsolutePath());
				setDatabaseLabel();
			}
		} else if (e.getSource().equals(clearRatingsButton)) {
			Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you wish to clear rating files? \n This action can not be undone.", ButtonType.YES, ButtonType.NO);
			alert.getDialogPane().getStylesheets().add(getClass().getResource("dialogs.css").toExternalForm());
			alert.getDialogPane().getStyleClass().add("alertDialog");
			alert.getDialogPane().setPrefSize(400,180);
			alert.showAndWait();
			if (alert.getResult() == ButtonType.YES) {
				audioRating.clearRatingFiles();
				audioRating.clearRatings();
			}
		} else if (e.getSource().equals(deleteAttemptsButton)) {
			Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you wish to delete user recording attempts? \nThis action can not be undone.", ButtonType.YES, ButtonType.NO);
			alert.getDialogPane().getStylesheets().add(getClass().getResource("dialogs.css").toExternalForm());
			alert.getDialogPane().getStyleClass().add("alertDialog");
			alert.getDialogPane().setPrefSize(400,200);
			alert.showAndWait();
			if (alert.getResult() == ButtonType.YES) {
				deleteRecordingAttempts();
			}
		}
	}

	//calculates the RMS level of the audio signal
	//code source: https://stackoverflow.com/questions/3899585/microphone-level-in-java
	private int calculateRMSLevel(byte[] audioData) {
		long lSum = 0;
		for(int i=0; i < audioData.length; i++)
			lSum = lSum + audioData[i];

		double dAvg = lSum / audioData.length;
		double sumMeanSquare = 0d;

		for(int j=0; j < audioData.length; j++)
			sumMeanSquare += Math.pow(audioData[j] - dAvg, 2d);

		double averageMeanSquare = sumMeanSquare / audioData.length;

		return (int)(Math.pow(averageMeanSquare,0.5d) + 0.5);
	}

	//Set the selected RadioButton to the correct selection
	private void setToggle(){
		int buttonNum =  settings.getTimesToLoop();
		switch (buttonNum){
		case 1: loopOption.selectToggle(loop1Times);
		break;
		case 2: loopOption.selectToggle(loop2Times);
		break;
		case 3: loopOption.selectToggle(loop3Times);
		break;
		case 4: loopOption.selectToggle(loop4Times);
		break;
		case 5: loopOption.selectToggle(loop5Times);
		break;
		}
	}

	//Change label to display currently chosen names database
	private void setDatabaseLabel(){
		databaseLabel.setText(settings.getDirectory());
	}

	//delete all files in "/attempts" directory
	private void deleteRecordingAttempts() {
		File dir = new File(System.getProperty("user.dir") + "/Attempts");
		for(File file: dir.listFiles()) 
			if (!file.isDirectory()) {
				file.delete();
				filenameMaps.clearAttemptNames();
			}
	}

	//enable keyboard short for 'escape' key, changes scene to main menu
	@FXML
	private void onKeyReleased(KeyEvent keyEvent) {
		if (keyEvent.getCode().equals(KeyCode.ESCAPE)){
			changeToMainMenu();
		}
	}
	
	//change scene to main menu
	private void changeToMainMenu(){
		try {
			//Stop microphone recording and go back to main menu
			stopCapture = true;
			Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("name_sayer/View/MainMenu.fxml"));
			Scene home = new Scene(root, 1200, 800);
			Main.ChangeScene(home);
		} catch(IOException ex) {

		}
	}
}
