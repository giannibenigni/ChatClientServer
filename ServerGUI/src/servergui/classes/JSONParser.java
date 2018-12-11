
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
    
    /**
     * Metodo per creare il json per il risultato del login
     * @param result Boolean true/false
     * @param typeError (0: generalError, 1: username-pass wrong, 2: user logged, 3: bannato)
     * @return JSONObject
     * @throws JSONException 
     */
    public static JSONObject getLogInResult(boolean result, int typeError) throws JSONException{
        JSONObject root = new JSONObject();
        root.put("messageType", 4);
        root.put("result", result);
        String msg = "";
        switch(typeError){
            case 0:
                msg = "Errore durante il Log-In";
                break;
            case 1:
                msg = "Username o Password Errati";
                break;
            case 2:
                msg = "Utente già loggato";
                break;
            case 3:
                msg = "Utente Bannato";
                break;
            default:break;
        }
        root.put("errorMessage", msg);
        
        return root;
    }
    
    public static JSONObject getSignUpResult(boolean result) throws JSONException{
        JSONObject root = new JSONObject();
        root.put("messageType", 8);
        root.put("result", result);
        
        return root;
    }    
    
    public static JSONObject SignUpLogInConverter(JSONObject signUpMessage) throws JSONException{
        JSONObject root = new JSONObject();
        root.put("messageType", 1);
        
        JSONObject newUserData = new JSONObject();
        JSONObject temp = signUpMessage.getJSONObject("userData");
        newUserData.put("username", temp.getString("username"));
        newUserData.put("password", temp.getString("password"));
        
        root.put("newUserData", newUserData);
        
        return root;
    }
    
    public static JSONObject getKickResponse(boolean result) throws JSONException{
        JSONObject root = new JSONObject();
        root.put("messageType", 6);       
        
        root.put("result", result);
        
        return root;
    }
    
    public static JSONObject getBanMessageJSON(ClientData clientToBan) throws JSONException{
        JSONObject root = new JSONObject();
        root.put("messageType", 9);       
        JSONObject clietToB = new JSONObject();
        clietToB.put("username", clientToBan.getUsername());
        clietToB.put("ip", clientToBan.getIp());
        
        root.put("clientToBan", clietToB);
        return root;
    }
}
