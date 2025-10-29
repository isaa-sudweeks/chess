package dataaccess;

import model.GameData;

import java.sql.SQLException;
import java.util.Map;

public interface GameDAO {
    Map<Integer, GameData> getGames() throws DataAccessException, SQLException;

    void addGame(GameData gameData) throws SQLException, DataAccessException;

    void updateGame(GameData gameData) throws SQLException, DataAccessException;

    void clear() throws SQLException, DataAccessException;
}
