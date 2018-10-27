package name_sayer.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import name_sayer.Main;
import name_sayer.Model.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;


public class FullPracticeListController implements Initializable {

    @FXML
    private Button listenButton;
    @FXML
    private ListView<String> attemptList;
    @FXML
    private ListView<String> _practiceList;
    @FXML
    private Button homeButton;
    @FXML
    private Button playButton;
    @FXML
    private Button recordButton;
    @FXML
    private Button nextButton;
    @FXML
    private Button saveButton;
    @FXML
    private Label currentPlaying;
    @FXML
    private Button rateButton;
    @FXML
    private Button previousButton;
    @FXML
    private Button compareButton;
    @FXML
    private Button stopRecordingButton;
    @FXML
    private ProgressIndicator progressIndicator;

    @FXML
    private ImageView upButtonImage;
    @FXML
    private ImageView downButtonImage;


    private String _currentItem;
    private String _currentAttempt;
    private int _index;
    private AudioRating audioRating = AudioRating.getInstance();
    private String _latestCreation;
    private PlayCreation playCreation = PlayCreation.getInstance();
    private RecordAttempt recordAttempt = RecordAttempt.getInstance();
    private NamesList namesList = NamesList.getInstance();
    private Settings settings = Settings.getInstance();
    private FilenameMaps filenameMaps = FilenameMaps.getInstance();
    private static HashMap<String, String> _attemptNames;

    //Recording logic variables
    private boolean _saveClicked = true;
    private boolean _attemptRecorded = false;
    private boolean goToNext;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        _attemptNames = filenameMaps.getAttemptMap();

        initialisePracticeList();

        previousButton.setDisable(true);

        //disable 'next' button if there is only 1 name user wishes to practice
        if (_practiceList.getItems().size() == 1) {
            nextButton.setDisable(true);
            previousButton.setDisable(true);
        }

        //read ratings from files
        audioRating.ReadCreations();

        //set labels
        setLabelText(_currentItem);

        //Update list of past attempts for current name
        updateAttemptList(_currentItem);

        //Set initial visibilities of buttons
        listenButton.setDisable(true);
        stopRecordingButton.setDisable(true);
        compareButton.setDisable(true);
        progressIndicator.setVisible(false);
        saveButton.setDisable(true);

