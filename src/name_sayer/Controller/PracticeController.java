package name_sayer.Controller;

import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import name_sayer.Main;
import name_sayer.Model.*;

import java.io.*;
import java.net.URL;
import java.util.*;


public class PracticeController implements Initializable {
	@FXML
	private TextField searchBar;
	@FXML
	private Button openTxtButton;
	@FXML
	private Button practiceButton;
	@FXML
	private Button addButton;
	@FXML
	private Button removeAllButton;
	@FXML
	private Button randomiseButton;
	@FXML
	private Button backButton;
	@FXML
	private Button playButton;
	@FXML
	private Button rateButton;
	@FXML
	private Button addFullNameButton;
	@FXML
	private Button editButton;
	@FXML
	private ListView<String> creationList;
	@FXML
	private ListView<String> practiceList;
	@FXML
	private ListView<String> attemptsList;
	@FXML
	private TextField inputFullNameTextField;

	private String _selectedCreation;
	private String _selectedPractice;
	private String _selectedAttempt;
	private static ArrayList<String> _staticPracticeList = new ArrayList<>();
	private static ArrayList<String> _staticCreationList = new ArrayList<>();
	private Settings settings = Settings.getInstance();
	private AudioRating audioRating = AudioRating.getInstance();
	private PlayCreation playCreation = PlayCreation.getInstance();
	private NamesList namesList = NamesList.getInstance();
	private FilenameMaps filenameMaps = FilenameMaps.getInstance();
	private ArrayList<String> fileNames = new ArrayList<>();


	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {

		//Get names directory from settings
		String namesDir = settings.getDirectory();


		//Reset list views
		creationList.getItems().clear();
		practiceList.getItems().clear();
		filenameMaps.clearCreationNames();
		filenameMaps.clearAttemptNames();

		//Populate hashmaps with names in database
		filenameMaps.listNames(namesDir);

		_staticCreationList.clear();


		//Add names from hashmap to creation listview
		Set creationSet = filenameMaps.getCreationNamesEntrySet();
		for (Object aSet : creationSet) {
			Map.Entry mentry = (Map.Entry) aSet;

			String name = (String) mentry.getKey();
			if (!name.contains("(")) {
				creationList.getItems().add((String) mentry.getKey());
			}
			_staticCreationList.add((String) mentry.getKey());
		}

		//Create directory for practice attempts if it does not exist
		String attemptsDir = System.getProperty("user.dir") + "/Attempts";
		File attemptsDirectory = new File(String.valueOf(attemptsDir));//"file:///"+System.getProperty("user.dir") + "/Names"));
		if (!attemptsDirectory.exists()) {
			attemptsDirectory.mkdir();
		}
		filenameMaps.listNames(attemptsDir);

		//Add names from hashmap to attempts listview
		Set attemptSet = filenameMaps.getAttemptsEntrySet();
		for (Object aSet : attemptSet) {
			Map.Entry mentry = (Map.Entry) aSet;
			attemptsList.getItems().add((String) mentry.getKey());
		}

		//Redo ratings array
		_staticPracticeList.clear();
		audioRating.clearRatings();

		//read ratings from files
		audioRating.ReadCreations();

		alphabetiseList(creationList);
		alphabetiseList(attemptsList);

		editButton.setDisable(true);
		checkListForButtons();

		//Allows for textfield within listview for editing
		practiceList.setCellFactory(TextFieldListCell.forListView());

		//User can select multiple items in creation list with control click
		creationList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		//Disable buttons when in list views so that arrow keys do nothing
		creationList.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				event.consume();
			}
		});
		practiceList.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				event.consume();
			}
		});
		attemptsList.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				event.consume();
			}
		});
	}

	@FXML
	private void handleCreationsListMouseClick(MouseEvent arg0) {
		//Get current selection in creations list
		_selectedCreation = creationList.getSelectionModel().getSelectedItem();
		practiceList.getSelectionModel().clearSelection();
		attemptsList.getSelectionModel().clearSelection();
		_selectedPractice = null;
		_selectedAttempt = null;

		//Add to practice list if item is double clicked
		if(arg0.getClickCount()==2){
			addToPractice();
			checkListForButtons();
		}

		editButton.setDisable(true);
	}

	@FXML
	private void handlePracticeListMouseClick(MouseEvent arg0) {
		//Get current selection in to practice list
		_selectedPractice = practiceList.getSelectionModel().getSelectedItem();
		creationList.getSelectionModel().clearSelection();
		attemptsList.getSelectionModel().clearSelection();
		_selectedCreation = null;
		_selectedAttempt = null;
		practiceList.setEditable(false);

		// enable/disable editing depending on name selected
		if (!_staticCreationList.contains(_selectedPractice)){
			editButton.setDisable(false);
		} else {
			editButton.setDisable(true);
		}

		//Remove from practice list if item is double clicked
		if(arg0.getClickCount()==2){
			removeFromPractice();
			alphabetiseList(creationList);
			checkListForButtons();

			editButton.setDisable(true);
		}
	}

	@FXML
	private void handleAttemptsListMouseClick(MouseEvent arg0) {
		//Get current selection in attempts list
		_selectedAttempt = attemptsList.getSelectionModel().getSelectedItem();

		//Clear other lists selections
		creationList.getSelectionModel().clearSelection();
		practiceList.getSelectionModel().clearSelection();
		_selectedCreation = null;
		_selectedPractice = null;

		editButton.setDisable(true);
	}

	@FXML
	private void onClick(ActionEvent e){
		if (e.getSource().equals(practiceButton)){
			//perform steps required before changing to practice scene
			copyItems();
			noItemsWarning();
			changeToPractice();

		} else if (e.getSource().equals(addButton)){
			//If more than one name is selected add multiple to list
			if (creationList.getSelectionModel().getSelectedIndices().size()>1) {
				addMultipleToPractice(creationList.getSelectionModel().getSelectedItems());
			} else {
				addToPractice();
			}

			//Enable/disable correct buttons
			checkListForButtons();

		} else if (e.getSource().equals(removeAllButton)){

			removeAllItemsFromPractice();
			alphabetiseList(creationList);

			//Enable/disable correct buttons
			checkListForButtons();

		} else if (e.getSource().equals(randomiseButton)){
			randomisePracticeList();

		} else if (e.getSource().equals(backButton)) {
			backToMainMenu();

		} else if (e.getSource().equals(playButton)){

			//Check if selected item is from database, or attempts list
			if (_selectedCreation!=null||_selectedPractice!=null){
				//Check if from creation list or practice list
				if (_selectedCreation!=null){
					playCreation.playName(_selectedCreation);
				} else {
					playCreation.playName(_selectedPractice);
				}
			} else if (_selectedAttempt != null) {
				playCreation.playRecordingAttempt(_selectedAttempt);

			} else {
				//Alert if no names selected
				Alert alert = new Alert(AlertType.WARNING, "No recording has currently been selected.\n\nPlease select a recording from 'Name Recordings', 'Practice List', or 'Attempt List' before pressing 'Play'");
				alert.getDialogPane().getStylesheets().add(getClass().getResource("dialogs.css").toExternalForm());
				alert.getDialogPane().getStyleClass().add("alertDialog");
				alert.getDialogPane().setPrefSize(550,250);
				alert.showAndWait();
			}

		} else if (e.getSource().equals(rateButton)) {

			//Check if no names from database have been selected and show warning
			if (_selectedCreation == null && _selectedPractice == null) {
				Alert alert = new Alert(AlertType.WARNING, "No recording has currently been selected.\n\nPlease select a recording from 'Name Recordings' or 'Practice List' before pressing 'Rate Quality'");
				alert.getDialogPane().getStylesheets().add(getClass().getResource("dialogs.css").toExternalForm());
				alert.getDialogPane().getStyleClass().add("alertDialog");
				alert.getDialogPane().setPrefSize(450,220);
				alert.showAndWait();
				return;

			} else if (_selectedCreation != null) {
				audioRating.setItemToRate(_selectedCreation);

			} else if (_selectedPractice != null) {
				audioRating.setItemToRate(_selectedPractice);
			}

			try {
				//Open rate name scene
				Stage rateMenuStage = new Stage();
				Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("name_sayer/View/RateName.fxml"));
				Scene rateMenu = new Scene(root,600, 400);
				rateMenuStage.setScene(rateMenu);
				rateMenuStage.initModality(Modality.APPLICATION_MODAL);
				rateMenuStage.show();

			} catch(IOException ex) {

			}

		} else if (e.getSource().equals(openTxtButton)){
			readFromTextFile();
			checkListForButtons();

		} else if (e.getSource().equals(addFullNameButton)) {
			//Make sure entered name is not blank
			addFullName();
			checkListForButtons();
		} else if (e.getSource().equals(editButton)) {
			//Edit selected item
			practiceList.setEditable(true);
			int selectedIndex = practiceList.getItems().lastIndexOf(_selectedPractice);
			practiceList.edit(selectedIndex);

		}
	}

	@FXML
	private void onKeyReleased(KeyEvent keyEvent) {
		//Keyboard shortcut
		if (keyEvent.getCode().equals(KeyCode.ESCAPE)){
			backToMainMenu();
		}
	}

	private void backToMainMenu(){
		//Change to main menu scene
		try {
			Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("name_sayer/View/MainMenu.fxml"));
			Scene home = new Scene(root, 1200, 800);
			Main.ChangeScene(home);
		} catch(IOException ex) {

		}
	}

	//Randomises the names in the to practice list
	private void randomisePracticeList() {
		if (practiceList.getItems().size()>1) {
			ArrayList<String> toShuffle = new ArrayList<>();
			//Add names in the to practice list to an ArrayList
			for (int index = 0; index < practiceList.getItems().size(); index++) {
				toShuffle.add(index, practiceList.getItems().get(index));
			}
			practiceList.getItems().clear();

			ArrayList<String> preShuffle = new ArrayList<>(toShuffle);
			while (preShuffle.equals(toShuffle)) {
				//Randomise the ArrayList
				Collections.shuffle(toShuffle);
			}
			//Add the randomised ArrayList elements back to the to practice list
			for (int index = 0; index < toShuffle.size(); index++) {
				practiceList.getItems().add(index, toShuffle.get(index));
			}
		}
	}


	private void copyItems(){
		//Copy items from practice list to arraylist
		_staticPracticeList.clear();
		for (int pos=0; pos<practiceList.getItems().size(); pos++) {
			_staticPracticeList.add(practiceList.getItems().get(pos));
		}
	}

	private void addToPractice(){
		//Add selected name from creations list to the to practice list, and remove it from the creations list
		if (_selectedCreation!=null) {
			practiceList.getItems().add(_selectedCreation);
			creationList.getItems().remove(_selectedCreation);
			_selectedCreation=null;
		}
		creationList.getSelectionModel().clearSelection();
	}

	private void addMultipleToPractice(ObservableList<String> list){
		boolean add = true;
		//Warning to make sure user wants to add more than 5 names
		if (list.size()>5){
			Alert alert = new Alert(AlertType.CONFIRMATION,"Are you sure you want to add " + list.size() + " names to the practice list?",ButtonType.YES,ButtonType.NO);
			alert.getDialogPane().getStylesheets().add(getClass().getResource("dialogs.css").toExternalForm());
			alert.getDialogPane().getStyleClass().add("alertDialog");
			alert.getDialogPane().setPrefSize(350,200);
			alert.showAndWait();
			if (alert.getResult()==ButtonType.NO){
				add = false;
			}

		}
		if (add) {
			//Add all names from selected list to practice list
			practiceList.getItems().addAll(list);
			creationList.getItems().removeAll(list);

			creationList.getSelectionModel().clearSelection();
		}
	}

	private void removeFromPractice(){
		//Add selected name from the to practice list to the creations list, and remove it from the to practice list
		if (_selectedPractice!=null) {
			//Only add back to database list if it was not a custom name
			if (_staticCreationList.contains(_selectedPractice) && !creationList.getItems().contains(_selectedPractice)) {
				creationList.getItems().add(_selectedPractice);
			}
			practiceList.getItems().remove(_selectedPractice);
			_selectedPractice = null;
		}
		practiceList.getSelectionModel().clearSelection();
	}

	private void removeAllItemsFromPractice() {
		//Go through practice list and add every item back to database list
		for (String item: practiceList.getItems()) {
			if (_staticCreationList.contains(item)) {
				creationList.getItems().add(item);
			}
		}

		//Clear everything in practice list
		practiceList.getItems().clear();
		practiceList.getSelectionModel().clearSelection();
		_selectedPractice = null;
	}

	private void alphabetiseList(ListView<String> listView){
		ArrayList<String> toSort = new ArrayList<>();
		//Add names in the list to an ArrayList
		for (int index = 0; index < listView.getItems().size(); index++) {
			toSort.add(index, listView.getItems().get(index));
		}
		listView.getItems().clear();

		//Sort the ArrayList ignoring case
		Collections.sort(toSort, new Comparator<String>() {
			@Override
			public int compare(String s1, String s2) {
				return s1.compareToIgnoreCase(s2);
			}
		});
		//Add the sorted ArrayList elements back to the list
		for (int index = 0; index < toSort.size(); index++) {
			listView.getItems().add(index, toSort.get(index));
		}
	}

	@FXML
	private void searchEntered(KeyEvent keyEvent) {
		//Update displayed names depending on searched item
		updateList(searchBar.getText());
		alphabetiseList(creationList);
	}

	private void updateList(String search){
		creationList.getItems().clear();
		//Search through arraylist of names and add names that contain search to listview
		for (String name: _staticCreationList){
			if (name.toLowerCase().contains(search.toLowerCase())&&(!name.contains("("))){
				creationList.getItems().add(name);
			}
		}
	}

	private void addNamesFromFile(File file){
		//Read through file and add each line to the practice list
		String line;
		fileNames.clear();
		try {
			FileReader fileReader =
					new FileReader(file);

			BufferedReader bufferedReader =
					new BufferedReader(fileReader);

			//Read next line
			while((line = bufferedReader.readLine()) != null) {
				fileNames.add(line);
				practiceList.getItems().add(line);
			}

			bufferedReader.close();
		}
		catch(FileNotFoundException ex) {
		}
		catch(IOException ex) {
		}
	}


	public static ArrayList<String> getStaticCreationList() {
		return _staticCreationList;
	}

	private void readFromTextFile() {
		//Open file chooser to choose text file
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Text File");
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("txt","*.txt"));
		File textFile = fileChooser.showOpenDialog(openTxtButton.getScene().getWindow());
		if (textFile!=null){
			addNamesFromFile(textFile);
		}
	}

	private void changeToPractice(){

		//Check all the items in the practice list in the namesList class
		namesList.clearLists();
		namesList.checkItems(_staticPracticeList);
		ArrayList<String> goodItems = namesList.getGoodItems();

		boolean cancel = true;

		//Show alert if any names could not be found in database
		String errorItems = namesList.getErrorItems();
		if (!errorItems.equals("\n")){
			Alert alert = new Alert(AlertType.WARNING,"Some name(s) not found in database:" + errorItems + "\n\nNames that do exist will still be practiced",ButtonType.OK, ButtonType.CANCEL);
			alert.getDialogPane().getStylesheets().add(getClass().getResource("dialogs.css").toExternalForm());
			alert.getDialogPane().getStyleClass().add("alertDialog");
			alert.showAndWait();

			if (alert.getResult() == ButtonType.OK) {
				cancel = true;
			} else if (alert.getResult() == ButtonType.CANCEL) {
				cancel = false;
			}
		}

		//Check if there is at least 1 name selected to practice from the database
		if (goodItems.size()>0 && cancel) {
			try {
				//Load practice scene
				Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("name_sayer/View/FullPracticeList.fxml"));
				Scene practiceList = new Scene(root, 1200, 800);
				Main.ChangeScene(practiceList);
			} catch (IOException ex) {

			}
		} else if (goodItems.size()==0 && cancel){
			//Alert if no names could be found from database
			Alert alert = new Alert(AlertType.ERROR,"No items entered in the Practice List \nexist in the database",ButtonType.OK);
			alert.getDialogPane().getStylesheets().add(getClass().getResource("dialogs.css").toExternalForm());
			alert.getDialogPane().getStyleClass().add("alertDialog");
			alert.getDialogPane().setPrefSize(350,200);
			alert.showAndWait();
		}
	}


	private void checkListForButtons() {
		//Enable/disable buttons depending on the size of the practice list
		if (practiceList.getItems().size() == 0) {
			practiceButton.setDisable(true);
			randomiseButton.setDisable(true);
			removeAllButton.setDisable(true);
		} else if (practiceList.getItems().size() == 1) {
			practiceButton.setDisable(false);
			randomiseButton.setDisable(true);
			removeAllButton.setDisable(false);
		} else if (practiceList.getItems().size() >= 2) {
			practiceButton.setDisable(false);
			removeAllButton.setDisable(false);
			randomiseButton.setDisable(false);
		}
	}




	//Error message if no names selected to practice
	private void noItemsWarning() {
		if (practiceList.getItems().size()==0){
			//open alert dialogue
			Alert alert = new Alert(Alert.AlertType.WARNING, "Please select at least one name to practice", ButtonType.OK);
			alert.getDialogPane().getStylesheets().add(getClass().getResource("dialogs.css").toExternalForm());
			alert.getDialogPane().getStyleClass().add("alertDialog");
			alert.getDialogPane().setPrefSize(400,150);
			alert.showAndWait();
			if (alert.getResult() == ButtonType.OK) {
				return;
			}
		}
	}

	//Error message if blank name entered
	private void blankNameWarning() {
		Alert alert = new Alert(AlertType.WARNING, "Cannot add blank name to practice list");
		alert.getDialogPane().getStylesheets().add(getClass().getResource("dialogs.css").toExternalForm());
		alert.getDialogPane().getStyleClass().add("alertDialog");
		alert.getDialogPane().setPrefSize(350,150);
		alert.showAndWait();
	}

	//Error message if illegal characters entered
	private void illegalCharsWarning() {
		Alert alert = new Alert(AlertType.WARNING, "Name contains illegal characters");
		alert.getDialogPane().getStylesheets().add(getClass().getResource("dialogs.css").toExternalForm());
		alert.getDialogPane().getStyleClass().add("alertDialog");
		alert.getDialogPane().setPrefSize(350,150);
		alert.showAndWait();
	}

	private void addFullName(){
		//Check if name is blank space or contains illegal characters
		if (!inputFullNameTextField.getText().equals("") && (inputFullNameTextField.getText() != null) && (!inputFullNameTextField.getText().matches("(\\s+)"))) {
			if (inputFullNameTextField.getText().matches("([a-zA-z' -]*)")) {
				practiceList.getItems().add(inputFullNameTextField.getText().trim().replaceAll(" +", " "));
				//clear textfield
				inputFullNameTextField.clear();
			} else {
				illegalCharsWarning();
			}
		} else {
			blankNameWarning();
			//clear textfield
			inputFullNameTextField.clear();
		}

	}

	@FXML
	private void handleEnterPressed(KeyEvent keyEvent) {
		//Keyboard shortut for enter
		if (keyEvent.getCode().equals(KeyCode.ENTER)){
			addFullName();
			checkListForButtons();
		}
	}

	@FXML
	private void editEnterPressed(KeyEvent keyEvent) {
		if (keyEvent.getCode().equals(KeyCode.ENTER)){
			//Change last selected practice item to be edited name
			if (practiceList.getSelectionModel().getSelectedIndex()==-1){
				_selectedPractice=practiceList.getItems().get(0);
			} else {
				_selectedPractice=practiceList.getSelectionModel().getSelectedItem();
			}

			//Clear selection and disable edit button
			practiceList.getSelectionModel().clearSelection();
			editButton.setDisable(true);
		}
	}
}
