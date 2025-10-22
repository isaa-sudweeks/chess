package dataaccess;

import model.AuthData;

public interface AuthDAO {
    void addAuth(AuthData authData);
    AuthData getAuth(String authToken);
    AuthData removeAuth(String authToken);
    void clear();
}
