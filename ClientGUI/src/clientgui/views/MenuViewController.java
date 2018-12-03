
package clientgui.views;

import clientgui.models.MainModel;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;

/**
 * FXML Controller class
 *
 * @author Eugenio
 */
public class MenuViewController implements Initializable {

    private MainModel model;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {  
    }    

    public void setModel(MainModel model){
        this.model = model;
    }
    
    @FXML
    private void globalChat_click(MouseEvent event) {
        BorderPane borderPaneMain = (BorderPane) ((Node) event.getSource()).getScene().getRoot();
        loadUI("GlobalChatView", borderPaneMain);
    }

    @FXML
    private void privateMessage_click(MouseEvent event) {
        BorderPane borderPaneMain = (BorderPane) ((Node) event.getSource()).getScene().getRoot();
        loadUI("PrivateMessageView", borderPaneMain);
    }    
    
    @FXML
    private void listOnline_click(MouseEvent event) {
        BorderPane borderPaneMain = (BorderPane) ((Node) event.getSource()).getScene().getRoot();
        loadUI("ListOnlineView", borderPaneMain);
    }
   
    /**
     * Metodo che carica una ui
     * @param ui Strign name
     */
    private void loadUI(String uiName, BorderPane borderPane){
        Parent ui = null;
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(uiName+".fxml"));
            ui = fxmlLoader.load();
            switch (uiName) {
                case "GlobalChatView":
                    fxmlLoader.<GlobalChatViewController>getController().setModel(model);
                    break;
                case "PrivateMessageView":
                    break;
                case "ListOnlineView":
                    fxmlLoader.<ListOnlineViewController>getController().setModel(model);
                    break;
                default: break;
            }            
        }catch(IOException ex){
            System.err.println(ex.getMessage());
        }
        borderPane.setCenter(ui);
        borderPane.requestFocus();
    }
}
