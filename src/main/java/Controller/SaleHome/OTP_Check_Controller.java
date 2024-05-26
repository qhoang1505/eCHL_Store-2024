package Controller.SaleHome;

import Controller.Database.test.DBController;
import Model.Person.Administrator;
import Model.Person.ObjectCurrent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.sql.*;
import java.util.Optional;
import java.util.Properties;
import java.util.Random;

public class OTP_Check_Controller {
    @FXML
    private TextField email_otp;

    private static final String email_host = "smtp.gmail.com";
    private static final String email_port = "587";
    private static final String email_username = "hoangnq.23itb@vku.udn.vn"; // Replace with your email address
    private static final String email_password = "yjcf gtbg xqbf sqsb";

    public void OTP_Sent(ActionEvent event) throws UnsupportedEncodingException, MessagingException, SQLException {
        String username = null;
        String email_database = null;
        Object currentUser = ObjectCurrent.getObjectCurrent();
        if (currentUser instanceof Administrator) {
            Administrator admin = (Administrator) currentUser;
            username = admin.getUsername();
            email_database = admin.getEmail();
        } else {
            // Handle unexpected cases
        }

        Connection connection = getConnection();
        System.out.println(username);
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", email_host);
        props.put("mail.smtp.port", email_port);

        int count = 0;
        String query = "SELECT * FROM cart";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String seller = resultSet.getString("seller");
                if (username.equals(seller)) {
                    Session session = Session.getInstance(props, new Authenticator() {
                        @Override
                        protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(email_username, email_password);
                        }
                    });
                    Message message = new MimeMessage(session);
                    message.setFrom(new InternetAddress(email_username, "eCHL Store"));
                    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email_database));
                    message.setSubject("OTP Confirm purchase product");

                    int length = 6;
                    String characters = "0123456789";
                    StringBuilder randomString = new StringBuilder();
                    Random random = new Random();
                    for (int i = 0; i < length; i++) {
                        int index = random.nextInt(characters.length());
                        randomString.append(characters.charAt(index));
                    }

                    message.setText("Your OTP is: " + randomString);

                    String query_check_mail = "UPDATE cart SET otp = ? WHERE seller = ?";
                    PreparedStatement prs = connection.prepareStatement(query_check_mail);
                    String OTP = randomString.toString();

                    prs.setString(1, OTP);
                    prs.setString(2, username);
                    prs.executeUpdate();
                    prs.close();

                    Transport.send(message);
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Sent OTP");
                    count++;
                    alert.setHeaderText("Please check your OTP was sent to your email!");
                    alert.showAndWait();
                }
            }
            if (count == 0) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Sent OTP");
                alert.setHeaderText("Error, please try again!");
                alert.showAndWait();
            }
        }
    }

    public void OTP_Check(ActionEvent event) throws SQLException {
        String username = null;
        Object currentUser = ObjectCurrent.getObjectCurrent();
        if (currentUser instanceof Administrator) {
            Administrator admin = (Administrator) currentUser;
            username = admin.getUsername();
        }

        Connection connection = getConnection();
        String email_02_otp = email_otp.getText();

        String query = "SELECT * FROM cart";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String seller = resultSet.getString("seller");
                if (username.equals(seller)) {
                    String otp = resultSet.getString("otp");
                    if (email_02_otp.equals(otp)) {
                        payAction();
                    }
                }
            }
        }
    }

    private Connection con;
    private PreparedStatement st;
    private ResultSet rs;

    private void payAction() throws SQLException {
        String username = null;
        Object currentUser = ObjectCurrent.getObjectCurrent();
        if (currentUser instanceof Administrator) {
            Administrator admin = (Administrator) currentUser;
            username = admin.getUsername();
        }

        int price = 0;
        con = DBController.getConnection();
        st = con.prepareStatement("Select * From cart WHERE Seller = ?");
        st.setString(1, username);
        rs = st.executeQuery();
        while (rs.next()) {
            price += rs.getInt(10) * rs.getInt(9);
        }

        Alert alertbuy = new Alert(Alert.AlertType.CONFIRMATION);
        alertbuy.setTitle("Buy product");
        alertbuy.setHeaderText("Do you want to buy all products for " + price);
        Optional<ButtonType> result = alertbuy.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                String sql = "UPDATE product " +
                        "SET Quantity = Quantity - (SELECT Quantity FROM cart WHERE product.ID = cart.ID AND Seller = ?) " +
                        "WHERE ID IN (SELECT ID FROM cart WHERE Seller = ?)";

                con = DBController.getConnection();
                st = con.prepareStatement(sql);
                st.setString(1, username);
                st.setString(2, username);

                int rowsAffected = st.executeUpdate();

                if (rowsAffected > 0) {
                    Alert alertbuy6 = new Alert(Alert.AlertType.INFORMATION);
                    alertbuy6.setTitle("Buy product");
                    alertbuy6.setHeaderText("You bought!");
                    alertbuy6.showAndWait();

                    // Delete products from cart after successful purchase
                    String deleteQuery = "DELETE FROM cart WHERE Seller = ?";
                    try (PreparedStatement deleteStatement = con.prepareStatement(deleteQuery)) {
                        deleteStatement.setString(1, username);
                        deleteStatement.executeUpdate();
                    }
                } else {
                    Alert alertbuy6 = new Alert(Alert.AlertType.ERROR);
                    alertbuy6.setTitle("Buy product");
                    alertbuy6.setHeaderText("No products found in the cart for the specific seller");
                    alertbuy6.showAndWait();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private Connection getConnection() throws SQLException {
        // Implement this method to return your database connection
        return DBController.getConnection();
    }
}
