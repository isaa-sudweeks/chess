package dataaccess;

import model.UserData;

import java.util.HashMap;
import java.util.Map;

public class MemoryUserDAO implements UserDAO {
    private Map<String,UserData> users = new HashMap<>();
    @Override
    public UserData getUser(String username) {

        return users.get(username);
    }

    @Override
    public void addUser(UserData userData){
            users.put(userData.username(), userData);
    }

    @Override
    public void clear() {
        users.clear();
    }
}
