
package clientgui.views;

import clientgui.models.MainModel;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

/**
 * FXML Controller class
 *
 * @author Eugenio
 */
public class MainViewController implements Initializable {

    @FXML
    private MainModel model;
    
    public MainViewController(){        
        this.model = new MainModel();
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }    
    
}
