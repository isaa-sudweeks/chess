package ui;

import ServerFacade.ServerFacade;
import chess.ChessGame;
import exception.ResponseException;
import model.*;

import static ui.EscapeSequences.ERASE_SCREEN;
import static ui.OftenPrinted.*;

public class CommandHelper {
    private ServerFacade serverFacade;
    private int state = 0;
    private String authToken;
    private OftenPrinted op = new OftenPrinted();

    public CommandHelper(ServerFacade serverFacade) {
        this.serverFacade = serverFacade;
    }

    public String commandHelper(String command) {
        switch (state) {
            case 0:
                return preLogin(command);
            case 1:
                return postLogin(command);
            default:
                return "";
        }
    }

    private String postLogin(String command) {
        if (command.equalsIgnoreCase("help")) {
            return LOGINHELP + "\n" + LOGGEDIN_HEADER;
        } else if (command.contains("create")) {
            try {
                String[] parts = command.split(" ");
                if (parts.length < 2) {
                    return "Command incorrect: " + command + " Proper usage is create <ID>\n" + LOGGEDIN_HEADER;
                }
                var result = serverFacade.createGame(new CreateGameRequest(this.authToken, parts[1]));
                return "You have created a new game called: " + parts[1] + "\n" + LOGGEDIN_HEADER;
            } catch (ResponseException e) {
                return "There was an error: " + e.getMessage() + "\n" + LOGGEDIN_HEADER;
            }
        } else if (command.equalsIgnoreCase("logout")) {
            try {
                serverFacade.logout(this.authToken);
                this.authToken = "";
                this.state = 0;
                return "The user has been logged out" + "\n" + LOGGEDOUT_HEADER;
            } catch (ResponseException e) {
                return "There was an error: " + e.getMessage() + "\n" + LOGGEDIN_HEADER;
            }
        } else if (command.equalsIgnoreCase("quit")) {
            return "exit";
        } else if (command.equalsIgnoreCase("list")) {
            return getAndListGames();
        } else if (command.contains("join")) {
            String[] parts = command.split(" ");
            if (parts.length != 3) {
                return "Command incorrect: " + command + " Proper usage is join <ID> [WHITE|BLACK]\n" + LOGGEDIN_HEADER;
            }
            return joinGame(parts[1], parts[2]);
        } else {
            return "The command " + command + " is unknown type help to get a list of commands\n" + LOGGEDIN_HEADER;
        }
    }

    private String joinGame(String num, String color) {
        try {
            int i = Integer.parseInt(num);
            var games = serverFacade.listGames(this.authToken);
            var listOfGames = games.games();
            int ID = listOfGames.get(i + 1).gameID();
            ChessGame.TeamColor pColor;
            if (color.equalsIgnoreCase("white")) {
                pColor = ChessGame.TeamColor.WHITE;
            } else if (color.equalsIgnoreCase("black")) {
                pColor = ChessGame.TeamColor.BLACK;
            } else {
                return "The color " + color + " is unrecognized\n" + LOGGEDIN_HEADER;
            }
            serverFacade.joinGame(new JoinGameRequest(pColor, ID, this.authToken));
            var gamesUpdated = serverFacade.listGames(this.authToken);
            var listOfUpdatedGames = gamesUpdated.games();
            return op.renderChessBoard(listOfUpdatedGames.get(i + 1).game());

        } catch (ResponseException e) {
            return "There was an error: " + e.getMessage() + "\n" + LOGGEDIN_HEADER;
        }
    }

    private String getAndListGames() {
        try {
            var games = serverFacade.listGames(this.authToken);
            int num = 1;
            StringBuilder sb = new StringBuilder();
            for (var game : games.games()) {
                sb.append(num);
                sb.append(" - Game Name: ");
                sb.append(game.gameName());
                sb.append(", White Username: ");
                sb.append(game.whiteUsername());
                sb.append(", Black Username: ");
                sb.append(game.blackUsername());
                sb.append("\n");
                num++;
            }
            sb.append(LOGGEDIN_HEADER);
            return sb.toString();
        } catch (ResponseException e) {
            return "There was an error: " + e.getMessage() + "\n" + LOGGEDIN_HEADER;
        }
    }

    private String preLogin(String command) {
        if (command.equalsIgnoreCase("help")) {
            return PRELOGINHELP + "\n" + LOGGEDOUT_HEADER;
        }
        if (command.equalsIgnoreCase("quit")) {
            return "exit";
        }
        if (command.contains("register")) {
            String[] parts = command.split(" ");
            try {
                RegisterLoginResult result = serverFacade.register(new RegisterRequest(parts[1], parts[2], parts[3]));
                this.authToken = result.authToken();
                state = 1;
                return ERASE_SCREEN + "\n Welcome to chess " + result.username() + "\n" + LOGGEDIN_HEADER;
            } catch (ResponseException e) {
                return "There was an error: " + e.getMessage() + "\n" + LOGGEDOUT_HEADER;
            }
        }
        if (command.contains("login")) {
            String[] parts = command.split(" ");
            try {
                RegisterLoginResult result = serverFacade.login(new LoginRequest(parts[1], parts[2]));
                this.authToken = result.authToken();
                state = 1;
                return ERASE_SCREEN + "\n Welcome to chess " + result.username() + "\n" + LOGGEDIN_HEADER;
            } catch (ResponseException e) {
                return "There was an error: " + e.getMessage() + "\n" + LOGGEDOUT_HEADER;
            }
        }
        return "The command " + command + " was not recognized type help to get a list of commands\n" + LOGGEDOUT_HEADER;
    }
}
