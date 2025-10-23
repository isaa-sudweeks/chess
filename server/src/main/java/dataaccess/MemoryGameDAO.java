package dataaccess;

import model.GameData;

import java.util.HashMap;
import java.util.Map;

public class MemoryGameDAO implements GameDAO {
    private final Map<Integer, GameData> games = new HashMap<>();

    @Override
    public Map<Integer, GameData> getGames() {
        return this.games;
    }

    @Override
    public void addGame(final GameData gameData) { //Consider checking to see if the name has been used for.
        this.games.put(gameData.gameID(), gameData);
    }

    @Override
    public void updateGame(final GameData gameData) {
        this.games.replace(gameData.gameID(), gameData);
    }

    @Override
    public void clear() {
        this.games.clear();
    }
}
