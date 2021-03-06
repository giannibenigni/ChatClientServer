
package servergui.models;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Semaphore;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import org.json.JSONException;
import servergui.classes.Connection;
import servergui.classes.JSONParser;



/**
 *
 * @author gianni.benigni
 */
public class MainModel extends Thread{    
    private ServerSocket Server;
    private StringProperty messageToSend;
    private StringProperty messages;
    
    public ObservableList<Connection> openConnection;
    
    private Semaphore listSem;
    private Semaphore messagesSem;
   
    public MainModel() throws Exception{        
        listSem = new Semaphore(1);
        messagesSem = new Semaphore(1);

        openConnection = FXCollections.observableArrayList();
        Server = new ServerSocket(4000);
        
        messageToSend = new SimpleStringProperty("");
        messages = new SimpleStringProperty("");
        
        this.start();
    }
    
    @Override
    public void run(){        
        while(true){            
            try {
                System.out.println("*****  In attesa di connessione.\n");
                
                Socket Client = Server.accept();
                
                System.out.println("*****  Connessione accettata da: " + Client.getInetAddress()+"\n");
                
                listSem.acquire();
                
                Platform.runLater(()->{
                    openConnection.add(new Connection(Client, openConnection, messages, listSem, messagesSem));} 
                );
                
                listSem.release();                
            } catch (IOException | InterruptedException ex) {
                System.out.println(ex);
            }                
        }        
    }
    
    
    /**
     * Getter messages per textArea
     * @return String
     */
    public String getMessages(){
        return this.messages.get();
    }
    
    /**
     * Setter del testo per textArea
     * @param value String
     */
    public void setMessages(String value){
        messages.set(value);
    }
    
    /**
     * Messages Property Getter
     * @return StringProperty
     */
    public StringProperty messagesProperty(){
        return messages;
    }
    
    /**
     * Getter del texsto da inviare
     * @return String
     */
    public String getMesssageToSend(){
        return this.messageToSend.get();
    }
    
    /**
     * Setter del testo da inviare
     * @param value 
     */
    public void setMessageTosend(String value){
        this.messageToSend.set(value);
    }
    
    /**
     * MessageToSend Property Getter
     * @return StringProperty
     */
    public StringProperty messageToSendProperty(){
        
        return messageToSend;
    }
    
    /**
     * Getter dei client Connessi
     * @return  ObserveList of Connection
     */
    public ObservableList<Connection> getOpenConnection(){
        return openConnection;
    }        
    
    //EVENTS ----------------------------------------------------------
    
    /**
     * SEND MESSAGE Handler
     */
    public EventHandler<ActionEvent> sendMessageHandler = e -> {                
        try{
            listSem.acquire();           
            
            if(!getMesssageToSend().replace(" ", "").isEmpty()){                           
                for(Connection connection: openConnection){
                    connection.writeMessage(JSONParser.getServerNormalMessageJSON(getMesssageToSend()).toString());
                }   
                messagesSem.acquire();
                messages.set(messages.get() + "\n<Server> " + getMesssageToSend());
                messagesSem.release();
            }
            listSem.release();
        }catch(InterruptedException | JSONException ex){
            System.err.println(ex.getMessage());
        }
        setMessageTosend("");        
    };
    
    /**
     * SendMessage Handler Getter
     * @return EventHandler
     */
    public EventHandler<ActionEvent> getSendMessageHandler(){
        return this.sendMessageHandler;
    }    
}
