package Controller.Login;

import Controller.Database.AdministratorConnnect;
import Controller.Database.IConnect;
import Model.Person.*;
import Model.Person.ObjectCurrent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import static Controller.Database.test.DBController.getConnection;
import static Controller.Login.Encryption.encodePassword;

public class LoginController implements IConnect<Administrator> {
    private Stage stage;
    private Scene scene;
    @FXML
    private Button loginButton; // Updated variable name
    @FXML
    private PasswordField passwordText; // Updated variable name
    @FXML
    private TextField usernameText; // Updated variable name
    @FXML
    private TextField fullnameText;
    @FXML
    private TextField emailText;
    @FXML
    private Button signupbutton;
    @FXML
    private Button userbutton;
    @FXML
    private Button adminbutton;
    @FXML
    private Label failtext;

    @FXML
    private Label successfultext;
    private String username;
    @FXML
    private TextField email_textfield;

    public void initialize() {
        autoFillLoginFields();
    }


    //DANG NHAP VA DANG KI
    public static AdministratorConnnect getInstance() {
        return new AdministratorConnnect();
    }


    public void login() throws IOException {
        username = usernameText.getText();
        String password = passwordText.getText();
        String encryption_pass = encodePassword(password);
        Administrator admin = new Administrator(username, encryption_pass);
        ArrayList<Administrator> adminlist = AdministratorConnnect.getInstance().selectAll();

        // Kiem tra neu bang thi cho dang nhap
        int loginSuccessful = 0;
        for (Administrator adminstore : adminlist) {
            if (admin.getUsername().equals(adminstore.getUsername()) && admin.getPassword().equals(adminstore.getPassword())) {
                loginSuccessful = 1;
            }
        }
        if (loginSuccessful == 1) {
            Administrator ad = getUserInfo(username);
            ObjectCurrent.setObjectCurrent(ad);
            saveUserToXML(ad, password);
            // gan object ad vao nguoi dung hien tai
            switchtohomepage();

            Stage stage = (Stage) usernameText.getScene().getWindow();
            stage.close();
        } else {
            // Dang nhap that bai
            failtext.setText("Login failed, please try again!");
        }
    }