        //Load images for buttons
        try {
            BufferedImage bufferedImageUp = ImageIO.read(new File("Resources/UpArrow.png"));
            Image upArrow = SwingFXUtils.toFXImage(bufferedImageUp, null);
            this.upButtonImage.setImage(upArrow);

            BufferedImage bufferedImageDown = ImageIO.read(new File("Resources/DownArrow.png"));
            Image downArrow = SwingFXUtils.toFXImage(bufferedImageDown, null);
            this.downButtonImage.setImage(downArrow);
        }
        catch (IOException e){
        	
        }
    }


    @FXML
    private void onClick(ActionEvent e) {
        listenButton.setDisable(true);
        compareButton.setDisable(true);
        if (e.getSource().equals(homeButton)) {
            backToPracticeMenu();

        } else if (e.getSource().equals(nextButton)) {
            nextName("Next");

        } else if (e.getSource().equals(previousButton)) {
            nextName("Previous");

        } else if (e.getSource().equals(playButton)) {
            PlayCreation();
        } else if (e.getSource().equals(recordButton)) {
            _attemptRecorded = true;
            boolean overwrite = true;

            //Alert if the save button has not been clicked yet asking if they want to override
            if (!_saveClicked) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Practice attempt has not been saved.\nDo you wish to overwrite with a new attempt?", ButtonType.YES, ButtonType.NO);
                alert.getDialogPane().getStylesheets().add(getClass().getResource("dialogs.css").toExternalForm());
                alert.getDialogPane().getStyleClass().add("alertDialog");
                alert.getDialogPane().setPrefSize(430,180);
                alert.showAndWait();

                if (alert.getResult() == ButtonType.YES) {
                    deleteLatestCreation();
                } else {
                    overwrite = false;
                }
            }
            if (overwrite) {
                //Get current date & time to use in attempt naming convention
                SimpleDateFormat formatter = new SimpleDateFormat(" (dd-MM-yyyy_HH-mm-ss)");
                Date date = new Date();
                String dateAndTime = formatter.format(date);

                //Start recording mic
                recordAttempt.recordVoice(_currentItem);
                _latestCreation = _currentItem + dateAndTime;
                _currentAttempt = _latestCreation;
                _saveClicked = false;

                //Enable/disable correct buttons
                setButtons(true);
            }
        } else if (e.getSource().equals(saveButton)) {
            //Put lastest attempt into attempts list
            _saveClicked = true;
            _attemptNames.put(_latestCreation,_latestCreation+".wav");
            updateAttemptList(_currentItem);
            saveButton.setDisable(true);

        } else if (e.getSource().equals(listenButton)){
            if (_currentAttempt!=null) {
                //Play selected past attempt
                playCreation.playRecordingAttempt(_currentAttempt);
                attemptList.getSelectionModel().clearSelection();
            }
        } else if (e.getSource().equals(rateButton)) {

            audioRating.setItemToRate(_currentItem);
            //Rate current name
            try {
                Stage rateMenuStage = new Stage();
                Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("name_sayer/View/RateName.fxml"));
                Scene rateMenu = new Scene(root,600, 400);
                rateMenuStage.setScene(rateMenu);
                rateMenuStage.initModality(Modality.APPLICATION_MODAL);
                rateMenuStage.show();

            } catch(IOException ex) {

            }


        } else if (e.getSource().equals(compareButton)) {
            /*
             * loop between database recording + attempt x number of times.
             */
            int numOfLoops = settings.getTimesToLoop();

            //Loop between name and attempt in background thread
            Task task = new Task() {
                @Override
                protected Object call() throws Exception {
                    for (int i=0; i<numOfLoops; i++) {
                        playCreation.playName(_currentItem);
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e1) {
                        }
                        playCreation.playRecordingAttempt(_currentAttempt);
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e1) {
                        }
                    }
                    return null;
                }
            };
            new Thread(task).start();

        } else if (e.getSource().equals(stopRecordingButton)) {
            recordAttempt.stopRecording();

            //Enable/disable correct buttons
            setButtons(false);
        }
    }

    private void setButtons(boolean recording){
        //Set all the buttons on the GUI depending if recording or not
        stopRecordingButton.setDisable(!recording);
        recordButton.setDisable(recording);
        progressIndicator.setVisible(recording);
        saveButton.setDisable(recording);
        rateButton.setDisable(recording);

        nextButton.setDisable(recording);
        previousButton.setDisable(recording);

        //Enable/disable up and down buttons depending on the current name
        if (_index==0){
            previousButton.setDisable(true);
        }
        if (_index==_practiceList.getItems().size()-1){
            nextButton.setDisable(true);
        }
    }

    @FXML
    private void handleAttemptListClicked(MouseEvent arg0){
        //change current attempt to selection and enable compare button
        compareButton.setDisable(false);
        _currentAttempt = attemptList.getSelectionModel().getSelectedItem();
        if (_currentAttempt!=null) {
            listenButton.setDisable(false);
        }
    }

    private void PlayCreation() {
        //Play the current name
        if (_currentItem != null) {
            playCreation.playName(_currentItem);
        }
    }

    private void deleteLatestCreation () {
        //Delete the latest creation
        try {
            Files.deleteIfExists(Paths.get(System.getProperty("user.dir") + "/Attempts/" + _latestCreation + ".wav"));
        } catch (IOException e) {

        }
    }

    private void initialisePracticeList(){
        //Get the list of the practice names that exist in the database
        ArrayList<String> tempList = namesList.getGoodItems();
        ObservableList<String> observableList = FXCollections.observableArrayList(tempList);

        //Set display of names
        _practiceList.setItems(observableList);

        //Set index and selected items
        _index = 0;
        _currentItem = _practiceList.getItems().get(_index);
        _practiceList.getSelectionModel().select(_index);

        //Add event handler so list is not clickable
        _practiceList.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                mouseEvent.consume();
            }
        });
    }

    private void updateAttemptList(String item){
        attemptList.getItems().clear();

        //Iterate through attempt hashmap
        Set set = _attemptNames.entrySet();
        Iterator iterator = set.iterator();
        while(iterator.hasNext()) {
            Map.Entry mentry = (Map.Entry)iterator.next();
            if (mentry.getValue().toString().toLowerCase().startsWith(item.toLowerCase() + " (")){

                String tmpString = (String) mentry.getKey();
                tmpString = tmpString.substring(item.length()+1);

                //only add if first index of ')' is last char
                if (tmpString.indexOf(")") == tmpString.length()-1){
                    attemptList.getItems().add((String) mentry.getKey());
                }
            }
        }
    }

    private void saveWarning() {
        if (_attemptRecorded && !_saveClicked) {
            //warning dialog box
            Alert alert = new Alert(Alert.AlertType.WARNING, "Practice attempt has not been saved.\nDo you wish to continue to next name?", ButtonType.YES, ButtonType.NO);
            alert.getDialogPane().getStylesheets().add(getClass().getResource("dialogs.css").toExternalForm());
            alert.getDialogPane().getStyleClass().add("alertDialog");
            alert.showAndWait();
            if (alert.getResult() == ButtonType.YES) {
                //delete practice attempt
                deleteLatestCreation();
            } else {
                goToNext = false;
            }
        }
    }

    private void updateToNextItem() {
        //Update Attempt Lists in case directory was deleted
        filenameMaps.clearAttemptNames();
        String attemptsDir = System.getProperty("user.dir") + "/Attempts";
        File attemptsDirectory = new File(String.valueOf(attemptsDir));
        if (!attemptsDirectory.exists()) {
            attemptsDirectory.mkdir();
        }
        filenameMaps.listNames(attemptsDir);
        _attemptNames = filenameMaps.getAttemptMap();


        //Change current name and selection
        _currentItem = _practiceList.getItems().get(_index);
        _practiceList.getSelectionModel().select(_index);

        //Disable next/previous buttons if index is first or last
        if (_index == 0) {
            previousButton.setDisable(true);
        } else if (_index == _practiceList.getItems().size() -1) {
            nextButton.setDisable(true);
        }

        _saveClicked = true;

        setLabelText(_currentItem);
        updateAttemptList(_currentItem);
    }

    //Sets the currently playing label to have capital letters in all the first letters of names
    private void setLabelText(String currentItem){
        StringBuilder properCaseName = new StringBuilder();

        //Split names into individual strings
        String[] splitNames = currentItem.split("[\\s+-]");
        for (String name: splitNames){
            //Convert lowercase string to char array, capitalise first letter
            char[] chars = name.toLowerCase().toCharArray();
            chars[0] = Character.toUpperCase(chars[0]);
            properCaseName.append(new String(chars)).append(" ");
        }

        currentPlaying.setText(properCaseName.toString());
    }

    @FXML
    private void onKeyReleased(KeyEvent keyEvent) {
        //Keyboard shortcuts
        if (keyEvent.getCode().equals(KeyCode.ESCAPE)){
            homeButton.fire();
        } else if (keyEvent.getCode().equals(KeyCode.UP)){
            previousButton.fire();
        } else if (keyEvent.getCode().equals(KeyCode.DOWN)){
            nextButton.fire();
        }
    }

    private void backToPracticeMenu(){
        //Confirmation asking to quit
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you wish to stop practicing?", ButtonType.YES, ButtonType.CANCEL);
        alert.getDialogPane().getStylesheets().add(getClass().getResource("dialogs.css").toExternalForm());
        alert.getDialogPane().getStyleClass().add("alertDialog");
        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {
            //change scenes
            try {
                Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("name_sayer/View/Practice.fxml"));
                Scene home = new Scene(root, 1200, 800);
                Main.ChangeScene(home);
            } catch (IOException ex) {

            }
        }
    }

    private void nextName(String nextOrPrevName) {
        goToNext = true;
        //Check if any recording attempt has not been saved
        saveWarning();

        if (goToNext) {
            //Change index depending if next or previous was clicked
            if (nextOrPrevName.equals("Next")) {
                _index++;
                previousButton.setDisable(false);
            } else {
                _index--;
                nextButton.setDisable(false);
            }
            compareButton.setDisable(true);
            updateToNextItem();
        }
    }
}