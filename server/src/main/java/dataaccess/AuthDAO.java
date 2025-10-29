package dataaccess;

import model.AuthData;

import java.sql.SQLException;

public interface AuthDAO {
    void addAuth(AuthData authData) throws SQLException, DataAccessException;

    AuthData getAuth(String authToken) throws DataAccessException, SQLException;

    AuthData removeAuth(String authToken) throws SQLException, DataAccessException;

    void clear() throws SQLException, DataAccessException;
}
