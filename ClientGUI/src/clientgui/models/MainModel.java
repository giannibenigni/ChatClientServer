
package clientgui.models;

import java.io.*;
import java.net.*;
import clientgui.Writer;
import clientgui.classes.ClientData;
import clientgui.classes.ServerData;
import clientgui.parser.JSONParser;
import java.util.concurrent.Semaphore;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.input.MouseEvent;
import org.json.JSONException;

/**
 *
 * @author Eugenio
 */
public class MainModel {
    private Writer writerThread;
    
    private final BooleanProperty userLogged;
    private final  StringProperty messages;
    private final StringProperty messageToSend;
    private final BooleanProperty showIp;
    
    private String username = "";
    private final ObjectProperty<ObservableList<ClientData>> clientsConnected = new SimpleObjectProperty<>(FXCollections.observableArrayList());
    
    private BufferedReader in = null;
    private PrintStream out = null;
    
    private Socket socket = null;
    
    private Semaphore disconnectSem;
    
    /**
     * Metodo Costruttore
     */
    public MainModel(){
        disconnectSem = new Semaphore(0);
        userLogged = new SimpleBooleanProperty(false);
        messages = new SimpleStringProperty("");     
        messageToSend = new SimpleStringProperty("");
        showIp = new SimpleBooleanProperty(true);        
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
        return clientsConnected.get();
    }
    
    /**
     * ClientsConnected Setter
     * @param value ObservableList of ClientData
     */
    public void setClientsConnected(ObservableList<ClientData> value){
        this.clientsConnected.set(value);
    }
    
    /**
     * ClientsConnected Property Getter
     * @return ObjectProperty
     */
    public ObjectProperty<ObservableList<ClientData>> clientsConnectedProperty(){
        return clientsConnected;
    }

    /**
     * Metodo che effettua la connessione al socket, setta lo username del client
     * e fa partire il thread per la scrittura dei messaggi del server
     * @param serverData ServerData serverData (ip, port)
     * @param username String username
     * @return true se si è riuscito a connettere. altrimenti false
     */
    public boolean connectToSocket(ServerData serverData, String username){
        try{        
            socket = new Socket(InetAddress.getByName(serverData.getIp()),  serverData.getPort()); 
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintStream(socket.getOutputStream(), true);
        }catch(IOException ex){
            Alert alert = new Alert(Alert.AlertType.ERROR, ex.getMessage(), ButtonType.OK);            
            alert.setHeaderText("ERRORE CONNESSIONE AL SERVER");
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
            return false;
            
//            System.exit(1);
        }
        
        try {
            out.println(JSONParser.getLogInJSON(username).toString());
        } catch (JSONException ex) {
            System.err.println(ex);
        }
        
        this.username = username;
        
        this.writerThread = new Writer(in, messages, clientsConnected, showIp, disconnectSem);         
        writerThread.start();
        setUserLogged(true);
        
        return true;
    }
    
    // EVENTS -------------------------------------------------    
    
    /**
     * LOG OUT Handler
     * Chiudo la connessione con il socket e stoppo il thread che scrive i messaggi
     */
    public EventHandler<MouseEvent> logOutHandler = e -> {
        if(!getUserLogged()) return;
                
        try{
            out.println(JSONParser.getLogOutJSON(username));
        }catch(JSONException jsonEx){
            System.err.println(jsonEx);
        }
        
        writerThread.ferma();
        
        try{
            disconnectSem.acquire();
            username = "";
            clientsConnected.get().clear();
            out.flush();
            out.close();
            in.close();
            socket.close();            
        }catch(IOException | InterruptedException ex){            
            System.err.println(ex);
        }
        
        setMessages("");
        setUserLogged(false);
        clientsConnected.get().clear();
    };  
    
    /**
     * LogOut Handler Getter
     * @return EventHandler
     */
    public EventHandler<MouseEvent> getLogOutHandler(){
        return this.logOutHandler;
    }

    /**
     * SEND MESSAGE Handler
     * Manda un messaggio al server
     */
    public EventHandler<MouseEvent> sendMessageHandler = e -> {
        if(!getUserLogged()) return;
        
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
    public EventHandler<MouseEvent> getSendMessageHandler(){
        return this.sendMessageHandler;
    }
}
