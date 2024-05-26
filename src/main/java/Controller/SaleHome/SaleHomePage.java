package Controller.SaleHome;

import Controller.Database.test.DBController;
import Model.Person.Administrator;
import Model.Person.ObjectCurrent;
import Model.Thing.Product;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.sql.*;
import java.util.Optional;
import java.util.Properties;
import java.util.Random;
import java.util.ResourceBundle;

import static Controller.Database.test.DBController.getConnection;
import static Controller.Login.Encryption.encodePassword;

public class SaleHomePage implements Initializable {
    @FXML
    private Label namelabel;
    private Connection con;
    private PreparedStatement st;
    private ResultSet rs;
    ObservableList<Product> productdata;
    //Load tableview
    @FXML
    private TableColumn<Product, Integer> quantityCol;

    @FXML
    private TableColumn<Product, String> ramCol;

    @FXML
    private TableColumn<Product, Integer> priceCol;

    @FXML
    private TableColumn<Product, String> storageCol;
    @FXML
    private TableView<Product> productTable;
    @FXML
    private TableColumn<Product, String> categoryCol;

    @FXML
    private TableColumn<Product, String> cpuCol;

    @FXML
    private TableColumn<Product, String> displayCol;

    @FXML
    private TableColumn<Product, String> idCol;
    @FXML
    private TableColumn<Product, String> nameCol;
    @FXML
    private TableColumn<Product, String> userCol;
    @FXML
    private TableColumn<Product, Integer> quacol;
    private Stage stage;
    private Scene scene;

    public void switchhomescene(MouseEvent event) {
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("/MainApplication/HomePage/HomePage.fxml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String username = null;
        Object currentUser = ObjectCurrent.getObjectCurrent();
        if (currentUser instanceof Administrator) {
            Administrator admin = (Administrator) currentUser;
            username = admin.getUsername();

        }
        else {

        }
        try {
            con = DBController.getConnection();
            st = con.prepareStatement("Select * From cart");
            rs = st.executeQuery();
            productdata = FXCollections.observableArrayList();
            while (rs.next()) {
                if(username.equals(rs.getString(11))) {
                    productdata.add(new Product(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4),
                            rs.getString(5), rs.getString(6), rs.getString(7),
                            rs.getInt(8), rs.getInt(9), rs.getInt(10), rs.getString(11)));
                }
                setCellTable();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        productTable.setItems(productdata);
        try {
            sortBuyer();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private void setCellTable() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("ID"));
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("Category"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("Name"));
        cpuCol.setCellValueFactory(new PropertyValueFactory<>("CPU"));
        ramCol.setCellValueFactory(new PropertyValueFactory<>("Ram"));
        storageCol.setCellValueFactory(new PropertyValueFactory<>("Storage"));
        displayCol.setCellValueFactory(new PropertyValueFactory<>("Display"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("Price"));
        quacol.setCellValueFactory(new PropertyValueFactory<>("Quantity"));
    }

    @FXML
    private void payAction() throws SQLException {
        String username = null;
        Object currentUser = ObjectCurrent.getObjectCurrent();
        if (currentUser instanceof Administrator) {
            Administrator admin = (Administrator) currentUser;
            username = admin.getUsername();
        }
        int price = 0;
        con = DBController.getConnection();
        // Moi cap nhat them tinh nang
        st = con.prepareStatement("Select * From cart WHERE Seller = ?");
        st.setString(1, username);
        rs = st.executeQuery();
        productdata = FXCollections.observableArrayList();
        setCellTable();
        while (rs.next()) {
            price += rs.getInt(10) * rs.getInt(9);
        }

        Alert alertbuy = new Alert(Alert.AlertType.CONFIRMATION);
        alertbuy.setTitle("Buy product");
        alertbuy.setHeaderText("Do you want buy all product with " + price);
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
                } else {
                    // Neu gio hang ma ko co
                    Alert alertbuy6 = new Alert(Alert.AlertType.ERROR);
                    alertbuy6.setTitle("Buy product");
                    alertbuy6.setHeaderText("No products found in the cart for the specific seller");
                    alertbuy6.showAndWait();
                }
            } catch (SQLException e) {
                // Xử lý ngoại lệ SQL một cách tốt đẹp hơn, ví dụ, hiển thị thông báo lỗi cho người dùng
                e.printStackTrace();
            }
        }
    }

