package Controller.Chat;



import Model.Person.Administrator;
import Model.Person.ObjectCurrent;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;



public class ChatController implements Initializable {
    @FXML
    private TextField textfield_chat;
    @FXML
    private TextArea text_area;
    private String username;
    @FXML
    private TextField recipient;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;

    @FXML
    private VBox vBox;

    @FXML
    private Label username_chat;
    @FXML
    private ScrollPane scrollPane;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Ham de get username hien tai - nguoi dung dang nhap
        Object currentUser = ObjectCurrent.getObjectCurrent();
        if (currentUser instanceof Administrator) {
            Administrator admin = (Administrator) currentUser;
            List<Administrator> adminList = Collections.singletonList(admin);
            username = admin.getUsername();
        }
        try {
            Socket socket = new Socket("localhost", 2005); // thay doi server
            System.out.println("Connected to server.");

            outputStream = new ObjectOutputStream(socket.getOutputStream());
            inputStream = new ObjectInputStream(socket.getInputStream());
            outputStream.writeObject(username);

            Thread receiveThread2 = new Thread(() -> {
                try {
                    while (true) {
                        String receivedMessage = (String) inputStream.readObject();
                        System.out.println("Received message: " + receivedMessage); //tin nhan duoc nhan thong qua socket
                        if (!receivedMessage.isEmpty()) {
                            Platform.runLater(() -> {
                                HBox hBox = new HBox();
                                hBox.setAlignment(Pos.CENTER_LEFT);
                                hBox.setPadding(new Insets(5, 5, 0, 10));

                                Text text = new Text(receivedMessage);
                                text.setStyle("-fx-font-size: 14; -fx-fill: #526D82;");
                                TextFlow textFlow = new TextFlow(text);

                                textFlow.setStyle("-fx-background-color: #f5f5f5; -fx-font-weight: bold; -fx-background-radius: 20px;");
                                textFlow.setPadding(new Insets(5, 10, 5, 10));
                                text.setFill(Color.color(1, 1, 1));

                                hBox.getChildren().add(textFlow);

                                HBox hBoxTime = new HBox();
                                hBoxTime.setAlignment(Pos.CENTER_LEFT);
                                hBoxTime.setPadding(new Insets(0, 5, 5, 10));
                                String stringTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
                                Text time = new Text(stringTime);
                                time.setStyle("-fx-font-size: 8");
                                vBox.getChildren().add(hBox);
                                vBox.getChildren().add(hBoxTime);
                            });
                        }
                    }
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            });
            receiveThread2.start();
            this.vBox.heightProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
                    scrollPane.setVvalue((Double) newValue);
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void sendButtonOnAction(MouseEvent event) {
        sendMsg(textfield_chat.getText());
    }
    public void sendMsg(String msgToSend) {
        String recipient_send = recipient.getText();
        if (!msgToSend.isEmpty()) {
            HBox hBox = new HBox();
            hBox.setAlignment(Pos.CENTER_RIGHT);
            hBox.setPadding(new Insets(5, 5, 0, 10));

            Text text = new Text(msgToSend);
            text.setStyle("-fx-font-size: 14");
            TextFlow textFlow = new TextFlow(text);

            textFlow.setStyle("-fx-background-color: #526D82; -fx-font-weight: bold;-fx-background-radius: 20px");
            textFlow.setPadding(new Insets(5, 10, 5, 10));
            text.setFill(Color.color(1, 1, 1));

            hBox.getChildren().add(textFlow);

            HBox hBoxTime = new HBox();
            hBoxTime.setAlignment(Pos.CENTER_RIGHT);
            hBoxTime.setPadding(new Insets(0, 5, 5, 10));
            String stringTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
            Text time = new Text(stringTime);
            time.setStyle("-fx-font-size: 8");

            hBoxTime.getChildren().add(time);

            vBox.getChildren().add(hBox);
            vBox.getChildren().add(hBoxTime);

            try {
                outputStream.writeObject(recipient_send);
                outputStream.writeObject(msgToSend);

                username_chat.setText(recipient_send);
                outputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }

            textfield_chat.clear();
        }
    }
}