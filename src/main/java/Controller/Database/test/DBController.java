package Controller.Database.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBController {

    //Tạo kết nối đến db
    public static Connection getConnection () {
        Connection a = null;

        try {
            // Đăng ký MySQL driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            String url = "jdbc:mysql://116.110.215.57/echl_store"; // Sửa cổng từ 3306 thành 1511
            String username = "echl_store_database";
            String password = "echl_store_database@2024";

            // Tạo kết nối
            a = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return a;
    }

    // Ngắt kết nối với db
    public static void closeConnection(Connection c) {
        try {
            if (c != null) {
                c.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void printInfo(Connection c) throws SQLException {
        if (c != null) {
            System.out.println(c.getMetaData().toString());
        }
    }
}
