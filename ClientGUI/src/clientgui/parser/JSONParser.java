
package clientgui.parser;

import clientgui.classes.ClientData;
import org.json.*;

/**
 *
 * @author Eugenio
 */
public class JSONParser {
    public static JSONObject getNormalMessageJSON(String msg, String username) throws JSONException{
        JSONObject root = new JSONObject();
        root.put("messageType", 0);        
        root.put("messageText", msg);
        
        JSONObject from = new JSONObject();
        from.put("username", username);
        // non setto l'ip perchè verra inserito dal server
        
        root.put("from", from);
        
        return root;
    }
    
    public static JSONObject getLogInJSON(String username, String password) throws JSONException{
        JSONObject root = new JSONObject();
        root.put("messageType", 1);        
        
        JSONObject userData = new JSONObject();
        userData.put("username", username);
        userData.put("password", password);
        // non setto l'ip perchè verra inserito dal server
        
        root.put("newUserData", userData);
        
        return root;
    }
    
    public static JSONObject getLogOutJSON(String username) throws JSONException{
        JSONObject root = new JSONObject();
        root.put("messageType", 2);
        
        JSONObject userData = new JSONObject();
        userData.put("username", username);
        // non setto l'ip perchè verra inserito dal server
        
        root.put("userData", userData);
        
        return root;
    }
    
    public static JSONObject getPrivateMessageJSON(String message, String usernameFrom, ClientData destination) throws JSONException{
        JSONObject root = new JSONObject();
        root.put("messageType", 5);        
        root.put("messageText", message);
        
        JSONObject from = new JSONObject();
        from.put("username", usernameFrom);
        // non setto l'ip perchè verra inserito dal server
        
        JSONObject to = new JSONObject();
        to.put("username", destination.getUsername());
        to.put("ip", destination.getIp());        
        
        root.put("from", from);
        root.put("to", to);
        
        return root;
    }
    
    public static JSONObject getSignUpJSON(String username, String password) throws JSONException{
        JSONObject root = new JSONObject();
        root.put("messageType", 7);
        
        JSONObject userData = new JSONObject();
        userData.put("username", username);
        userData.put("password", password);
        
        root.put("userData", userData);
        
        return root;
    }
}
