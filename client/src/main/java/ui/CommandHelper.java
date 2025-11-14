package ui;

import ServerFacade.ServerFacade;
import chess.ChessGame;
import exception.ResponseException;
import model.*;

import static ui.EscapeSequences.*;
import static ui.OftenPrinted.*;

public class CommandHelper {
    private static final String DIVIDER_LINE = "-".repeat(36);
    private static final String LISTING_DIVIDER =
            SET_TEXT_COLOR_LIGHT_GREY +
                    DIVIDER_LINE +
                    RESET_ALL_FORMATTING +
                    "\n";
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
                if (parts.length != 2) {
                    return withHeader(warn("Command incorrect: " + command + " Proper usage is create <NAME>"), LOGGEDIN_HEADER);
                }
                serverFacade.createGame(new CreateGameRequest(this.authToken, parts[1]));
                return withHeader(success("You have created a new game called: " + parts[1]), LOGGEDIN_HEADER);
            } catch (ResponseException e) {
                return withHeader(error("There was an error: " + e.getMessage()), LOGGEDIN_HEADER);
            }
        } else if (command.equalsIgnoreCase("logout")) {
            try {
                serverFacade.logout(this.authToken);
                this.authToken = "";
                this.state = 0;
                return withHeader(success("The user has been logged out"), LOGGEDOUT_HEADER);
            } catch (ResponseException e) {
                return withHeader(error("There was an error: " + e.getMessage()), LOGGEDIN_HEADER);
            }
        } else if (command.equalsIgnoreCase("quit")) {
            return "exit";
        } else if (command.equalsIgnoreCase("list")) {
            return getAndListGames();
        } else if (command.contains("join")) {
            try {
                String[] parts = command.split(" ");
                if (parts.length != 3) {
                    return withHeader(warn("Command incorrect: " + command + " Proper usage is join <ID> [WHITE|BLACK]"), LOGGEDIN_HEADER);
                }
                return joinGame(parts[1], parts[2]);
            } catch (NumberFormatException e) {
                return withHeader(error("You must pass an integer ID"), LOGGEDIN_HEADER);
            }
        } else if (command.contains("observe")) {
            try {
                String[] parts = command.split(" ");
                if (parts.length != 2) {
                    return withHeader(warn("Command incorrect: " + command + " Proper usage is observe <ID>"), LOGGEDIN_HEADER);
                }
                return observe(parts[1]);
            } catch (NumberFormatException e) {
                return withHeader(error("You must pass an integer ID"), LOGGEDIN_HEADER);
            }
        } else {
            return withHeader(warn("The command " + command + " is unknown. Type help to get a list of commands."), LOGGEDIN_HEADER);
        }
    }

    private String observe(String num) {
        try {
            int i = Integer.parseInt(num);
            return op.renderChessBoard(getGameData(i), "white");
        } catch (ResponseException e) {
            return withHeader(error("There was an error: " + e.getMessage()), LOGGEDIN_HEADER);
        }
    }

    private GameData getGameData(int i) throws ResponseException {
        var games = serverFacade.listGames(this.authToken);
        var listOfGames = games.games();
        if (i > listOfGames.toArray().length || i <= 0) {
            throw new ResponseException(ResponseException.Code.ClientError, "There is no game " + i + " try again");
        }
        return listOfGames.get(i - 1);
    }

    private String joinGame(String num, String color) {
        try {
            int i = Integer.parseInt(num);
            int ID = getGameData(i).gameID();

            ChessGame.TeamColor pColor;
            if (color.equalsIgnoreCase("white")) {
                pColor = ChessGame.TeamColor.WHITE;
            } else if (color.equalsIgnoreCase("black")) {
                pColor = ChessGame.TeamColor.BLACK;
            } else {
                return withHeader(warn("The color " + color + " is unrecognized"), LOGGEDIN_HEADER);
            }
            serverFacade.joinGame(new JoinGameRequest(pColor, ID, this.authToken));
            return op.renderChessBoard(getGameData(i), color);

        } catch (ResponseException e) {
            return withHeader(error("There was an error: " + e.getMessage()), LOGGEDIN_HEADER);
        }
    }

    private String getAndListGames() {
        try {
            var games = serverFacade.listGames(this.authToken);
            StringBuilder sb = new StringBuilder();
            sb.append(SET_TEXT_COLOR_BLUE)
                    .append(SET_TEXT_BOLD)
                    .append("Available Games")
                    .append(RESET_ALL_FORMATTING)
                    .append("\n");
            sb.append(LISTING_DIVIDER);
            var list = games.games();
            if (list.isEmpty()) {
                sb.append(info("No games available. Use create <NAME> to start one."));
                sb.append("\n");
            } else {
                int num = 1;
                for (var game : list) {
                    sb.append(formatGameListing(num, game));
                    num++;
                }
            }
            sb.append(LOGGEDIN_HEADER);
            return sb.toString();
        } catch (ResponseException e) {
            return withHeader(error("There was an error: " + e.getMessage()), LOGGEDIN_HEADER);
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
            if (parts.length != 4) {
                return withHeader(warn("The proper way to use the command is register <USERNAME> <PASSWORD> <EMAIL>"), LOGGEDOUT_HEADER);
            }
            try {
                RegisterLoginResult result = serverFacade.register(new RegisterRequest(parts[1], parts[2], parts[3]));
                this.authToken = result.authToken();
                state = 1;
                return ERASE_SCREEN + success("\nWelcome to chess " + result.username()) + "\n" + LOGGEDIN_HEADER;
            } catch (ResponseException e) {
                return withHeader(error("There was an error: " + e.getMessage()), LOGGEDOUT_HEADER);
            }
        }
        if (command.contains("login")) {
            String[] parts = command.split(" ");
            if (parts.length != 3) {
                return withHeader(warn("The proper way to use the command is login <USERNAME> <PASSWORD>"), LOGGEDOUT_HEADER);
            }
            try {
                RegisterLoginResult result = serverFacade.login(new LoginRequest(parts[1], parts[2]));
                this.authToken = result.authToken();
                state = 1;
                return ERASE_SCREEN + success("\nWelcome to chess " + result.username()) + "\n" + LOGGEDIN_HEADER;
            } catch (ResponseException e) {
                return withHeader(error("There was an error: " + e.getMessage()), LOGGEDOUT_HEADER);
            }
        }
        return withHeader(warn("The command " + command + " was not recognized. Type help to get a list of commands."), LOGGEDOUT_HEADER);
    }

    private String formatGameListing(int number, GameData game) {
        StringBuilder sb = new StringBuilder();
        sb.append(SET_TEXT_COLOR_BLUE)
                .append(SET_TEXT_BOLD)
                .append("#")
                .append(number)
                .append(RESET_ALL_FORMATTING)
                .append(" ");
        sb.append(SET_TEXT_COLOR_YELLOW)
                .append(game.gameName() == null ? "Untitled Game" : game.gameName())
                .append(RESET_ALL_FORMATTING)
                .append("\n");
        sb.append(SET_TEXT_COLOR_LIGHT_GREY)
                .append("   White: ")
                .append(displayName(game.whiteUsername()))
                .append("  |  Black: ")
                .append(displayName(game.blackUsername()))
                .append("  |  ID: ")
                .append(game.gameID())
                .append(RESET_ALL_FORMATTING)
                .append("\n");
        sb.append(LISTING_DIVIDER);
        return sb.toString();
    }

    private String displayName(String name) {
        return name == null ? "-" : name;
    }

    private String success(String message) {
        return SET_TEXT_COLOR_GREEN + SET_TEXT_BOLD + message + RESET_ALL_FORMATTING;
    }

    private String warn(String message) {
        return SET_TEXT_COLOR_RED + message + RESET_ALL_FORMATTING;
    }

    private String error(String message) {
        return SET_TEXT_COLOR_RED + message + RESET_ALL_FORMATTING;
    }

    private String info(String message) {
        return SET_TEXT_COLOR_LIGHT_GREY + message + RESET_ALL_FORMATTING;
    }

    private String withHeader(String message, String header) {
        return message + "\n" + header;
    }
}
