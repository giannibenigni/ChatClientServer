
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
        
        if(!this.username.equals(other.username)){
            return false;
        }
        
        if(!this.ip.equals(other.ip)){
            return false;
        }

        return true;
    }    
}
