
package clientgui.models;

import java.io.*;
import java.net.*;
import clientgui.Writer;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author Eugenio
 */
public class MainModel {
    private Writer writerThread;
    
    private BooleanProperty userLogged = new SimpleBooleanProperty(false);
    private StringProperty messages = new SimpleStringProperty("");
    
    private BufferedReader in = null;
    private PrintStream out = null;
    
    private Socket socket = null;
    
    /**
     * Metodo Costruttore
     */
    public MainModel(){
        try{
            socket = new Socket("localhost", 4000);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintStream(socket.getOutputStream(), true);
        }catch(Exception ex){
            Alert alert = new Alert(Alert.AlertType.ERROR, ex.getMessage(), ButtonType.OK);
            alert.setHeaderText("ERRORE CONNESSIONE AL SERVER");
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
            
            System.exit(1);
        } 
        
        this.writerThread = new Writer(in, messages);
        writerThread.start();
    }
    
    /**
     * Messages Getter
     * @return 
     */
    public String getMessages(){
        return this.messages.get();
    }
    
    /**
     * UserLogged Getter
     * @return 
     */
    public boolean getUserLogged(){
        return this.userLogged.get();
    }

    // EVENTS -------------------------------------------------
    
    public EventHandler<ActionEvent> logInHandler = e -> {
        System.out.println("LOG IN");

        try{
            Parent root = FXMLLoader.load(getClass().getResource("/clientgui/views/LoginView.fxml"));        
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            
            stage.setAlwaysOnTop(true);
            stage.initModality(Modality.APPLICATION_MODAL);
            
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        }catch(Exception ex){
            System.err.println(ex.getMessage());
        }
    };  
    
    public EventHandler<ActionEvent> getLogInHandler(){
        return this.logInHandler;
    }
    
    public EventHandler<ActionEvent> logOutHandler = e -> {
        System.out.println("LOG OUT");
    };  
    
    public EventHandler<ActionEvent> getLogOutHandler(){
        return this.logOutHandler;
    }

    public EventHandler<ActionEvent> sendMessageHandler = e -> {
        System.out.println("SEND");
    };
    
    public EventHandler<ActionEvent> getSendMessageHandler(){
        return this.sendMessageHandler;
    }
}
