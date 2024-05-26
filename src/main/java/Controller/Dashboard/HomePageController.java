package Controller.Dashboard;

import Controller.Database.test.DBController;
import Model.Person.Administrator;
import Model.Person.ObjectCurrent;
import Model.Thing.Product;
import javafx.beans.property.Property;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableCell;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import javafx.scene.control.TableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.ByteArrayInputStream;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class HomePageController implements Initializable {
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
    //Search (tim kiem)
    @FXML
    private TextField searchtext;

    @FXML
    private TableColumn<Product, Integer> quantityCol;

    @FXML
    private TableColumn<Product, String> ramCol;

    @FXML
    private TableColumn<Product, Double> selledCol;

    @FXML
    private TableColumn<Product, String> storageCol;

    @FXML
    private TableColumn<Product, Integer> yearCol;
    @FXML
    private TableColumn<Product, String> sellerCol;
    @FXML
    private TableColumn<Product, Image> imageCol;
    private Connection con;
    private PreparedStatement st;
    private ResultSet rs;
    ObservableList<Product> productdata;
    ObservableList<Product> productlist;

    public Image convertByteArrayToImage(byte[] imageData) {
        ByteArrayInputStream bis = new ByteArrayInputStream(imageData);
        return new Image(bis);
    }

    // Trung tam xu li tableview
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setCellTable();
         // Kiểm tra xem cột đã được ánh xạ hay chưa


        try {
            con = DBController.getConnection();
            // Delete products where Quantity is 0
            String deleteQuery = "DELETE FROM product WHERE Quantity <= 0";
            st = con.prepareStatement(deleteQuery);
            int rowsAffected = st.executeUpdate();

            if (rowsAffected > 0) {
                setCellTable();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try {
            con = DBController.getConnection();
            st = con.prepareStatement("Select * From product");
            rs = st.executeQuery();
            productdata = FXCollections.observableArrayList();

            while (rs.next()) {
                productdata.add(new Product(
                        rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4),
                        rs.getString(5), rs.getString(6), rs.getString(7),
                        rs.getInt(8), rs.getInt(9), rs.getInt(10), rs.getString(11),
                        rs.getBytes(12) // Assuming the image is the 12th column
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        productTable.setItems(productdata);
        search(null);
    }

    private void setCellTable() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("ID"));
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("Category"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("Name"));
        cpuCol.setCellValueFactory(new PropertyValueFactory<>("CPU"));
        ramCol.setCellValueFactory(new PropertyValueFactory<>("Ram"));
        storageCol.setCellValueFactory(new PropertyValueFactory<>("Storage"));
        displayCol.setCellValueFactory(new PropertyValueFactory<>("Display"));
        yearCol.setCellValueFactory(new PropertyValueFactory<>("Year"));
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("Quantity"));
        selledCol.setCellValueFactory(new PropertyValueFactory<>("Price"));
        sellerCol.setCellValueFactory(new PropertyValueFactory<>("Seller"));
    }


        // Trung tam xu li su kien va chuc nang o homepage
          // 1. Chuyen Canh
        public void addnewproduct(MouseEvent event) throws IOException {
            Parent parent = FXMLLoader.load(getClass().getResource("/MainApplication/HomePage/Addnewproduct.fxml"));
            Scene scene = new Scene(parent);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.initStyle(StageStyle.UTILITY);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(((Node) event.getSource()).getScene().getWindow());
            stage.show();
        }


    public void switcheditproductscene(MouseEvent event) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("/MainApplication/HomePage/Editproduct.fxml"));
        Scene scene = new Scene(parent);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.initStyle(StageStyle.UTILITY);
        stage.show();
    }

          // 2. Chuc nang trong dashboard
    public void Remove(MouseEvent event) {
        Product selectedProduct = productTable.getSelectionModel().getSelectedItem();
        String adminUsername = null;
        String username = null;
        Object currentUser = ObjectCurrent.getObjectCurrent();
        if (currentUser instanceof Administrator) {
            Administrator admin = (Administrator) currentUser;
            username = admin.getUsername();
            adminUsername = selectedProduct.getSeller();
        }
        if ((selectedProduct != null) && (username.equals(adminUsername))) {
            delete(selectedProduct);
            Alert alertSuccess = new Alert(Alert.AlertType.INFORMATION);
            alertSuccess.setTitle("Delete");
            alertSuccess.setHeaderText("Detele product successful!");
            alertSuccess.showAndWait();

            refresh2();
        }
        else {
            Alert alertSuccess = new Alert(Alert.AlertType.ERROR);
            alertSuccess.setTitle("Delete");
            alertSuccess.setHeaderText("Please choose your product want to delete!");
            alertSuccess.showAndWait();

            refresh2();
        }
    }

    public void search(MouseEvent event) {
        try {
            con = DBController.getConnection();
            st = con.prepareStatement("Select * From product");
            rs = st.executeQuery();
            productlist = FXCollections.observableArrayList();
            setCellTable();

            while (rs.next()) {
                productlist.add(new Product(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4),
                        rs.getString(5), rs.getString(6), rs.getString(7),
                        rs.getInt(8), rs.getInt(9), rs.getInt(10), rs.getString(11)));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        productTable.setItems(productlist);
        FilteredList<Product> filteredList = new FilteredList<>(productlist, b -> true);
        searchtext.textProperty().addListener((observableValue, s, t1) -> {
            filteredList.setPredicate(product -> { // dat dieu kien de tim kiem san pham
                if (t1 == null || t1.isEmpty()) {
                    return true;
                }
                String lowerCaseSearch = t1.toLowerCase();

                if(product.getID().toLowerCase().indexOf(lowerCaseSearch) != -1) {
                    return true;
                }
                else if(product.getCPU().toLowerCase().indexOf(lowerCaseSearch) != -1) {
                    return true;
                }
                else if(product.getName().toLowerCase().contains(lowerCaseSearch)) {
                    return true;
                }
                else if(product.getRam().toLowerCase().indexOf(lowerCaseSearch) != -1) {
                    return true;
                }
                else if(product.getCategory().toLowerCase().indexOf(lowerCaseSearch) != -1) {
                    return true;
                }
                else if (String.valueOf(product.getPrice()).toLowerCase().contains(lowerCaseSearch)) {
                    return true;
                }
                else if (String.valueOf(product.getQuantity()).toLowerCase().contains(lowerCaseSearch)) {
                    return true;
                }
                else if (String.valueOf(product.getYear()).toLowerCase().contains(lowerCaseSearch)) {
                    return true;
                }
                else if (String.valueOf(product.getSeller()).toLowerCase().contains(lowerCaseSearch)) {
                    return true;
                }
                else {
                    return false;
                }
            });
        });
        SortedList<Product> sortedList = new SortedList<>(filteredList);
        //Thay doi thuoc tinh sau do anh xa lam phai thay doi theo
        sortedList.comparatorProperty().bind(productTable.comparatorProperty());
        productTable.setItems(sortedList); // Gia tri sau khi bi thay doi
    }

    //Lam moi chủ động
    public void refresh(MouseEvent event) {
        try {
            productdata.clear();
            con = DBController.getConnection();
            st = con.prepareStatement("Select * From product");
            rs = st.executeQuery();
            while (rs.next()) {
                productdata.add(new Product(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4),
                        rs.getString(5), rs.getString(6), rs.getString(7),
                        rs.getInt(8), rs.getInt(9), rs.getInt(10), rs.getString(11), rs.getByte(12)));

                productTable.setItems(productdata);
            }
            Alert alertSuccess = new Alert(Alert.AlertType.CONFIRMATION);
            alertSuccess.setTitle("Refresh");
            alertSuccess.setHeaderText("Refreshed!");
            alertSuccess.showAndWait();

        } catch (SQLException e) {
            Alert alertFail = new Alert(Alert.AlertType.ERROR);
            alertFail.setTitle("Refresh");
            alertFail.setHeaderText("Don't refreshed!");
            alertFail.showAndWait();
        }
    }
    //Làm mới bị động (tự động)
    public void refresh2() {
        try {
            productdata.clear();
            con = DBController.getConnection();
            st = con.prepareStatement("Select * From product");
            rs = st.executeQuery();
            while (rs.next()) {
                productdata.add(new Product(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4),
                        rs.getString(5), rs.getString(6), rs.getString(7),
                        rs.getInt(8), rs.getInt(9), rs.getInt(10), rs.getString(11), rs.getByte(12)));

                productTable.setItems(productdata);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Xoa
    public void delete(Product product) {
        try {
            Product selectedProduct = productTable.getSelectionModel().getSelectedItem();
            if (selectedProduct == null) {
                Alert alertSuccess = new Alert(Alert.AlertType.ERROR);
                alertSuccess.setTitle("Delete");
                alertSuccess.setHeaderText("No product selected");
                alertSuccess.setContentText("Please select a product to delete.");
                alertSuccess.showAndWait();
                return;
            }
            Object currentUser = ObjectCurrent.getObjectCurrent(); // lay gia tri ma hoi nay gan vô ở login
            if (currentUser instanceof Administrator) {
                Administrator admin = (Administrator) currentUser;
                String username = admin.getUsername();
                String adminUsername = selectedProduct.getSeller();

                if (username.equals(adminUsername)) {
                    con = DBController.getConnection();
                    String query = "DELETE FROM product WHERE ID = ?";
                    st = con.prepareStatement(query);
                    st.setString(1, selectedProduct.getID());
                    int rowsAffected = st.executeUpdate();
                    if (rowsAffected > 0) {
                        setCellTable();
                    } else {
                        Alert alertError = new Alert(Alert.AlertType.ERROR);
                        alertError.setTitle("Delete");
                        alertError.setHeaderText("Delete error, check it again!");
                        alertError.showAndWait();
                    }
                }

                if (!username.equals(adminUsername)) {
                    Alert alertError = new Alert(Alert.AlertType.ERROR);
                    alertError.setTitle("Unvalid Operation");
                    alertError.setHeaderText("As a buyer, you are ineligible to delete products that do not belong to you.");
                    alertError.showAndWait();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private int cartQuantity = 1;
    public void addtocart() {
        Product selectedProduct = productTable.getSelectionModel().getSelectedItem();
        ResultSet rs1;
        Object currentUser = ObjectCurrent.getObjectCurrent();
        String adminUsername;
        try {
            con = DBController.getConnection();
            st = con.prepareStatement("SELECT * FROM product");
            rs1 = st.executeQuery();
            productdata = FXCollections.observableArrayList();

            while (rs1.next()) {
                if (currentUser instanceof Administrator) {
                    Administrator admin = (Administrator) currentUser;
                    String username = admin.getUsername();
                    adminUsername = selectedProduct.getSeller();

                    if (username.equals(adminUsername)) {
                        Alert alertError = new Alert(Alert.AlertType.ERROR);
                        alertError.setTitle("Add to cart");
                        alertError.setHeaderText("Invalid Operation");
                        alertError.setContentText("Unfortunately, as the proprietor of this product, you are ineligible to make a purchase.");
                        alertError.showAndWait();
                        break;
                    }

                    else if (!username.equals(adminUsername)) {
                        if (selectedProduct != null) {
                            inserttocart(selectedProduct, cartQuantity);
                            Alert alertSuccess = new Alert(Alert.AlertType.INFORMATION);
                            alertSuccess.setTitle("Add to cart");
                            alertSuccess.setHeaderText("Add product successful!");
                            alertSuccess.showAndWait();
                            cartQuantity++;
                            break;
                        } else {
                            Alert alertError = new Alert(Alert.AlertType.ERROR);
                            alertError.setTitle("Add to cart");
                            alertError.setHeaderText("No Product Selected");
                            alertError.setContentText("Please choose the product you want to add!");
                            alertError.showAndWait();
                        }
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        catch (NullPointerException e) {
            Alert alertError = new Alert(Alert.AlertType.ERROR);
            alertError.setTitle("Add to cart");
            alertError.setHeaderText("No Product Selected");
            alertError.setContentText("Please choose the product you want to add!");
            alertError.showAndWait();
        }
    }

    // Xu li xu kien mua hang
    public void inserttocart(Product product, int quantity) throws SQLException {
        String username = null;
        Object currentUser = ObjectCurrent.getObjectCurrent();
        if (currentUser instanceof Administrator) {
            Administrator admin = (Administrator) currentUser;
            username = admin.getUsername();
        }
        Connection con = DBController.getConnection();
        // Tao doi tuong statement
        Statement st = con.createStatement();
        // thuc thi
        String sqlCheck = "SELECT * FROM cart WHERE ID = '" +product.getID()+ "' AND Seller = '" +username+ "'";
        rs= st.executeQuery(sqlCheck);

        if (rs.next()) {
            int newQuantity = 0;
            int existingQuantity = rs.getInt(9);
            if (existingQuantity <= 0) {
                delete(product);
            }
            else if (existingQuantity > 0){
                newQuantity = existingQuantity + 1;
            }
            String sqlUpdate = "UPDATE cart SET Quantity = " + newQuantity + " WHERE ID = '" + product.getID() +
                    "' AND Seller = '" + username + "'";
            st.executeUpdate(sqlUpdate);

        } else {
            //San pham khong trung ID thi them vo dong moi
            String sql = "INSERT INTO cart (ID, Category, name, CPU, Ram, Storage, Display, Quantity, Price, Seller) VALUES (" +
                    "'" + product.getID() + "', " +
                    "'" + product.getCategory() + "', " +
                    "'" + product.getName() + "', " +
                    "'" + product.getCPU() + "', " +
                    "'" + product.getRam() + "', " +
                    "'" + product.getStorage() + "', " +
                    "'" + product.getDisplay() + "', " +
                    "'" + quantity + "', " +
                    "'" + product.getPrice() + "', '" + username + "')";

            st.executeUpdate(sql);
        }
        // Close result set and statement
        rs.close();
        st.close();
    }


}




