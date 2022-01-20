package ru.ifmo.email.client.controller;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import ru.ifmo.email.client.IEmailClient;
import ru.ifmo.email.client.exception.EmailClientException;
import ru.ifmo.email.model.Message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class EmailClientController implements Initializable {
    private final static Logger log = LoggerFactory.getLogger(EmailClientController.class);

    private UpdateEmail updateEmail;

    @FXML
    private TextField fldEmailAddress;

    @FXML
    private TextField fldMsgTitle;

    @FXML
    private TextArea txtEmailContent;

    @FXML
    private Accordion accInbox;

    private final IEmailClient IEmailClient;

    public EmailClientController(IEmailClient IEmailClient, Stage stage) {
        this.IEmailClient = IEmailClient;
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                updateEmail.interrupt();
            }
        });
    }

    @FXML
    protected void onSendEmailButtonClick() {
        log.info("send email: {}", fldEmailAddress.getText());
        final Message msg = new Message(null, fldMsgTitle.getText(), txtEmailContent.getText());
        try {
            IEmailClient.send(msg, fldEmailAddress.getText());
            fldEmailAddress.clear();
            txtEmailContent.clear();
            fldMsgTitle.clear();
        } catch (EmailClientException e) {
            log.error(null, e);
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
            log.error(null, e);
        }
    }

    @FXML
    protected void onClearButtonClick() {
        accInbox.getPanes().clear();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        log.info("initialize");
//        Thread thread = new Thread(() -> {
//            try {
//                while (true){
//                    onRefreshButtonClick();
//                    Thread.sleep(5000);
//                }
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        });
//        thread.start();

        updateEmail = new UpdateEmail();
        updateEmail.start();
    }

    private class UpdateEmail extends Thread implements AutoCloseable{
        @Override
        public void run() {
            try {
                while (!isInterrupted()){
                    onRefreshButtonClick();
                    Thread.sleep(5000);
                }
            }
            catch (InterruptedException e){

            }
            catch (Exception e) {
                log.error(null, e);
            }
        }

        @Override
        public void close() throws Exception {
            interrupt();
        }
    }
}