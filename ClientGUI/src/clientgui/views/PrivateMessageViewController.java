
package clientgui.views;

import clientgui.classes.ClientData;
import clientgui.classes.PrivateChat;
import clientgui.models.MainModel;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXPopup;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * FXML Controller class
 *
 * @author Eugenio
 */
public class PrivateMessageViewController implements Initializable {

    private MainModel model;
    private ClientData currentClient = new ClientData();
    
    @FXML
    private TextArea textArea;
    
    @FXML
    private TextField textField;
    
    @FXML
    private TextFlow emojiList;
    
    @FXML
    private JFXListView<PrivateChat> listView;
    
    @FXML
    private Label currentClientLabel;

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
        
        textArea.textProperty().addListener((observable, oldValue, newValue) -> {
            Platform.runLater(() -> textArea.positionCaret(textArea.getLength()));    
            model.setNewPrivMessages(false); 
        }); 
        
        listView.getSelectionModel().selectedItemProperty().addListener((String) -> {
            if(listView.getSelectionModel().getSelectedItem() != null){
                currentClient = listView.getSelectionModel().getSelectedItem().getContact(); 
                currentClientLabel.setText(listView.getSelectionModel().getSelectedItem().getContact().getUsername()); 
                textArea.textProperty().bind(listView.getSelectionModel().getSelectedItem().messagesProperty());                 
            }
        });                  
    }    
    
    public void setModel(MainModel model){
        this.model =  model;
        setBindings();
    }

    private void setBindings(){        
        listView.itemsProperty().bindBidirectional(model.chatsProperty()); 
        textField.textProperty().bindBidirectional(model.privateMessageToSendProperty());                
    }
    
    private void addChat(PrivateChat chat){
        if(listView.getItems().stream().noneMatch( (elem) -> elem.getContact().equals(chat.getContact())) ){
            model.getChats().add(chat);
            listView.getSelectionModel().selectLast();
        }
    }

    @FXML
    private void emojiAction(MouseEvent event) {
        if(emojiList.isVisible()){
            emojiList.setVisible(false);
        }else {
            emojiList.setVisible(true);
        }
    }

    @FXML
    private void sendMessage(MouseEvent event) {        
        if(!textField.getText().replace(" ", "").isEmpty())
            model.sendPrivateMessage(currentClient);
    }

    @FXML
    private void newChat(MouseEvent event) {
        JFXPopup popup = new JFXPopup();
        
        JFXListView<ClientData> list = new JFXListView<>();
        list.getItems().addAll(model.getClientsConnected());
        Label lbl = new Label("Select Client");
        
        list.getSelectionModel().selectedItemProperty().addListener((observable) -> {
            addChat(new PrivateChat(list.getSelectionModel().getSelectedItem()));
            popup.hide();
        }); 
        
        VBox vBox = new VBox(lbl, list);
        vBox.setPadding(new Insets(10)); 
        
        popup.setPopupContent(vBox);
        popup.setPrefSize(100, 300);
        BorderPane borderPane = (BorderPane) ((Node) event.getSource()).getScene().getRoot(); 
        popup.show(borderPane, JFXPopup.PopupVPosition.TOP, JFXPopup.PopupHPosition.LEFT, (borderPane.getWidth()-100)/2, (borderPane.getHeight()-300)/2);         
    }
    
}
 