package dataaccess;

import model.UserData;

import java.sql.Connection;
import java.sql.SQLException;

public class DBUserDAO implements UserDAO {
    public DBUserDAO() throws SQLException, DataAccessException {
        configureDatabase();
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return null;
    }

    @Override
    public void addUser(UserData userData) throws DataAccessException {

    }

    @Override
    public void clear() {

    }

    private void configureDatabase() throws DataAccessException, SQLException {
        DatabaseManager.createDatabase();

        try (Connection conn = DatabaseManager.getConnection()) {
            String[] createStatements = { //TODO: Write the DB creation string here
                    """
                    
                    """
            };
            for (String statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }
}

