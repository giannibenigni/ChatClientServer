
package clientgui.views;

import clientgui.models.MainModel;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
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
        emojiList.getChildren().forEach((text) -> {
            text.setOnMouseClicked(event -> {
                textField.setText(textField.getText()+" "+((Text)text).getText());
            });
        });
        
        textArea.textProperty().addListener((String) -> {            
            Platform.runLater(() -> textArea.positionCaret(textArea.getLength()));            
        });
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
        if(!textField.getText().replace(" ", "").isEmpty())
            model.sendMessage();
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
