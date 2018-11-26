
package clientgui.models;

import clientgui.classes.ServerData;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.stage.Stage;

/**
 *
 * @author Eugenio
 */
public class LoginModel {
    private ServerData serverData;
    private StringProperty username;
    
    private MainModel mainModel;
    
    /**
     * Metodo Costruttore
     */
    public LoginModel(){
        this.serverData = new ServerData("127.0.0.1", 4000);
        this.username = new SimpleStringProperty(""); 
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
     * MainModel Setter
     * @param mainModel MainModel 
     */
    public void setMainModel(MainModel mainModel){
        this.mainModel = mainModel;
    }
    
    private EventHandler<ActionEvent> logInHandler = e -> {
        mainModel.connectToSocket(serverData, username.get());        
        ((Stage)((Node)e.getTarget()).getScene().getWindow()).close();
    };
    
    public EventHandler<ActionEvent> getLogInHandler(){
        return this.logInHandler;
    }
}
