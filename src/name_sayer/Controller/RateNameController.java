package name_sayer.Controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import com.sun.javafx.tk.Toolkit;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import name_sayer.Model.AudioRating;
import name_sayer.Model.NamesList;

public class RateNameController implements Initializable {
	@FXML
	private ListView<String> namesToRateList;
	@FXML
	private Button goodButton;
	@FXML
	private Button badButton;
	@FXML
	private Button doneButton;
	@FXML
	private Label ratingLabel;
	private AudioRating audioRating = AudioRating.getInstance();
	private NamesList namesList = NamesList.getInstance();
	private String name;


	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		//add single names to list view
		name = audioRating.getItemToRate();
		namesToRateList.getItems().addAll(namesList.checkItemForRating(name));

		//let user select multiple items in the listview without holding 'ctrl'
		namesToRateList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		namesToRateList.addEventFilter( MouseEvent.MOUSE_PRESSED, eventHandler );
		namesToRateList.addEventFilter( MouseEvent.MOUSE_RELEASED, eventHandler );

		goodButton.setDisable(true);
		badButton.setDisable(true);

		//if there is only one name in the list view, automatically select it and enable buttons
		if (namesToRateList.getItems().size()==1) {
			namesToRateList.getSelectionModel().select(0);;
			goodButton.setDisable(false);
			badButton.setDisable(false);
		}
	}

	@FXML
	private void onClick(ActionEvent e) {
		if (e.getSource().equals(goodButton)){
			boolean error = false;
			StringBuilder errorItems = new StringBuilder("\n");
			//rate all selected items 'good'
			for (String item: namesToRateList.getSelectionModel().getSelectedItems()) {
				if (!audioRating.RateQualityGood(item)) {
					error = true;
					errorItems.append("\n").append(item);
				} 
			}

			if (error) {
				DisplayPlayWarning(errorItems.append("\n\n").toString());
				ratingLabel.setText("Unable to complete rating of all selected name(s)");
			} else {
				ratingLabel.setText("The selected name(s) have been rated as 'good'.");
			}
		} else if (e.getSource().equals(badButton)) {
			boolean error = false;
			StringBuilder errorItems = new StringBuilder("\n");
			//rate all selected items 'bad's
			for (String item: namesToRateList.getSelectionModel().getSelectedItems()) {
				if (!audioRating.RateQualityBad(item)) {
					error = true;
					errorItems.append("\n").append(item);
				} 
			}
			
			if (error) {
				DisplayPlayWarning(errorItems.append("\n").toString());
				ratingLabel.setText("Unable to complete rating of all selected name(s)");
			} else {
				ratingLabel.setText("The selected name(s) have been rated as 'bad'.");
			}
		} else if (e.getSource().equals(doneButton)) {
			closeStage();
		}
	}

	@FXML
	private void handleMouseClick(MouseEvent arg0) {
		goodButton.setDisable(false);
		badButton.setDisable(false);
	}

	//close the rate menu
	private void closeStage(){
		Stage s = (Stage) doneButton.getScene().getWindow();
		s.close();
	}

	//keyboard shortcut for 'escape' key: closes the rate menu
	@FXML
	private void onKeyReleased(KeyEvent keyEvent) {
		if (keyEvent.getCode().equals(KeyCode.ESCAPE)){
			closeStage();
		}
	}


	/*
	 * the following code allows user to select multiple items without holding down 'ctrl'
	 * code source: https://stackoverflow.com/questions/32136669/select-multiple-listview-item-without-pressing-ctrl-shortcut-key
	 */
	EventHandler<MouseEvent> eventHandler = ( event ) ->
	{
		if ( !event.isShortcutDown() )
		{
			Event.fireEvent( event.getTarget(), cloneMouseEvent( event ) );
			event.consume();
		}
	};

	//display a warning if user wants to rate a name without listening to the to a recording first
	private void DisplayPlayWarning(String errorItems) {
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Some recordings have not been played before:" + errorItems + "Please listen to them recording before rating", ButtonType.OK);
		alert.getDialogPane().getStylesheets().add(getClass().getResource("dialogs.css").toExternalForm());
		alert.getDialogPane().getStyleClass().add("alertDialog");
		alert.getDialogPane().setPrefSize(450,300);
		alert.showAndWait();
	}

	private MouseEvent cloneMouseEvent( MouseEvent event )
	{
		switch (Toolkit.getToolkit().getPlatformShortcutKey())
		{
		case SHIFT:
			return new MouseEvent(
					event.getSource(),
					event.getTarget(),
					event.getEventType(),
					event.getX(),
					event.getY(),
					event.getScreenX(),
					event.getScreenY(),
					event.getButton(),
					event.getClickCount(),
					true,
					event.isControlDown(),
					event.isAltDown(),
					event.isMetaDown(),
					event.isPrimaryButtonDown(),
					event.isMiddleButtonDown(),
					event.isSecondaryButtonDown(),
					event.isSynthesized(),
					event.isPopupTrigger(),
					event.isStillSincePress(),
					event.getPickResult()
					);

		case CONTROL:
			return new MouseEvent(
					event.getSource(),
					event.getTarget(),
					event.getEventType(),
					event.getX(),
					event.getY(),
					event.getScreenX(),
					event.getScreenY(),
					event.getButton(),
					event.getClickCount(),
					event.isShiftDown(),
					true,
					event.isAltDown(),
					event.isMetaDown(),
					event.isPrimaryButtonDown(),
					event.isMiddleButtonDown(),
					event.isSecondaryButtonDown(),
					event.isSynthesized(),
					event.isPopupTrigger(),
					event.isStillSincePress(),
					event.getPickResult()
					);

		case ALT:
			return new MouseEvent(
					event.getSource(),
					event.getTarget(),
					event.getEventType(),
					event.getX(),
					event.getY(),
					event.getScreenX(),
					event.getScreenY(),
					event.getButton(),
					event.getClickCount(),
					event.isShiftDown(),
					event.isControlDown(),
					true,
					event.isMetaDown(),
					event.isPrimaryButtonDown(),
					event.isMiddleButtonDown(),
					event.isSecondaryButtonDown(),
					event.isSynthesized(),
					event.isPopupTrigger(),
					event.isStillSincePress(),
					event.getPickResult()
					);

		case META:
			return new MouseEvent(
					event.getSource(),
					event.getTarget(),
					event.getEventType(),
					event.getX(),
					event.getY(),
					event.getScreenX(),
					event.getScreenY(),
					event.getButton(),
					event.getClickCount(),
					event.isShiftDown(),
					event.isControlDown(),
					event.isAltDown(),
					true,
					event.isPrimaryButtonDown(),
					event.isMiddleButtonDown(),
					event.isSecondaryButtonDown(),
					event.isSynthesized(),
					event.isPopupTrigger(),
					event.isStillSincePress(),
					event.getPickResult()
					);

		default:
			return event;

		}
	}

}
