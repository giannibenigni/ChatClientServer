
package clientgui.classes;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Eugenio
 */
public class PrivateChat {
    private ClientData contact;
    private StringProperty messages;
    
    /**
     * Metodo Costruttore
     * @param contact ClientData del destinatario
     */
    public PrivateChat(ClientData contact){
        this.contact = contact;
        this.messages = new SimpleStringProperty("");
    }

    /**
     * Contact Getter
     * @return ClientData
     */
    public ClientData getContact() {
        return contact;
    }

    /**
     * Contact Setter
     * @param value ClientData value  
     */
    public void setContact(ClientData value) {
        this.contact = contact;
    }
    
    /**
     * Messages Getter
     * @return String messages
     */
    public String getMessages(){
        return this.messages.get();
    }
    
    /**
     * Messages Setter
     * @param value String value
     */
    public void setMessages(String value){
        this.messages.set(value);
    }
    
    /**
     * MessagesProperty Getter
     * @return StringProperty
     */
    public StringProperty messagesProperty(){
        return this.messages;
    }    

    /**
     * Metodo per aggiungere un messaggio
     * @param newMessage String new Message
     */
    public void addMessages(String newMessage){
        setMessages(getMessages()+"\n"+newMessage);
    }
    
    /**
     * Metodo toString
     * @return String
     */
    @Override
    public String toString() {
        return "Username: "+contact.getUsername()+"\nIp: "+contact.getIp();
    }
}
