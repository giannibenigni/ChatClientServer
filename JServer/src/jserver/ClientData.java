
package jserver;

/**
 *
 * @author Eugenio
 */
public class ClientData {
    private String username;
    private String ip;
    
    public ClientData(){}   

    public ClientData(String username, String ip) {
        this.username = username;
        this.ip = ip;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
    
}
