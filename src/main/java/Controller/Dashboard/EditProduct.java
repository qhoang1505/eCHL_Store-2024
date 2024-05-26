package Controller.Dashboard;

import Controller.Database.test.DBController;
import Model.Person.Administrator;
import Model.Person.ObjectCurrent;
import Model.Thing.Product;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.sql.*;

public class EditProduct {
    @FXML
    private TextField cputext;

    @FXML
    private TextField displaytext;

    @FXML
    private TextField idtext;

    @FXML
    private TextField quantitytext; // int

    @FXML
    private TextField ramtext;

    @FXML
    private TextField storagetext;
    @FXML
    private Button editbutton;
    @FXML
    private TextField pricetext;

    @FXML
    private TextField nametext;
    private Connection con;
    private PreparedStatement st;
    private ResultSet rs;

    public void EditproductAction(ActionEvent event) throws SQLException {
        con = DBController.getConnection();
        st = con.prepareStatement("Select * From product");
        rs = st.executeQuery();

        //Lay gia tri thuc tu truong nhap lieu ----------------------------------------------
        try {
            String id = idtext.getText();
            String name = nametext.getText();
            String cpu = cputext.getText();
            String ram = ramtext.getText();
            String storage = storagetext.getText();
            String display = displaytext.getText();
            int quantity = Integer.parseInt(quantitytext.getText());
            String buttonsave = editbutton.getText();
            int price = Integer.parseInt(pricetext.getText());
            //---------------------------------------------------------------------------------

            if (buttonsave.equals("Save")) {
                String username = null;
                Object currentUser = ObjectCurrent.getObjectCurrent();
                if (currentUser instanceof Administrator) {
                    Administrator admin = (Administrator) currentUser;
                    username = admin.getUsername();
                }

                if (id.equals("") || name.equals("") || cpu.equals("") || ram.equals("") || storage.equals("") || display.equals("") || String.valueOf(quantity).equals("") || String.valueOf(price).equals("")) {
                    Alert alertFail = new Alert(Alert.AlertType.ERROR);
                    alertFail.setTitle("Add a new product");
                    alertFail.setHeaderText("Please, fill in all the information for your product!");
                    alertFail.showAndWait();
                }
                try {
                    while (rs.next()) {
                        String seller = rs.getString(11);
                        if (id.equals(rs.getString(1)) && username.equals(seller)) {
                            Product product = new Product(id, name, cpu, ram, storage, display, quantity, price, username);
                            try {
                                update(product);
                            } catch (SQLException e) {
                                e.printStackTrace();
                                Alert alertFail = new Alert(Alert.AlertType.ERROR);
                                alertFail.setTitle("Add a new product");
                                alertFail.setHeaderText("Added a product is: Failed, please try again");
                                alertFail.showAndWait();
                            }
                            Alert alertSuccess = new Alert(Alert.AlertType.CONFIRMATION);
                            alertSuccess.setTitle("Add a new product");
                            alertSuccess.setHeaderText("Added a product is: Successful!");
                            alertSuccess.showAndWait();
                            return;
                        }
                    }
                    Alert alertFail = new Alert(Alert.AlertType.ERROR);
                    alertFail.setTitle("Add a new product");
                    alertFail.setHeaderText("This product's id is not your product, please try again!");
                    alertFail.showAndWait();


                } catch (SQLException e) {
                    e.printStackTrace();

                }
            }
        }
        catch (NumberFormatException e) {
            Alert alertFail = new Alert(Alert.AlertType.ERROR);
            alertFail.setTitle("Add a new product");
            alertFail.setHeaderText("Please, fill information for your product!");
            alertFail.showAndWait();
        }
    }

    public void update(Product product) throws SQLException {
        try (Connection con = DBController.getConnection();
             PreparedStatement preparedStatement = con.prepareStatement(
                     "UPDATE product SET " +
                             "Name = ?, " +
                             "CPU = ?, " +
                             "Ram = ?, " +
                             "Storage = ?, " +
                             "Display = ?, " +
                             "Quantity = ?, " +
                             "Price = ? " +
                             "WHERE ID = ?")) {

            preparedStatement.setString(1, product.getName());
            preparedStatement.setString(2, product.getCPU());
            preparedStatement.setString(3, product.getRam());
            preparedStatement.setString(4, product.getStorage());
            preparedStatement.setString(5, product.getDisplay());
            preparedStatement.setInt(6, product.getQuantity());
            preparedStatement.setInt(7, product.getPrice());
            preparedStatement.setString(8, product.getID());

            preparedStatement.executeUpdate();

            // Add success handling if needed

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
