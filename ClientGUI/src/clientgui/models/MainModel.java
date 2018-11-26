
package clientgui.models;

import java.io.*;
import java.net.*;
import clientgui.Writer;
import clientgui.classes.ServerData;
import clientgui.views.LoginViewController;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

/**
 *
 * @author Eugenio
 */
public class MainModel {
    private Writer writerThread;
    
    private BooleanProperty userLogged;
    private StringProperty messages;
    private StringProperty messageToSend;
    
    private BufferedReader in = null;
    private PrintStream out = null;
    
    private Socket socket = null;
    
    /**
     * Metodo Costruttore
     */
    public MainModel(){
        userLogged = new SimpleBooleanProperty(false);
        messages = new SimpleStringProperty("");     
        messageToSend = new SimpleStringProperty("");
    }
    
    /**
     * Messages Getter
     * @return String
     */
    public String getMessages(){
        return this.messages.get();
    }
    
    /**
     * Messages Setter
     * @param value String
     */
    public void setMessages(String value){
        messages.set(value);
    }
    
    /**
     * UserLogged Getter
     * @return Boolean
     */
    public boolean getUserLogged(){
        return this.userLogged.get();
    }
    
    /**
     * UserLogged Setter
     * @param value Boolean 
     */
    public void setUserLogged(boolean value){
        this.userLogged.set(value);
    }
    
    /**
     * MessageToSend Getter
     * @return String 
     */
    public String getMessageToSend(){
        return this.messageToSend.get();
    }
    
    /**
     * MessageToSend Setter
     * @param value String 
     */
    public void setMessageToSend(String value){
        this.messageToSend.set(value);
    }
    
    /**
     * UserLogged Propety Getter
     * @return BooleanProperty
     */
    public BooleanProperty userLoggedProperty(){
        return userLogged;
    }
    
    /**
     * Messages Property Getter
     * @return StringProperty
     */
    public StringProperty messagesProperty(){
        return messages;
    }
    
    /**
     * MessageToSend Property Getter
     * @return StringProperty
     */
    public StringProperty messageToSendProperty(){
        return messageToSend;
    }

    /**
     * Metodo che effettua la connessione al socket, setta lo username del client
     * e fa partire il thread per la scrittura dei messaggi del server
     * @param serverData ServerData serverData (ip, port)
     * @param username String username
     */
    public void connectToSocket(ServerData serverData, String username){
        try{        
            socket = new Socket(InetAddress.getByName(serverData.getIp()),  serverData.getPort()); 
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintStream(socket.getOutputStream(), true);
        }catch(IOException ex){
            Alert alert = new Alert(Alert.AlertType.ERROR, ex.getMessage(), ButtonType.OK);            
            alert.setHeaderText("ERRORE CONNESSIONE AL SERVER");
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
            
            System.exit(1);
        } 
        
        this.writerThread = new Writer(in, messages);
        
        out.println(username);               
        
        writerThread.start();
        setUserLogged(true);
    }
    
    // EVENTS -------------------------------------------------
    
    /**
     * Apro la finestra di Login
     */
    public EventHandler<ActionEvent> logInHandler = e -> {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/clientgui/views/LoginView.fxml"));                    
            Stage stage = new Stage();            
            
            //TODO cercare un modo per fare apparire la finestra di login sopra a tutto però senza compromettere la alerrtBox della connessione al server
//            stage.setAlwaysOnTop(true);
//            stage.initModality(Modality.APPLICATION_MODAL);            
            
            stage.setScene(new Scene(loader.load()));
            stage.setResizable(false);
            
            LoginViewController controller = loader.<LoginViewController>getController();
            
            controller.setMainModel(this);
            
            stage.showAndWait();
                
        }catch(IOException ex){
            System.err.println(ex.getMessage());
        }
    };  
    
    /**
     * LogIn Handler Getter
     * @return EventHandler
     */
    public EventHandler<ActionEvent> getLogInHandler(){
        return this.logInHandler;
    }
    
    /**
     * LOG OUT Handler
     * Chiudo la connessione con il socket e stoppo il thread che scrive i messaggi
     */
    public EventHandler<ActionEvent> logOutHandler = e -> {
        if(!getUserLogged()) return;
        
        writerThread.ferma();        
        //invio un messaggio al server per dirgli che mi sto disconnettendo
        out.println("/exit");
        
        try{
            out.flush();
            out.close();
            in.close();
            socket.close();
        }catch(IOException ex){
            System.err.println(ex.getMessage());
        }
        
        setMessages("");
        setUserLogged(false);
    };  
    
    /**
     * LogOut Handler Getter
     * @return EventHandler
     */
    public EventHandler<ActionEvent> getLogOutHandler(){
        return this.logOutHandler;
    }

    /**
     * SEND MESSAGE Handler
     * Manda un messaggio al server
     */
    public EventHandler<ActionEvent> sendMessageHandler = e -> {
        out.println(getMessageToSend());
        setMessageToSend("");
    };
    
    /**
     * SendMessage Handler Getter
     * @return EventHandler
     */
    public EventHandler<ActionEvent> getSendMessageHandler(){
        return this.sendMessageHandler;
    }
}
