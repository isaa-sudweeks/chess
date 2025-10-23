package dataaccess;

import model.GameData;

import java.util.Map;

public interface GameDAO {
    Map<Integer, GameData> getGames();

    void addGame(GameData gameData);

    void updateGame(GameData gameData);

    void clear();
}
