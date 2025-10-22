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
    public AuthData getAuth(String authToken) {
        return auths.get(authToken);
    }

    @Override
    public void removeAuth(String authToken) {
        auths.remove(authToken);
    }

    @Override
    public void clear() {
        auths.clear();
    }
}
