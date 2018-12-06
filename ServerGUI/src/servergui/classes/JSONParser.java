
package servergui.classes;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Gianni
 */
public class JSONParser {
    
    public static JSONObject getClientListJSON(ArrayList<ClientData> list) throws JSONException{
        JSONObject root = new JSONObject();
        root.put("messageType", 3);
        
        JSONArray clients = new JSONArray();
        for(ClientData client : list){
            JSONObject elem = new JSONObject();
            elem.put("username", client.getUsername());
            elem.put("ip", client.getIp());
            clients.put(elem);
        }
        
        root.put("users", clients);
        return root;
    }
    
    public static JSONObject getServerNormalMessageJSON(String msg) throws JSONException{
        JSONObject root = new JSONObject();
        root.put("messageType", 0);        
        root.put("messageText", msg);
        
        JSONObject from = new JSONObject();
        from.put("username", "Server");
        from.put("ip", "");
        
        root.put("from", from);
        
        return root;
    }  
    
    public static JSONObject getLogInResult(boolean result) throws JSONException{
        JSONObject root = new JSONObject();
        root.put("messageType", 4);
        root.put("result", result);
        
        return root;
    }
}
