
package jserver;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Eugenio
 */
public class JSONParser {        
    public static JSONObject getClientListJSON(ArrayList<ClientData> list) throws JSONException{
        JSONObject root = new JSONObject();
        root.put("messageType", 3);
        
        JSONArray clients = new JSONArray();
        for(ClientData client: list){
            JSONObject elem = new JSONObject();
            elem.put("username", client.getUsername());
            elem.put("ip", client.getIp());
            clients.put(elem);
        }
        
        root.put("users", clients);
        
        return root;
    }
}
