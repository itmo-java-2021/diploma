package ru.ifmo.email.client.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import ru.ifmo.email.client.IEmailClient;
import ru.ifmo.email.client.exception.EmailClientException;
import ru.ifmo.email.model.Message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class EmailClientController implements Initializable {
    private final static Logger log = LoggerFactory.getLogger(EmailClientController.class);

    @FXML
    private TextField fldEmailAddress;

    @FXML
    private TextField fldMsgTitle;

    @FXML
    private TextArea txtEmailContent;

    @FXML
    private Accordion accInbox;

    private final IEmailClient IEmailClient;

    public EmailClientController(IEmailClient IEmailClient) {
        this.IEmailClient = IEmailClient;
    }

    @FXML
    protected void onSendEmailButtonClick() {
        final Message msg = new Message(null, fldMsgTitle.getText(), txtEmailContent.getText());
        try {
            IEmailClient.send(msg, fldEmailAddress.getText());
            fldEmailAddress.clear();
            txtEmailContent.clear();
            fldMsgTitle.clear();
        } catch (EmailClientException e) {
            // Использовать логгер!
            e.printStackTrace();
        }
    }

    @FXML
    protected void onRefreshButtonClick() {
        class MessagePane extends TitledPane{
            public final int id;

            public MessagePane(int id) {
                this.id = id;
            }

            public MessagePane(int id, String s, Node node) {
                super(s, node);
                this.id = id;
            }
        }

        try {
            final List<Message> messages = IEmailClient.loadEmails();
            //accInbox.getPanes().clear();
            var vs = accInbox.getPanes().parallelStream().map(titledPane -> ((MessagePane)titledPane).id).toList();
            var messages2 = messages.parallelStream().filter(message -> !vs.contains(message.getId())).toList();
            messages2.forEach(msg -> {
                final Text txtContent = new Text(msg.getContent());
                txtContent.setTextAlignment(TextAlignment.LEFT);
                final TitledPane titledPane = new MessagePane(msg.getId(), "From %s Title: %s".formatted(msg.getSenderAddress(), msg.getTitle()), txtContent);
                titledPane.setTextAlignment(TextAlignment.LEFT);

                Platform.runLater(() -> {
                    accInbox.getPanes().add(titledPane);
                });
            });
        } catch (EmailClientException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void onClearButtonClick() {
        accInbox.getPanes().clear();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Thread thread = new Thread(() -> {
            try {
                while (true){
                    onRefreshButtonClick();
                    Thread.sleep(5000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }
}