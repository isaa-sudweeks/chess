package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import exception.ResponseException;
import model.*;
import serverfacade.ServerFacade;
import websocket.NotificationHandler;
import websocket.WebSocketFacade;
import websocket.messages.ServerMessage;

import java.util.Scanner;

import static ui.EscapeSequences.*;
import static ui.OftenPrinted.*;

public class CommandHelper implements NotificationHandler {
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
    private WebSocketFacade webSocketFacade;

    private ChessGame.TeamColor color;
    private GameData gameData;

    public CommandHelper(ServerFacade serverFacade, String serverURL) throws ResponseException {
        this.serverFacade = serverFacade;
        this.webSocketFacade = new WebSocketFacade(serverURL, this);
    }

    public String commandHelper(String command) {
        switch (state) {
            case 0:
                return preLogin(command);
            case 1:
                return postLogin(command);
            case 2:
                return resign(command);
            case 3:
                return gamePlayUI(command);
            default:
                return "";
        }
    }

    private String gamePlayUI(String command) {
        if (command.equalsIgnoreCase("help")) {
            return withHeader(GAMEPLAYUIHELP, LOGGEDIN_HEADER);
        } else if (command.equalsIgnoreCase("redraw chess board")) {
            return withHeader(op.renderChessBoard(gameData, this.color.name(), null), LOGGEDIN_HEADER);

        } else if (command.equalsIgnoreCase("leave")) {
            try {
                webSocketFacade.leaveGame(this.authToken, gameData.gameID());
                state = 1;
                return withHeader("", LOGGEDIN_HEADER);
            } catch (ResponseException e) {
                return withHeader(error("There was an error: " + e.getMessage()), LOGGEDIN_HEADER);
            }
        } else if (command.contains("make move")) {
            try {
                String[] parts = command.split(" ");
                if (parts.length != 4) {
                    return withHeader(
                            warn(
                                    "Command incorrect " +
                                            command +
                                            " Proper usage is make move <Start Location> <End Location>"),
                            LOGGEDIN_HEADER);
                } else {
                    String from = parts[2].toLowerCase();
                    String to = parts[3].toLowerCase();

                    if (!validSquare(from) || !validSquare(to)) {
                        return withHeader(
                                warn("Invalid square(s). Use algebraic like e2 e4."),
                                LOGGEDIN_HEADER);
                    }

                    int fromCol = from.charAt(0) - 'a' + 1;
                    int fromRow = from.charAt(1) - '0';

                    int toCol = to.charAt(0) - 'a' + 1;
                    int toRow = to.charAt(1) - '0';
                    ChessPiece.PieceType promotion = null;
                    try {
                        promotion = getPromotion(fromRow, fromCol, toRow, promotion);
                    } catch (NullPointerException e) {
                        return withHeader(error("There is no valid piece there"), LOGGEDIN_HEADER);
                    }


                    ChessMove move =
                            new ChessMove(
                                    new ChessPosition(fromRow, fromCol),
                                    new ChessPosition(toRow, toCol), promotion);

                    webSocketFacade.makeMove(this.authToken, gameData, move);
                    return withHeader("", LOGGEDIN_HEADER);

                }


            } catch (ResponseException e) {
                return withHeader(error("There was an error: " + e.getMessage()), LOGGEDIN_HEADER);
            }
        } else if (command.equalsIgnoreCase("resign")) {
            state = 2;
            return withHeader("are you sure you want to resign", LOGGEDIN_HEADER);

        } else if (command.contains("highlight legal moves")) {
            String[] parts = command.split(" ");
            if (parts.length != 4) {
                return withHeader(
                        warn(
                                "Command incorrect " +
                                        command +
                                        " Proper usage is make move <Start Location>"),
                        LOGGEDIN_HEADER);
            }
            String from = parts[3].toLowerCase();
            if (!validSquare(from)) {
                return withHeader(
                        warn("Invalid square(s). Use algebraic like e2 e4."),
                        LOGGEDIN_HEADER);
            }
            int fromCol = from.charAt(0) - 'a' + 1;
            int fromRow = from.charAt(1) - '0';


            return withHeader(op.renderChessBoard(this.gameData, this.color.name(), new ChessPosition(fromRow, fromCol)), LOGGEDIN_HEADER);
        } else {
            return withHeader(warn("Command: " + command + "Was not recognized"), LOGGEDIN_HEADER);
        }
    }

