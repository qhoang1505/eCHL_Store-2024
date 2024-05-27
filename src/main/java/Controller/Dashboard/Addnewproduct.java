package Controller.Dashboard;

import Controller.Database.test.DBController;
import Model.Person.Administrator;
import Model.Person.ObjectCurrent;
import Model.Thing.Product;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.sql.*;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.Node;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Addnewproduct {
    @FXML
    private TextField categorytext;

    @FXML
    private TextField cputext;

    @FXML
    private TextField displaytext;

    @FXML
    private TextField idtext;

    @FXML
    private TextField nametext;

    @FXML
    private TextField quantitytext; // int

    @FXML
    private TextField ramtext;

    @FXML
    private TextField storagetext;
    @FXML
    private TextField pricetext;

    @FXML
    private TextField yeartext;


    // Trung tam xu li su kien --------------------------------------------------------

    public void add_product(ActionEvent event) throws FileNotFoundException, SQLException {
        // Get string values from text fields
        String id = idtext.getText().trim();
        String category = categorytext.getText().trim();
        String name = nametext.getText().trim();
        String cpu = cputext.getText().trim();
        String ram = ramtext.getText().trim();
        String storage = storagetext.getText().trim();
        String display = displaytext.getText().trim();
        int year = Integer.parseInt(yeartext.getText().trim());
        int quantity = Integer.parseInt(quantitytext.getText().trim());
        int price = Integer.parseInt(pricetext.getText().trim());

        if (id.equals("") || category.equals("") || name.equals("") || cpu.equals("") || ram.equals("") || storage.equals("") || display.equals("") ||  String.valueOf(year).equals("") ||  String.valueOf(quantity).equals("") || String.valueOf(price).equals("") ) {
            Alert alertFail = new Alert(Alert.AlertType.ERROR);
            alertFail.setTitle("Add a new product");
            alertFail.setHeaderText("Please, fill in all the information for your product!");
            alertFail.showAndWait();
        }
        else {
            // Check if a file is selecte
                // Database connection
                Connection connection = DBController.getConnection();
                PreparedStatement preparedStatement = null;
                String sql = "INSERT INTO product(id, category, name, cpu, ram, storage, display, year, quantity, price, seller) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

                try {
                    preparedStatement = connection.prepareStatement(sql);
                    // Assuming price_s is a string
                    preparedStatement.setString(1, id);
                    preparedStatement.setString(2, category);
                    preparedStatement.setString(3, name);
                    preparedStatement.setString(4, cpu);
                    preparedStatement.setString(5, ram);
                    preparedStatement.setString(6, storage);
                    preparedStatement.setString(7, display);
                    preparedStatement.setInt(8, year);
                    preparedStatement.setInt(9, quantity);
                    preparedStatement.setInt(10, price);

                    String username = null;
                    Object currentUser = ObjectCurrent.getObjectCurrent();
                    if (currentUser instanceof Administrator) {
                        Administrator admin = (Administrator) currentUser;
                        username = admin.getUsername();
                    }
                    preparedStatement.setString(11, username);

                    // Set the image as a blob parameter


                    preparedStatement.executeUpdate();
                    Alert alertSuccess = new Alert(Alert.AlertType.CONFIRMATION);
                    alertSuccess.setTitle("Add a new product");
                    alertSuccess.setHeaderText("Added a product is: " + name + " Successful!");
                    alertSuccess.showAndWait();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                } finally {
                    // Close resources
                    if (preparedStatement != null) {
                        preparedStatement.close();
                    }
                    connection.close();
                }
            }
        }
    }