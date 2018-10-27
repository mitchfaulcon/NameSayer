package name_sayer.HelpMenu;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Help4Controller extends AbstractHelpController implements Initializable {

    @FXML
    private Button previousButton;
    @FXML
    private Button nextButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        try {
            BufferedImage bufferedImage = ImageIO.read(new File("Resources/PracticeList1.png"));
            Image image = SwingFXUtils.toFXImage(bufferedImage, null);
            this.imageView.setImage(image);
        }
        catch (IOException e){

        }

        hideHomeOnFirstLaunch();
    }


    @Override
    @FXML
	protected void onClick(ActionEvent e) {
        super.onClick(e);
    	if (e.getSource().equals(nextButton)){
            changeHelpScene("Help4_1.fxml");
        } else if (e.getSource().equals(previousButton)){
            changeHelpScene("Help3_2.fxml");
        }

    }

    @FXML
    private void onKeyReleased(KeyEvent keyEvent) {
        if (keyEvent.getCode().equals(KeyCode.ESCAPE) && !checkFirstLaunch()){
            changeToMainMenu();
        } else if (keyEvent.getCode().equals(KeyCode.RIGHT)){
            changeHelpScene("Help4_1.fxml");
        } else if (keyEvent.getCode().equals(KeyCode.LEFT)){
            changeHelpScene("Help3_2.fxml");
        }
    }
}
