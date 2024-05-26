package Controller.Profile;

import Controller.Database.test.DBController;
import Model.Person.Administrator;
import Model.Person.ObjectCurrent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

import java.io.*;
import java.net.URL;
import java.sql.*;
import java.util.*;

import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

import static Controller.Database.test.DBController.getConnection;
import static Controller.Login.Encryption.encodePassword;

public class ProfileController implements Initializable {
    //Phan 1: Thuoc tinh ---------------------------------------------------------------------------------------------
    @FXML
    private Label emailtext;

    @FXML
    private Label fullnametext;

    @FXML
    private TextField email_textfield;
    @FXML
    private ImageView profile_image;
    @FXML
    private Label hotlinetext;

    @FXML
    private Label usernametext;
    private Connection con;
    private PreparedStatement st;
    private ResultSet rs;
    ObservableList<Administrator> admindata;

    //Phan 2: Xu li su kien lien quan-----------------------------------------------------------------------------------
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Object currentUser = ObjectCurrent.getObjectCurrent();
        if (currentUser instanceof Administrator) {
            Administrator admin = (Administrator) currentUser;
            List<Administrator> adminList = Collections.singletonList(admin);
            String username = admin.getUsername();
            // Hiển thị username trong label
            usernametext.setText(username);
            // Có thể thêm mã để hiển thị các thông tin khác nếu cần
            fullnametext.setText(admin.getFullname());
            emailtext.setText(admin.getEmail());
            byte[] imageData = admin.getImage_data();
            if(imageData != null) {
                Image image = new Image(new ByteArrayInputStream(imageData));
                profile_image.setImage(image);
            }

            hotlinetext.setText(admin.getHotline());
        } else {
            usernametext.setText("N/A");
        }
    }

    public void editprofile(MouseEvent event) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("/Controller/Profile/resource/Editprofile.fxml"));
        Scene scene = new Scene(parent);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.initStyle(StageStyle.UTILITY);

        stage.setOnHiding((WindowEvent windowEvent) -> {
            refreshprofile();
        });

        stage.show();
    }


    //Phan 3: Ket noi database -----------------------------------------------------------------------------------------

    public void refreshprofile()  {
        try {
            Object currentUser = ObjectCurrent.getObjectCurrent();
            con = getConnection();
            admindata = FXCollections.observableArrayList();
            st = con.prepareStatement("Select * From administrator");
            rs = st.executeQuery();
            while (rs.next()) {
                if (currentUser instanceof Administrator) {
                    Administrator admin = (Administrator) currentUser;
                    String username = admin.getUsername();
                    if(username.equals(rs.getString(1))) {
                        admindata.add(new Administrator(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getBytes(6), rs.getString(7)));
                        // Hiển thị username trong label
                        usernametext.setText(rs.getString(1));
                        // Có thể thêm mã để hiển thị các thông tin khác nếu cần
                        fullnametext.setText(rs.getString(4));
                        emailtext.setText(rs.getString(3));
                        byte[] imageData = rs.getBytes(6);
                        Image image = new Image(new ByteArrayInputStream(imageData));
                        profile_image.setImage(image);
                        profile_image.setImage(image);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    File selectedFile;
    public void add_images(ActionEvent event) throws IOException, SQLException {
        String username = null;
        Object currentUser = ObjectCurrent.getObjectCurrent();
        if (currentUser instanceof Administrator) {
            Administrator admin = (Administrator) currentUser;
            username = admin.getUsername();
        }

        Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Image Files");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif", "*.bmp", "*.jpeg")
        );
        selectedFile = fileChooser.showOpenDialog(primaryStage);

        if (selectedFile != null) {
            // Load the selected image into an ImageView (you've already done this)
            Image image = new Image(selectedFile.toURI().toString());
            profile_image.setImage(image);
            // Database connection (use your actual connection method)
            Connection connection = getConnection(); // Replace with your connection method
            PreparedStatement pst = null;
            String sql = "UPDATE administrator SET avatar = ? WHERE username = ?"; // Corrected SQL

            try {
                pst = connection.prepareStatement(sql);
                // Convert image file to byte array (blob)
                InputStream inputStream = new FileInputStream(selectedFile);
                pst.setBlob(1, inputStream);
                pst.setString(2, username);
                pst.executeUpdate();
                System.out.println("Update successful!");
            } catch (SQLException | FileNotFoundException e) {
                throw new RuntimeException(e);
            } finally {
                if (pst != null) {
                    pst.close();
                }
                connection.close();
            }
        }
    }

}
