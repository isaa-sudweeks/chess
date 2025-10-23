package dataaccess;

import model.UserData;

import java.util.HashMap;
import java.util.Map;

public class MemoryUserDAO implements UserDAO {
    private final Map<String, UserData> users = new HashMap<>();

    @Override
    public UserData getUser(final String username) {

        return this.users.get(username);
    }

    @Override
    public void addUser(final UserData userData) {
        this.users.put(userData.username(), userData);
    }

    @Override
    public void clear() {
        this.users.clear();
    }
}
