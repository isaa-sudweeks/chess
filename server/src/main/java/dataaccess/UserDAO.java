package dataaccess;

import model.UserData;

import java.sql.SQLException;

public interface UserDAO {
    UserData getUser(String username) throws DataAccessException, SQLException;

    void addUser(UserData userData) throws DataAccessException;

    void clear();
}
