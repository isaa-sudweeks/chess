package dataaccess;

import model.UserData;

public interface UserDAO {
    UserData getUser(String username) throws DataAccessException;
    void addUser(UserData userData) throws  DataAccessException;
    void clear();
}
