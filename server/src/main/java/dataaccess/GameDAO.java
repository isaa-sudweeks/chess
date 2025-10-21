package dataaccess;

import model.GameData;

import java.util.List;

public interface GameDAO {
    List<GameData> listGames();
    void addGame(GameData gameData);
    void updateGame(GameData gameData);
    void clear();
}
