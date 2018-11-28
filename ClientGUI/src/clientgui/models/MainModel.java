
package clientgui.models;

import java.io.*;
import java.net.*;
import clientgui.Writer;
import clientgui.classes.ClientData;
import clientgui.classes.ServerData;
import clientgui.parser.JSONParser;
import clientgui.views.LoginViewController;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import org.json.JSONException;

/**
 *
 * @author Eugenio
 */
public class MainModel {
    private Writer writerThread;
    
    private BooleanProperty userLogged;
    private StringProperty messages;
    private StringProperty messageToSend;
    private BooleanProperty showIp;
    
    private String username = "";
    private ObservableList<ClientData> clientsConnected;
    
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
        showIp = new SimpleBooleanProperty(true);
        clientsConnected = FXCollections.observableArrayList();
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
     * ShowIp Getter
     * @return Boolean
     */
    public boolean getShowIp(){
        return showIp.get();
    }
    
    /**
     * ShowIp Setter
     * @param value Boolean 
     */
    public void setShowIp(boolean value){
        this.showIp.set(value);
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
     * ShowIp Property Getter
     * @return BooleanProperty
     */
    public BooleanProperty showIpProperty(){
        return this.showIp;
    }
    
    /**
     * ClientsConnected Getter
     * @return ObservableList of ClientData
     */
    public ObservableList<ClientData> getClientsConnected(){
        return clientsConnected;
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
        
        try {
            out.println(JSONParser.getLogInJSON(username).toString());
        } catch (JSONException ex) {
            System.err.println(ex);
        }
        
        this.username = username;
        
        this.writerThread = new Writer(in, messages, clientsConnected, showIp);                
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
            
            //TODO cercare un modo per fare apparire la finestra di login sopra a tutto però senza compromettere la alertBox di errore della connessione al server
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
        
        try{
            out.println(JSONParser.getLogOutJSON(username));
        }catch(JSONException jsonEx){
            System.err.println(jsonEx);
        }
        
        try{
            username = "";
            clientsConnected.clear();
            out.flush();
            out.close();
            in.close();
            socket.close();
        }catch(IOException ex){
            System.err.println(ex.getMessage());
        }
        
        setMessages("");
        setUserLogged(false);
        clientsConnected = FXCollections.observableArrayList();
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
        try{
            out.println(JSONParser.getNormalMessageJSON(getMessageToSend(), username));
        }catch(JSONException ex){
            System.err.println(ex);
        }
        
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
