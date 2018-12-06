
package servergui.views;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import servergui.models.MainModel;

/**
 *
 * @author Gianni
 */
public class MainViewController implements Initializable {
    
   @FXML
    private TextField txtMessageToSend;
   
   @FXML
   private TextArea txtMessages;
    
    @FXML
    private MainModel model;
            
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        model.messageToSendProperty().bindBidirectional(txtMessageToSend.textProperty());   
        
        txtMessages.textProperty().addListener((String) -> {            
            Platform.runLater(() -> txtMessages.positionCaret(txtMessages.getLength()));            
        });
    }    
    
}
