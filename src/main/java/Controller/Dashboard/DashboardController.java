package Controller.Dashboard;

import Model.Person.Administrator;
import Model.Person.ObjectCurrent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class DashboardController {

    @FXML
    private BorderPane bp;
    @FXML
    private Label Usernamelabel;

    public void initialize() {
        switchhomescene(null);
        Object currentUser = ObjectCurrent.getObjectCurrent();
        if (currentUser instanceof Administrator) {
            Administrator admin = (Administrator) currentUser;
            List<Administrator> adminList = Collections.singletonList(admin);
            String username = admin.getUsername();
            // Hiển thị username trong label
            Usernamelabel.setText(username);
            System.out.println(username);
        } else {
            // Xử lý trường hợp không có ai đăng nhập vào
            Usernamelabel.setText("N/A");
            System.err.println("Unexpected type for currentUser: " + currentUser.getClass().getName());
        }
    }

        @FXML
        public void switchhomescene(MouseEvent event) {
            try {
                AnchorPane homeview = FXMLLoader.load(getClass().getResource("/MainApplication/HomePage/HomePage.fxml"));
                bp.setCenter(homeview);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @FXML
        public void switchprofile(MouseEvent event) {
            try {
                AnchorPane profileview = FXMLLoader.load(getClass().getResource("/Controller/Profile/resource/Profile.fxml"));
                bp.setCenter(profileview);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    @FXML
    public void change_chat(MouseEvent event) {
        try {
            AnchorPane profileview = FXMLLoader.load(getClass().getResource("/Chat/Message.fxml"));
            bp.setCenter(profileview);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public void yourcartscene(MouseEvent event) {
            try {
                AnchorPane profileview = FXMLLoader.load(getClass().getResource("/MainApplication/HomePage/SellHomePage.fxml"));
                bp.setCenter(profileview);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
}
