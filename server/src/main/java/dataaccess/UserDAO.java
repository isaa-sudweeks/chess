package dataaccess;

import model.UserData;

public interface UserDAO {
    UserData getUser(String username);
    void addUser(UserData userData);
    void clear();
}
