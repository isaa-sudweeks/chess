package dataaccess;

import model.GameData;
import model.UserData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemoryGameDAO implements GameDAO{
    private Map<String, GameData> games = new HashMap<>();
    @Override
    public Map<String, GameData> getGames() {
        return games;
    }

    @Override
    public void addGame(GameData gameData) { //Consider checking to see if the name has been used for.
         games.put(gameData.gameName(),gameData);
    }

    @Override
    public void updateGame(GameData gameData) {
        games.replace(gameData.gameName(), gameData);
    }

    @Override
    public void clear() {
        games.clear();
    }
}
