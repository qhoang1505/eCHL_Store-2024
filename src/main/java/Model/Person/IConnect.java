package Model.Person;

import java.sql.SQLException;
import java.util.ArrayList;

public interface IConnect<O> {
    //Them thong tin
    public int insert(O o) throws RuntimeException, SQLException;

    //Cap nhat thong tin
    public int update(O o) throws SQLException;
    //Xoa thong tin
    public int detele(O o) throws SQLException;
    //tao mang chua administrator
    public ArrayList<O> selectAll();
}
