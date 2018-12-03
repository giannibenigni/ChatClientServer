
package clientgui.models;

import clientgui.classes.ServerData;
import clientgui.views.GlobalChatViewController;
import java.io.IOException;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;

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
    
    /**
     * LOG IN Handler
     * faccio la connessione al socket e chiudo la finestra di login
     */
    private EventHandler<MouseEvent> logInHandler = e -> {        
        // controllo se la connessione è andata a buon fine.
        if(mainModel.connectToSocket(serverData, username.get())){ 
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
        }
    };

    /**
     * LogIn Handler Getter
     * @return EventHandler
     */
    public EventHandler<MouseEvent> getLogInHandler(){
        return this.logInHandler;
    }
}
