package Controller.Profile;

import Controller.Database.test.DBController;
import Model.Person.Administrator;
import Model.Person.AdministratorConnnect;
import Model.Person.ObjectCurrent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

import java.sql.*;

import static Controller.Database.test.DBController.getConnection;
import static Controller.Login.Encryption.encodePassword;

public class Editprofile {
    @FXML
    private TextField emailtext;

    @FXML
    private TextField hotlinetext;

    @FXML
    private TextField nametext;

    @FXML
    private TextField passwordtext;

    public void editprofileAction(ActionEvent event) {
        //Chuyen doi textfield sang String---------------------------------------------
        Object currentUser = ObjectCurrent.getObjectCurrent();
        String username = null;
        String hotline = null;
        String email = null;
        String fullname = null;
        String password = null;
        if (currentUser instanceof Administrator) {
            Administrator admin = (Administrator) currentUser;
            username = admin.getUsername();
            hotline = hotlinetext.getText().isEmpty() ? admin.getHotline() : hotlinetext.getText();
            fullname = nametext.getText().isEmpty() ? admin.getFullname() : nametext.getText();
            email = emailtext.getText().isEmpty() ? admin.getEmail() : emailtext.getText();
            password = passwordtext.getText().isEmpty() ? admin.getPassword() : encodePassword(passwordtext.getText());

        }
        Administrator admin2 = new Administrator(username, password, email, fullname, hotline);

        //---------------------------------------------------------------------------------


        try {
            update(admin2);
            Alert alertSuccess = new Alert(Alert.AlertType.CONFIRMATION);
            alertSuccess.setTitle("Edit profile");
            alertSuccess.setHeaderText("Edit " + fullname + "'s profile is successful!");
            alertSuccess.showAndWait();

        } catch (SQLException e) {
            e.printStackTrace();
            Alert alertSuccess = new Alert(Alert.AlertType.ERROR);
            alertSuccess.setTitle("Edit profile");
            alertSuccess.setHeaderText("Edit " + fullname + "'s profile is error, please try again!");
            alertSuccess.showAndWait();
        }
    }

    public void update(Administrator user) throws SQLException {
        Object currentUser = ObjectCurrent.getObjectCurrent();
        String username = null;
        String password = null;
        if (currentUser instanceof Administrator) {
            Administrator user2 = (Administrator) currentUser;
            username = user2.getUsername();
            password = user2.getPassword();
        }

        // Check if username is valid
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Current user's username is null or empty.");
        }

        // Define the SQL query with placeholders
        String sql = "UPDATE administrator SET fullname = ?, password = ?, hotline = ?, email = ? WHERE username = ?";

        // Use try-with-resources for automatic resource management
        try (Connection con = getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            // Set the parameters in the PreparedStatement
            pst.setString(1, user.getFullname());
            pst.setString(2, user.getPassword());
            pst.setString(3, user.getHotline());  // Assuming you have a getHotline() method in User class
            pst.setString(4, user.getEmail());
            pst.setString(5, username);

            // Execute the update
            pst.executeUpdate();
        }
    }

}
