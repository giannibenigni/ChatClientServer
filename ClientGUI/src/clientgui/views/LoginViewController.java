
package clientgui.views;

import clientgui.models.LoginModel;
import clientgui.models.MainModel;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.util.converter.NumberStringConverter;

/**
 * FXML Controller class
 *
 * @author Eugenio
 */
public class LoginViewController implements Initializable {
    
    @FXML    
    private LoginModel model;
    
    @FXML
    private TextField txtIp;
    
    @FXML
    private TextField txtPort;
            
    @FXML
    private TextField txtUsername;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) { 
        model.getServerData().ipProperty().bindBidirectional(txtIp.textProperty());
        txtPort.textProperty().bindBidirectional(model.getServerData().portProperty(), new NumberStringConverter());
        model.usernameProperty().bindBidirectional(txtUsername.textProperty()); 
    }    
    
    /**
     * Metodo per Settare il MainModel
     * @param mainModel MainModel
     */
    public void setMainModel(MainModel mainModel){
        this.model.setMainModel(mainModel);
    }
}
