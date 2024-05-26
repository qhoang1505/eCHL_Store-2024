module com.example.ss {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires java.sql;
    requires java.datatransfer;
    requires mysql.connector.java;
    requires java.mail;


    exports Controller.Profile to javafx.fxml, javafx.graphics;
    opens Controller.Profile to javafx.fxml;
    exports Controller.SaleHome to javafx.fxml, javafx.graphics;
    opens Controller.SaleHome to javafx.fxml;
    exports Controller.Dashboard to javafx.fxml, javafx.graphics;
    opens Controller.Dashboard to javafx.fxml;

    exports View to javafx.fxml, javafx.graphics;
    opens View to javafx.fxml;
    exports Controller.Login to javafx.fxml, javafx.graphics;
    opens Controller.Login to javafx.fxml;
    exports Model.Person to javafx.fxml, javafx.graphics;
    opens Model.Person to javafx.base, javafx.fxml;
    exports Model.Thing to javafx.fxml, javafx.graphics;
    opens Model.Thing to javafx.base, javafx.fxml;
    exports Controller.Database to javafx.fxml, javafx.graphics;
    opens Controller.Database to javafx.base, javafx.fxml;

    exports Controller.Chat;
    opens Controller.Chat to javafx.fxml;
}