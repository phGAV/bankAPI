package impl;

import java.sql.SQLException;
import java.util.List;

public interface Dao<T> {

    T get(long id) throws SQLException;

    List<T> getAll() throws SQLException;

    void add(T t) throws SQLException;

    void update(T t, String[] params) throws SQLException;

    void delete(T t) throws SQLException;
}
