
package clientgui.classes;

import java.util.Objects;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Eugenio
 */
public class ClientData {
    private StringProperty username;
    private StringProperty ip;
    
    /**
     * Metodo Costruttore
     */
    public ClientData(){}
    
    /**
     * Metodo Costruttore
     * @param username String username
     * @param ip String ip
     */
    public ClientData(String username, String ip){
        this.username = new SimpleStringProperty(username);
        this.ip = new SimpleStringProperty(ip);
    }
    
    /**
     * Username Getter
     * @return String
     */
    public String getUsername(){
        return username.get();
    }
    
    /**
     * Ip Getter
     * @return String
     */
    public String getIp(){
        return ip.get();
    }
    
    /**
     * Username Setter
     * @param value String
     */
    public void setUsername(String value){
        username.set(value);
    }
    
    /**
     * Ip Setter
     * @param value String
     */
    public void setIp(String value){
        ip.set(value);
    }
    
    /**
     * Username Property Getter
     * @return String Property
     */
    public StringProperty usernameProperty(){
        return username;
    }
    
    /**
     * Ip Property Getter
     * @return StringProperty
     */
    public StringProperty ipProperty(){
        return ip;
    }

    /**
     * ClientData Equals
     * @param obj ClientData
     * @return Boolean
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ClientData other = (ClientData) obj;
        
        if(!this.username.get().equals(other.username.get())){
            return false;
        }
        
        if(!this.ip.get().equals(other.ip.get())){
            return false;
        }

        return true;
    }
    

    /**
     * Metodo toString
     * @return String
     */
    @Override
    public String toString() {
        return "Username: "+username.get()+"\nIp: "+ip.get();
    }
    
}
