package name_sayer.HelpMenu;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import name_sayer.Main;

import java.io.IOException;

public abstract class AbstractHelpController {
	
	protected Help help = Help.getInstance();
    @FXML
    protected Button homeButton;
    @FXML
    protected ImageView imageView;
    @FXML
    protected Button help1Button;
    @FXML
    protected Button help2Button;
    @FXML
    protected Button help3Button;
    @FXML
    protected Button help4Button;
    @FXML
    protected Button help5Button;
    @FXML
    protected Button help6Button;
    @FXML
    protected Button help7Button;

	
	
  protected void hideHomeOnFirstLaunch() {
		if (checkFirstLaunch()) {
			homeButton.setVisible(false);
		}
  }

  protected boolean checkFirstLaunch(){
      return help.checkFirstLaunch();
  }
  
  protected void createFileOnLaunch() {
	  help.createFileOnLaunch();
  }

  protected void changeToMainMenu(){
      try {
          Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("name_sayer/View/MainMenu.fxml"));
          Scene home = new Scene(root,1200, 800);
          Main.ChangeScene(home);
      } catch(IOException ex) {

      }
  }
	
  protected void changeHelpScene(String fxml){
      try {
          Parent root = FXMLLoader.load(getClass().getResource(fxml));
          Scene help = new Scene(root, 1200, 800);
          Main.ChangeScene(help);
      } catch(IOException ex1) {

      }
  }
  
  @FXML
  protected void onClick(ActionEvent e) {
      if (e.getSource().equals(homeButton)){
          changeToMainMenu();
          if (this.getClass().getSimpleName().equals("Help4_1Controller")) {
        	  createFileOnLaunch();
          }
      } else if (e.getSource().equals(help1Button)) {
    	  changeHelpScene("Help1.fxml");
      } else if (e.getSource().equals(help2Button)) {
    	  changeHelpScene("Help2.fxml");
      } else if (e.getSource().equals(help3Button)) {
    	  changeHelpScene("Help3.fxml");
      } else if (e.getSource().equals(help4Button)) {
    	  changeHelpScene("Help3_1.fxml");
      } else if (e.getSource().equals(help5Button)) {
    	  changeHelpScene("Help3_2.fxml");
      } else if (e.getSource().equals(help6Button)) {
    	  changeHelpScene("Help4.fxml");
      } else if (e.getSource().equals(help7Button)) {
    	  changeHelpScene("Help4_1.fxml");
      } 

  }
	
}