    public void purchase(MouseEvent event) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("/MainApplication/HomePage/OTP_Mail.fxml"));
        Scene scene = new Scene(parent);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.initStyle(StageStyle.UTILITY);
        stage.show();
    }


    ObservableList<Product> productlist;
    public void sortBuyer() throws SQLException {
        String username = null;
        Object currentUser = ObjectCurrent.getObjectCurrent();
        if (currentUser instanceof Administrator) {
            Administrator admin = (Administrator) currentUser;
            username = admin.getUsername();
        }
        idCol.setCellValueFactory(new PropertyValueFactory<>("ID"));
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("Category"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("Name"));
        cpuCol.setCellValueFactory(new PropertyValueFactory<>("CPU"));
        ramCol.setCellValueFactory(new PropertyValueFactory<>("Ram"));
        storageCol.setCellValueFactory(new PropertyValueFactory<>("Storage"));
        displayCol.setCellValueFactory(new PropertyValueFactory<>("Display"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("Price"));
        userCol.setCellValueFactory(new PropertyValueFactory<>("Seller"));
        try {
            con = DBController.getConnection();
            PreparedStatement st = con.prepareStatement("Select * From cart");
            rs = st.executeQuery();
            productlist = FXCollections.observableArrayList();
            setCellTable();
            while (rs.next()) {
                if(username.equals(rs.getString(11))) {
                    productlist.add(new Product(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4),
                            rs.getString(5), rs.getString(6), rs.getString(7),
                            rs.getInt(8), rs.getInt(9), rs.getInt(10), rs.getString(11)));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        productTable.setItems(productdata);
    }

    @FXML
    public void Remove(MouseEvent event) {
        Product selectedProduct = productTable.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            delete(selectedProduct);
            Alert alertSuccess = new Alert(Alert.AlertType.CONFIRMATION);
            alertSuccess.setTitle("Delete");
            alertSuccess.setHeaderText("Detele product successful!");
            alertSuccess.showAndWait();

            refresh2();
        }
        else {
            Alert alertSuccess = new Alert(Alert.AlertType.ERROR);
            alertSuccess.setTitle("Delete");
            alertSuccess.setHeaderText("Please choose product you want to delete!");
            alertSuccess.showAndWait();

            refresh2();
        }
    }

    //Delete one product
    public void delete(Product product) {
        try {
            con = DBController.getConnection();
            String query = "DELETE FROM cart WHERE ID = ? LIMIT 1";
            st = con.prepareStatement(query);

            // Assuming product.getID() returns the ID value as a String
            st.setString(1, product.getID());

            // Execute the delete operation
            int rowsAffected = st.executeUpdate();

            if (rowsAffected > 0) {
                setCellTable();
            } else {
                // If no rows were affected, display an error message
                Alert alertSuccess = new Alert(Alert.AlertType.ERROR);
                alertSuccess.setTitle("Delete");
                alertSuccess.setHeaderText("No matching product found for ID: " + product.getID());
                alertSuccess.showAndWait();
            }
        } catch (SQLException e) {
            // Handle SQL exceptions more gracefully, e.g., show an error message to the user
            e.printStackTrace();
            Alert alertSuccess = new Alert(Alert.AlertType.ERROR);
            alertSuccess.setTitle("Delete");
            alertSuccess.setHeaderText("Delete error, check it again!");
            alertSuccess.showAndWait();
        }
    }

    public void refresh2() {
        String username = null;
        Object currentUser = ObjectCurrent.getObjectCurrent();
        if (currentUser instanceof Administrator) {
            Administrator admin = (Administrator) currentUser;
            username = admin.getUsername();
        }
        try {
            productdata.clear();
            con = DBController.getConnection();
            st = con.prepareStatement("Select * From cart");
            rs = st.executeQuery();
            while (rs.next()) {
                if (username.equals(rs.getString(11))) {
                    productdata.add(new Product(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4),
                            rs.getString(5), rs.getString(6), rs.getString(7),
                            rs.getInt(8), rs.getInt(9), rs.getInt(10), rs.getString(11)));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        productTable.setItems(productdata);
    }
}
