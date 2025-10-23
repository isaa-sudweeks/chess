package dataaccess;

import model.GameData;

import java.util.List;
import java.util.Map;

public interface GameDAO {
    Map<Integer, GameData> getGames();
    void addGame(GameData gameData);
    void updateGame(GameData gameData);
    void clear();
}
