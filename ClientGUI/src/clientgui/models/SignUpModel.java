
package clientgui.models;

import clientgui.classes.ServerData;
import clientgui.views.GlobalChatViewController;
import clientgui.views.LoginViewController;
import java.io.IOException;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;

/**
 *
 * @author Eugenio
 */
public class SignUpModel {
    private ServerData serverData;
    private StringProperty username;
    private StringProperty password;
    private StringProperty confirmPassword;
    
    private MainModel mainModel;
    
    /**
     * Metodo Costruttore
     */
    public SignUpModel(){
        this.serverData = new ServerData("127.0.0.1", 4000);
        this.username = new SimpleStringProperty(""); 
        this.password = new SimpleStringProperty("");
        this.confirmPassword = new SimpleStringProperty("");
    }
    
    /**
     * ServerData Getter
     * @return ServerData
     */
    public ServerData getServerData(){
        return this.serverData;
    }
    
    /**
     * Username Getter
     * @return String Username
     */
    public String getUsername(){
        return this.username.get();
    }
    
    /**
     * Username Setter
     * @param username String Username 
     */
    public void setUsername(String username){
        this.username.set(username);
    }    
    
    /**
     * Username Property Getter
     * @return StringProperty Username
     */
    public StringProperty usernameProperty(){
        return username;
    }
    
    /**
     * Password Getter
     * @return String Password
     */
    public String getPassword(){
        return this.password.get();
    }
    
    /**
     * Password Setter 
     * @param value String value
     */
    public void setPassword(String value){
        this.password.set(value);
    }
    
    /**
     * Password Property Getter
     * @return StringProperty Password
     */
    public StringProperty passwordProperty(){
        return password;
    }
    
    /**
     * ConfirmPassword Getter
     * @return String ConfirmPassword
     */
    public String getConfirmPassword(){
        return this.confirmPassword.get();
    }
    
    /**
     * ConfirmPassword Setter 
     * @param value String value
     */
    public void setConfirmPassword(String value){
        this.confirmPassword.set(value);
    }
    
    /**
     * ConfirmPassword Property Getter
     * @return StringProperty ConfirmPassword
     */
    public StringProperty confirmPasswordProperty(){
        return confirmPassword;
    }
    
    /**
     * MainModel Setter
     * @param mainModel MainModel 
     */
    public void setMainModel(MainModel mainModel){
        this.mainModel = mainModel;
    }
    
    /**
     * Metodo per resettare i campi della registrazione
     */
    private void resetFields(){
        setUsername("");
        setPassword("");
        setConfirmPassword("");
    }
    
    /**
     * Sign Up Handler
     */
    private EventHandler<MouseEvent> signUpHandler = e -> {  
        
        if(!getPassword().equals(getConfirmPassword())){
            Alert alert = new Alert(Alert.AlertType.ERROR, "LE PASSWORD NON CORRISPONDONO", ButtonType.OK);            
            alert.setHeaderText("ERRORE REGISTRAZIONE");                
            alert.showAndWait();
            resetFields();
            return;
        }
        
        if(mainModel.getUserLogged()) mainModel.logOutHandler.handle(e);
        
        // controllo se la registrazione è andata a buon fine.
        if(mainModel.signUp(serverData, getUsername(), getPassword())){ 
            // se si è connesso tolgo la finestra di login e metto la chat
            BorderPane borderPaneMain = (BorderPane) ((Node) e.getSource()).getScene().getRoot();
            Parent ui = null;
            try{
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/clientgui/views/GlobalChatView.fxml"));
                ui = fxmlLoader.load();
                fxmlLoader.<GlobalChatViewController>getController().setModel(mainModel);
            }catch(IOException ex){
                System.err.println(ex.getMessage());
            }
            borderPaneMain.setCenter(ui);
            borderPaneMain.requestFocus();
        }else{
            resetFields();
        }
    };

    /**
     * SignUp Handler Getter
     * @return EventHandler
     */
    public EventHandler<MouseEvent> getSignUpHandler(){
        return this.signUpHandler;
    }
}
