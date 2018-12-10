
package clientgui.views;

import clientgui.classes.util.StringNumberConverter;
import clientgui.models.MainModel;
import clientgui.models.SingUpModel;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.IntegerValidator;
import com.jfoenix.validation.RequiredFieldValidator;
import com.jfoenix.validation.base.ValidatorBase;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

/**
 * FXML Controller class
 *
 * @author Eugenio
 */
public class SingUpViewController implements Initializable {

    @FXML
    private SingUpModel model;
    
    @FXML
    private JFXTextField txtIp;
    
    @FXML
    private JFXTextField txtPort;
    
    @FXML
    private JFXTextField txtUsername;
    
    @FXML
    private JFXPasswordField txtPassword;
    
    @FXML
    private JFXPasswordField txtConfirmPassword;
    
    @FXML
    private JFXButton btnSingUp;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) { }        
    
    private void setValidator(){
        RequiredFieldValidator fieldRequiredValidator = new RequiredFieldValidator();
        fieldRequiredValidator.setMessage("Field Required");
        
        IntegerValidator integerValidator = new IntegerValidator();
        integerValidator.setMessage("Fiels must be an Integer");                
        
        txtIp.getValidators().add(fieldRequiredValidator);
        txtPort.getValidators().addAll(fieldRequiredValidator, integerValidator);
        txtUsername.getValidators().add(fieldRequiredValidator);
        txtPassword.getValidators().add(fieldRequiredValidator);
        txtConfirmPassword.getValidators().add(fieldRequiredValidator);
        
        txtIp.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            if(!newValue){
                btnSingUp.setDisable(!txtIp.validate());
            }
        }); 
        
        txtPort.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            if(!newValue){
                btnSingUp.setDisable(!txtPort.validate());
            }
        }); 
        
        txtUsername.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            if(!newValue){
                btnSingUp.setDisable(!txtUsername.validate()); 
            }
        }); 
        
        txtPassword.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            if(!newValue){
                btnSingUp.setDisable(!txtPassword.validate()); 
            }
        }); 
        
        txtConfirmPassword.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            if(!newValue){
                btnSingUp.setDisable(!txtConfirmPassword.validate());                 
            }
        }); 
        
        btnSingUp.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {            
            // abilito o disabilito il bottone di login in base alle validation            
            btnSingUp.setDisable(!txtIp.validate() || !txtPort.validate() || !txtUsername.validate() || !txtPassword.validate() || !txtConfirmPassword.validate());            
        });
    }

    /**
     * Metodo per Settare il MainModel
     * @param mainModel MainModel
     */
    public void setMainModel(MainModel mainModel){
        this.model.setMainModel(mainModel);
        
        txtIp.textProperty().bindBidirectional(model.getServerData().ipProperty());        
        txtPort.textProperty().bindBidirectional(model.getServerData().portProperty(), new StringNumberConverter());
        txtUsername.textProperty().bindBidirectional(model.usernameProperty());  
        txtPassword.textProperty().bindBidirectional(model.passwordProperty()); 
        txtConfirmPassword.textProperty().bindBidirectional(model.confirmPasswordProperty());
        setValidator();
    }
}
