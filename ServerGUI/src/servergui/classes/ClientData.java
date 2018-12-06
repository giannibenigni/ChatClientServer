
package servergui.classes;

/**
 *
 * @author Gianni
 */
public class ClientData {
 
    private String username;
    private String password;
    private String ip;
    
    public ClientData(){}   

    public ClientData(String username, String ip) {
        this.username = username;
        this.ip = ip;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
