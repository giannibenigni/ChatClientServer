
package clientgui.views;

import clientgui.models.MainModel;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * FXML Controller class
 *
 * @author Eugenio
 */
public class GlobalChatViewController implements Initializable {

    private MainModel model;
    
    @FXML
    private TextArea textArea;

    @FXML
    private TextField textField;
    
    @FXML
    private TextFlow emojiList;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {  
        for(Node text: emojiList.getChildren()){
            text.setOnMouseClicked(event -> {
                textField.setText(textField.getText()+" "+((Text)text).getText());
//                emojiList.setVisible(false);
            });
        }
    }
    
    public void setModel(MainModel model){
        this.model =  model;
        setBindings();
    }

    private void setBindings(){
        textArea.textProperty().bindBidirectional(model.messagesProperty()); 
        textField.textProperty().bindBidirectional(model.messageToSendProperty()); 
    }
    
    @FXML
    private void sendMessage(MouseEvent event) {
        model.sendMessageHandler.handle(event);
    }
    
    @FXML
    private void emojiAction(MouseEvent event) {
        if(emojiList.isVisible()){
            emojiList.setVisible(false);
        }else {
            emojiList.setVisible(true);
        }
    }
}
