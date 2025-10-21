package dataaccess;

import model.AuthData;
import java.util.Map;
import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO{
    private Map<String, AuthData> auths = new HashMap<>();
    @Override
    public void addAuth(AuthData authData) {
        auths.put(authData.authToken(), authData);
    }

    @Override
    public AuthData getAuth(AuthData authData) {
        return auths.get(authData.authToken());
    }

    @Override
    public void removeAuth(AuthData authData) {
        auths.remove(authData.authToken());
    }

    @Override
    public void clear() {
        auths.clear();
    }
}
