package Controller.Database;

import Controller.Database.test.DBController;
import Model.Person.Administrator;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class AdministratorConnnect implements IConnect<Administrator> {
    int check;
    public static AdministratorConnnect getInstance() {
        return new AdministratorConnnect();
    }
    @Override
    public int insert(Administrator admin) throws SQLException {
        Connection con = DBController.getConnection();

        // Tao doi tuong statement
        Statement st = con.createStatement();

        // thuc thi
        String sql = "INSERT INTO administrator(username, password)" + "VALUES ('"+admin.getUsername()+"', '"+admin.getPassword()+"')";

        check = st.executeUpdate(sql);
        System.out.println("Da thuc thi" + sql);
        System.out.println("Có " + check + "dong bi thay doi!");
        return 0;
    }

    @Override
    public int update(Administrator o) throws SQLException {
        return 0;
    }

    @Override
    public int detele(Administrator o) throws SQLException {
        return 0;
    }

    @Override
    public ArrayList<Administrator> selectAll() {
        ArrayList<Administrator> result = new ArrayList<Administrator>();
        Connection con = DBController.getConnection();

        // Tao doi tuong statement
        try {
            Statement st = con.createStatement();
            String sql = "SELECT * FROM administrator";
            ResultSet rs;
            rs = st.executeQuery(sql);

            while (rs.next()) {
                String username = rs.getString("username");
                String password = rs.getString("PASSWORD");
                Administrator admin = new Administrator(username, password);
                result.add(admin);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return result;
    }
}