    private ChessPiece.PieceType getPromotion(int fromRow, int fromCol, int toRow, ChessPiece.PieceType promotion) {
        var piece = gameData.game().getBoard().getPiece(new ChessPosition(fromRow, fromCol));
        if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
            if (color.equals(ChessGame.TeamColor.WHITE)) {
                if (toRow == 8) {
                    System.out.print(withHeader("What would you like to promote to?", LOGGEDIN_HEADER));

                    promotion = promote();
                }
            }
            if (color.equals(ChessGame.TeamColor.BLACK)) {
                if (toRow == 1) {
                    System.out.print(withHeader("What would you like to promote to?", LOGGEDIN_HEADER));
                    promotion = promote();
                }
            }
        }
        return promotion;
    }

    private ChessPiece.PieceType promote() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String command = scanner.nextLine();
            if (command.equalsIgnoreCase("pawn")) {
                return ChessPiece.PieceType.PAWN;
            } else if (command.equalsIgnoreCase("rook")) {
                return ChessPiece.PieceType.ROOK;
            } else if (command.equalsIgnoreCase("bishop")) {
                return ChessPiece.PieceType.BISHOP;
            } else if (command.equalsIgnoreCase("queen")) {
                return ChessPiece.PieceType.QUEEN;
            } else if (command.equalsIgnoreCase("knight")) {
                return ChessPiece.PieceType.KNIGHT;
            }
        }
    }

    private String resign(String command) {
        if (command.equalsIgnoreCase("yes")) {
            try {
                state = 3;
                webSocketFacade.resign(this.authToken, this.gameData.gameID());
                return withHeader(ERASE_SCREEN, LOGGEDIN_HEADER);
            } catch (ResponseException e) {
                return withHeader(error("There was an error: " + e.getMessage()), LOGGEDIN_HEADER);
            }
        } else if (command.equalsIgnoreCase("no")) {
            state = 3;
            return withHeader("Resignation cancelled", LOGGEDIN_HEADER);
        } else {
            return warn(withHeader(command + " was not reconized must be a yes or no", LOGGEDIN_HEADER));
        }

    }

    private boolean validSquare(String s) {
        return s.length() == 2 &&
                Character.isLetter(s.charAt(0)) &&
                Character.isDigit(s.charAt(1)) &&
                s.charAt(0) >= 'a' && s.charAt(0) <= 'h' &&
                s.charAt(1) >= '1' && s.charAt(1) <= '8';

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
            webSocketFacade.observeGame(this.authToken, getGameData(i).gameID());
            state = 3;
            this.color = ChessGame.TeamColor.WHITE;
            return ERASE_SCREEN;
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
            int id = getGameData(i).gameID();

            ChessGame.TeamColor pColor;
            if (color.equalsIgnoreCase("white")) {
                pColor = ChessGame.TeamColor.WHITE;
            } else if (color.equalsIgnoreCase("black")) {
                pColor = ChessGame.TeamColor.BLACK;
            } else {
                return withHeader(warn("The color " + color + " is unrecognized"), LOGGEDIN_HEADER);
            }
            serverFacade.joinGame(new JoinGameRequest(pColor, id, this.authToken));
            webSocketFacade.joinGame(this.authToken, id, color);
            this.gameData = getGameData(i);
            this.color = pColor;
            state = 3;
            return gamePlayUI("redraw chess board");

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

    @Override
    public void notify(ServerMessage message) {
        if (message.getServerMessageType() == ServerMessage.ServerMessageType.NOTIFICATION) {
            System.out.print(withHeader(ERASE_LINE + info("\n" + message.getMessage()), LOGGEDIN_HEADER));
        }
        if (message.getServerMessageType() == ServerMessage.ServerMessageType.LOAD_GAME) {
            this.gameData = message.getGame();
            System.out.print(commandHelper("redraw chess board"));
        }
        if (message.getServerMessageType() == ServerMessage.ServerMessageType.ERROR) {
            System.out.print(withHeader(ERASE_LINE + error(message.getErrorMessage()), LOGGEDIN_HEADER));
        }
    }
}
