package View;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class SmartShop extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(SmartShop.class.getResource("/LoginScreen/Login.fxml"));

        Scene scenelogin = new Scene(fxmlLoader.load(), 1117, 638);
        stage.setTitle("eCHL Store");
        stage.setScene(scenelogin);
        stage.show();
        stage.setResizable(false);
    }
}
