package Controller.Database.test;

import java.sql.Connection;
import java.sql.SQLException;

public class DBControllerTest {
    public static void main(String[] args) {
        // Tạo kết nối đến cơ sở dữ liệu
        Connection connection = DBController.getConnection();

        // In thông tin kết nối
        try {
            if (connection != null) {
                DBController.printInfo(connection);
            } else {
                System.out.println("Không thể kết nối đến cơ sở dữ liệu.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Đóng kết nối
            DBController.closeConnection(connection);
        }
    }
}
