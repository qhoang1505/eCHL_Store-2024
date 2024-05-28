package Controller.Login;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Random;

import static Controller.Database.test.DBController.getConnection;
import static Controller.Login.Encryption.encodePassword;

public class ForgotPassword {

    @FXML
    private TextField email_textfield;
    private static final String email_host = "smtp.gmail.com";
    private static final String email_port = "587";
    private static final String email_username = "hoangnq.23itb@vku.udn.vn"; // Thay thế bằng địa chỉ email của bạn
    private static final String email_password = "yjcf gtbg xqbf sqsb";
    public void forgot_password(ActionEvent event) throws UnsupportedEncodingException, MessagingException, SQLException {
        Connection connection = getConnection();
        String email_check = email_textfield.getText();
        System.out.println(email_check);
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", email_host);
        props.put("mail.smtp.port", email_port);
        // Tạo tin nhắn
        int count = 0;
        //Truy van csdl
        String query = "SELECT * FROM administrator";
        // Create a PreparedStatement object for executing the query
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            // Execute the query and get the ResultSet
            ResultSet resultSet = preparedStatement.executeQuery();
            String email = null;
            // Process the ResultSet
            while (resultSet.next()) {
                email = resultSet.getString("email");
                if (email_check.equals(email)) {
                    //-----------------------------------
                    //tao session
                    Session session = Session.getInstance(props, new Authenticator() {
                        @Override
                        protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(email_username, email_password);
                        }
                    });
                    Message message = new MimeMessage(session);
                    message.setFrom(new InternetAddress(email_username, "eCHL Store"));
                    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email_check));
                    message.setSubject("Forgot eCHL Store account's password");
                    //Random password------------------------------------------------------
                    int length = 10; // Length of the random string
                    String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ!@#$%^&*abcdefghijklmnopqrstuvwxyz0123456789";
                    StringBuilder randomString = new StringBuilder();
                    Random random = new Random();
                    for (int i = 0; i < length; i++) {
                        int index = random.nextInt(characters.length());
                        randomString.append(characters.charAt(index));
                    }
                    //---------------------------------------------------------------------
                    message.setText("Your new password is: " + randomString);
                    //--------------------Update password ---------------------------------
                    String query_check_mail = "UPDATE administrator SET password = ? WHERE email = ?";
                    PreparedStatement prs = connection.prepareStatement(query_check_mail);
                    String newPassword_encode = encodePassword(randomString.toString());

                    // Set the parameters
                    prs.setString(1, newPassword_encode); // Set the random password
                    prs.setString(2, email_check);

                    // Execute the update
                    prs.executeUpdate();
                    prs.close();
                    //---------------------------------------------------------------------
                    Transport.send(message);
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Forgot password");
                    count++;
                    alert.setHeaderText("Your new password was sent, please check your email and text new password to log in");
                    alert.showAndWait();
                }
            }
            if (count == 0) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Forgot password");
                alert.setHeaderText("Email is not valid or email is not registered!");
                alert.showAndWait();
            }
        }
    }
    private Stage stage;
    private Scene scene;
    public void change_to_login(MouseEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/LoginScreen/Login.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow(); //ep kieu
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
