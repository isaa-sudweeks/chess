package dataaccess;

import model.UserData;

import java.util.HashMap;
import java.util.Map;

//TODO: Consider changing this so that it returns null if a user is not found
public class MemoryUserDAO implements UserDAO {
    private Map<String,UserData> users = new HashMap<>();
    @Override
    public UserData getUser(String username) throws DataAccessException {

        UserData user = users.get(username);
        if (user == null){
            throw new DataAccessException("User Not Found");
        }
        else{
            return user;
        }
    }


    @Override
    public void addUser(UserData userData) throws DataAccessException {
        try {
            getUser(userData.username());
            throw new DataAccessException("The user already exists");
        }
        catch (DataAccessException e){
            users.put(userData.username(), userData);
        }
    }

    @Override
    public void clear() {
        users.clear();
    }
}