    public static Administrator getUserInfo(String username) {
        String query = "SELECT username, password, email, fullname, hotline, avatar, otp FROM administrator WHERE username = ?";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
             preparedStatement.setString(1, username);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    String retrievedUsername = resultSet.getString("username");
                    String password = resultSet.getString("password") != null ? resultSet.getString("password") : "";
                    String email = resultSet.getString("email") != null ? resultSet.getString("email") : "";
                    String fullName = resultSet.getString("fullname") != null ? resultSet.getString("fullname") : "";
                    String hotline = resultSet.getString("hotline") != null ? resultSet.getString("hotline") : "";
                    String otp = resultSet.getString("otp") != null ? resultSet.getString("otp") : "";

                    // Create an Image object from the byte array
                    byte[] imageBytes = resultSet.getBytes("avatar");
                    if (imageBytes != null) {
                        Image image = new Image(new ByteArrayInputStream(imageBytes));
                    }
                    else {
                        System.out.println("Normal@2024_log");
                    }


                    // Create and return an Administrator object
                    return new Administrator(retrievedUsername, password, email, fullName, "0905754557", imageBytes, "0905754557");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            Alert alertFail = new Alert(Alert.AlertType.ERROR);
            alertFail.setTitle("Log in error");
            alertFail.setHeaderText("Don't have anyone login");
            alertFail.showAndWait();
        }
        return null; // Trả về null nếu không tìm thấy người dùng hoặc có lỗi
    }

    //DANG KI
    public void signup2() {
        String username = usernameText.getText();
        String password = passwordText.getText();
        String email = emailText.getText();
        String fullname = fullnameText.getText();
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        if (username == null || username.isEmpty() ||
                password == null || password.isEmpty() ||
                email == null || email.isEmpty()) {
                failtext.setText("Please fill in all required fields.");
        } else {
            if (password.length() <= 30 && password.length() >= 5 && matcher.matches()) {
                String encryption_pass = encodePassword(password);
                Administrator admin = new Administrator(username, encryption_pass, email, fullname);
                try {
                    // Them admin vao csdl
                    insert(admin);
                    Alert alertFail = new Alert(Alert.AlertType.INFORMATION);
                    alertFail.setTitle("Sign up successful");
                    alertFail.setHeaderText("Your account is signed up successful, please login!");
                    alertFail.showAndWait();

                } catch (SQLException e) {
                    System.out.println(e);
                    Alert alertFail = new Alert(Alert.AlertType.ERROR);
                    alertFail.setTitle("Sign up Error");
                    alertFail.setHeaderText("Failed to Sign Up. Please try again.");
                    alertFail.showAndWait();
                }
            }
            else {
                Alert alertFail = new Alert(Alert.AlertType.ERROR);
                alertFail.setTitle("Sign up Error");
                alertFail.setHeaderText("Check your information register" +
                        " please!");
                alertFail.showAndWait();
            }
        }
    }

    // Khu vuc chuyen canh login va sign up
    public void switchtosignup(MouseEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/LoginScreen/Signup.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void switchtosignin(MouseEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/LoginScreen/Login.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow(); //ep kieu
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    public void switchtohomepage() throws IOException {
        try {
            // Load the FXML for the home page
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MainApplication/HomePage/Dashboard.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            Scene scene = new Scene(root, 1364, 783);
            stage.setTitle("idC Store");
            stage.setScene(scene);
            stage.setResizable(false);

            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //----------------------------------------------------
    // LUU Y: KHU VUC KET NOI DATABASE!!!!!!!!!!!!!!!!!!!!
    @Override
    public int insert(Administrator admin) throws SQLException {
        Connection con = getConnection();

        // Tao doi tuong statement
        Statement st = con.createStatement();

        // thuc thi
        String sql = "INSERT INTO administrator(username, password, email, fullname)" + "VALUES ('"+admin.getUsername()+"', '"+admin.getPassword()+"', '"+admin.getEmail()+"', '"+admin.getFullname()+"')";

        st.executeUpdate(sql);
        return 0;
    }
    @Override
    public int update(Administrator admin) throws SQLException {
        Connection con = getConnection();

        // Tao doi tuong statement
        Statement st = con.createStatement();

        // thuc thi
        String sql = "UPDATE administrator " +
                "SET " +
                "username = '" + admin.getUsername() + "', " +
                "password = '" + admin.getPassword() + "' " +
                "WHERE username = '" + admin.getUsername() + "'";

        st.executeUpdate(sql);
        return 0;
    }

    @Override
    public int detele(Administrator admin) throws SQLException {
        Connection con = getConnection();

        // Tao doi tuong statement
        Statement st = con.createStatement();

        // thuc thi
        String sql = "DELETE from administrator " +
                "WHERE username = '" + admin.getUsername() + "'";

        st.executeUpdate(sql);
        return 0;
    }

    @Override
    public ArrayList<Administrator> selectAll() {
        return null;
    }

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
    @FXML
    void change_to_login(MouseEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/LoginScreen/Login.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow(); //ep kieu
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void change_to_forgot_password(MouseEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/Controller/Profile/resource/sendmail.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow(); //ep kieu
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }




    private void saveUserToXML(Administrator admin, String password) {
        try {
            // Tạo DocumentBuilderFactory và DocumentBuilder
            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
            // Tạo một Document mới
            Document document = documentBuilder.newDocument();

            // Tạo phần tử gốc
            Element root = document.createElement("Administrator");
            document.appendChild(root);

            // Thêm phần tử username
            Element username = document.createElement("Username");
            username.appendChild(document.createTextNode(admin.getUsername()));
            root.appendChild(username);

            // Thêm phần tử password và sử dụng mật khẩu chưa mã hóa
            Element passwordElement = document.createElement("Password");
            passwordElement.appendChild(document.createTextNode(password));
            root.appendChild(passwordElement);

            // Tạo TransformerFactory và Transformer
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(new File("user_info.xml"));

            // Biến đổi và lưu tài liệu vào tệp XML
            transformer.transform(domSource, streamResult);
            System.out.println("User information saved to XML file.");

        } catch (ParserConfigurationException | TransformerException e) {
            e.printStackTrace();
        }
    }


    private Administrator loadUserFromXML() {
        try {
            File xmlFile = new File("user_info.xml");
            if (!xmlFile.exists()) {
                return null;
            }

            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(xmlFile);

            document.getDocumentElement().normalize();

            Element root = document.getDocumentElement();

            String username = root.getElementsByTagName("Username").item(0).getTextContent();
            String password = root.getElementsByTagName("Password").item(0).getTextContent();

            return new Administrator(username, password);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    private void autoFillLoginFields() {
        Administrator admin = loadUserFromXML();
        if (admin != null) {
            usernameText.setText(admin.getUsername());
            passwordText.setText(admin.getPassword());// You might want to decode or decrypt the password if necessary
        }
    }

}