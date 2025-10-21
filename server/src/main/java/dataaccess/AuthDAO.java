package dataaccess;

import model.AuthData;

public interface AuthDAO {
    void addAuth(AuthData authData);
    AuthData getAuth(AuthData authData);
    void removeAuth(AuthData authData);
    void clear();
}
