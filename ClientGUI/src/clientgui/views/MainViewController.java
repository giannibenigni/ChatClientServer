
package clientgui.views;

import clientgui.models.MainModel;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author Eugenio
 */
public class MainViewController implements Initializable {

    @FXML
    private MainModel model;
    
    @FXML
    private TextField txtMessageToSend;
        
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) { 
        model.messageToSendProperty().bindBidirectional(txtMessageToSend.textProperty());         
    }
    
}
