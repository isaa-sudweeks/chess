package dataaccess;

import model.AuthData;

import java.util.HashMap;
import java.util.Map;

public class MemoryAuthDAO implements AuthDAO {
    private final Map<String, AuthData> auths = new HashMap<>();

    @Override
    public void addAuth(final AuthData authData) {
        this.auths.put(authData.authToken(), authData);
    }

    @Override
    public AuthData getAuth(final String authToken) {
        return this.auths.get(authToken);
    }

    @Override
    public AuthData removeAuth(final String authToken) {
        return this.auths.remove(authToken);
    }

    @Override
    public void clear() {
        this.auths.clear();
    }
}
