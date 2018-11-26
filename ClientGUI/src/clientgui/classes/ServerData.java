
package clientgui.classes;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Eugenio
 */
public class ServerData {
    private StringProperty ip;
    private IntegerProperty port;

    /**
     * Metodo Costruttore
     * @param ip Ip del Server
     * @param port Porta del Server
     */
    public ServerData(String ip, int port){
        this.ip = new SimpleStringProperty(ip);
        this.port = new SimpleIntegerProperty(port);
    }
    
    /**
     * Ip Getter
     * @return String Ip
     */
    public String getIp() {
        return ip.get();
    }        
        
    /**
     * Port Getter
     * @return Int port
     */
    public int getPort() {
        return port.get();
    }
    
    /**
     * Ip Setter
     * @param ip String Ip 
     */
    public void setIp(String ip){
        this.ip.set(ip);
    }
    
    /**
     * Port Setter
     * @param port Int Port 
     */
    public void setPort(int port){
        this.port.set(port);
    }
    
    /**
     * Ip Property Getter
     * @return StringProperty Ip
     */
    public StringProperty ipProperty(){
        return ip;
    }
    
    /**
     * Port Property Getter
     * @return IntegerProperty Port
     */
    public IntegerProperty portProperty(){
        return port;
    }
}
