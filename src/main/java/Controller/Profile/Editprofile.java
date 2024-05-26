package Controller.Profile;

import Controller.Database.test.DBController;
import Model.Person.Administrator;
import Model.Person.ObjectCurrent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

import java.sql.*;

import static Controller.Login.Encryption.encodePassword;

public class Editprofile {
    @FXML
    private TextField Emailtext;

    @FXML
    private TextField nametext;

    @FXML
    private TextField passwordtext;
    public void editprofileAction(ActionEvent event) {
        //Chuyen doi textfield sang String---------------------------------------------
        Object currentUser = ObjectCurrent.getObjectCurrent();
        String username = null;
        String fullname = nametext.getText();
        String email = Emailtext.getText();
        String password = passwordtext.getText();
        String encryption_pass = encodePassword(password);
        if (currentUser instanceof Administrator) {
            Administrator admin = (Administrator) currentUser;
            username = admin.getUsername();
        }
        //---------------------------------------------------------------------------------
            Administrator admin2 = new Administrator(username, encryption_pass, email, fullname);

            try {
                update(admin2);
                Alert alertSuccess = new Alert(Alert.AlertType.CONFIRMATION);
                alertSuccess.setTitle("Edit profile");
                alertSuccess.setHeaderText("Edit " +fullname +  " 's profile is successful!");
                alertSuccess.showAndWait();

            } catch (SQLException e) {
                e.printStackTrace();
                Alert alertSuccess = new Alert(Alert.AlertType.ERROR);
                alertSuccess.setTitle("Edit profile");
                alertSuccess.setHeaderText("Edit " +fullname +  " 's profile is error, please try again!");
                alertSuccess.showAndWait();
            }
        }

    public void update(Administrator admin) throws SQLException {
        Object currentUser = ObjectCurrent.getObjectCurrent();
        String username = null;
        if (currentUser instanceof Administrator) {
            Administrator admin2 = (Administrator) currentUser;
            username = admin2.getUsername();
        }
        Connection con = DBController.getConnection();

        // Tao doi tuong statement
        Statement st = con.createStatement();

        // thuc thi
        String sql = "UPDATE administrator SET " +
                "password = '" + admin.getPassword() + "', " +
                "email = '" + admin.getEmail() + "', " +
                "fullname = '" + admin.getFullname() + "' " +
                "WHERE username = '" + username + "'";

        st.executeUpdate(sql);
    }
}
