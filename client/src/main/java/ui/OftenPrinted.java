package ui;

import chess.ChessPosition;
import model.GameData;

import java.util.ArrayList;
import java.util.List;

import static ui.EscapeSequences.*;

public class OftenPrinted {
    private static final String INFO_COLOR = SET_TEXT_COLOR_LIGHT_GREY;
    private static final String ACCENT_COLOR = SET_TEXT_COLOR_BLUE;
    public static final String LOGGEDOUT_HEADER = buildPrompt("LOGGED_OUT", ACCENT_COLOR);
    private static final String SUCCESS_COLOR = SET_TEXT_COLOR_GREEN;
    public static final String LOGGEDIN_HEADER = buildPrompt("LOGGED_IN", SUCCESS_COLOR);
    private static final String COMMAND_COLOR = SET_TEXT_COLOR_MAGENTA;
    private static final String BOARD_EDGE_COLOR = SET_TEXT_COLOR_DARK_GREY;
    public static final String PRELOGINHELP = buildPreloginHelp();
    public static final String LOGINHELP = buildLoginHelp();
    public static final String GAMEPLAYUIHELP = buildGamePlayUIHelp();


    private static String buildGamePlayUIHelp() {
        var sb = new StringBuilder();
        sb.append(sectionTitle("Commands (Gameplay UI)")).append("\n");
        sb.append(divider());
        sb.append(bullet("help", "List possible commands"));
        sb.append(bullet("redraw chess board", "Redraws the chess board"));
        sb.append(bullet("leave", "Leave the game"));
        sb.append(bullet("make move <Start Location> <End Location>", "Make a move"));
        sb.append(bullet("resign", "Resign the game"));
        sb.append(bullet("highlight legal move <Piece Location>",
                "Highlights the legal moves for the selected piece"));
        return sb.toString().stripTrailing();
    }

    private static String buildPreloginHelp() {
        var sb = new StringBuilder();
        sb.append(sectionTitle("Commands (Not Logged In)")).append("\n");
        sb.append(divider());
        sb.append(bullet("help", "List possible commands"));
        sb.append(bullet("quit", "Exit the program"));
        sb.append(bullet("login <username> <password>", "Login to play chess"));
        sb.append(bullet("register <username> <password> <email>", "Create a new account"));
        return sb.toString().stripTrailing();
    }

    private static String buildLoginHelp() {
        var sb = new StringBuilder();
        sb.append(sectionTitle("Commands (Logged In)")).append("\n");
        sb.append(divider());
        sb.append(bullet("help", "List possible commands"));
        sb.append(bullet("logout", "Logout of chess"));
        sb.append(bullet("create <NAME>", "Create a game with the given name"));
        sb.append(bullet("list", "List games you can join"));
        sb.append(bullet("join <ID> [WHITE|BLACK]", "Join game <ID> on a team"));
        sb.append(bullet("observe <ID>", "Watch game <ID>"));
        sb.append(bullet("quit", "Quit the program"));
        return sb.toString().stripTrailing();
    }

    private static String buildPrompt(String label, String color) {
        return color +
                SET_TEXT_BOLD +
                "[" +
                label +
                "]" +
                RESET_ALL_FORMATTING +
                " " +
                INFO_COLOR +
                ">>> " +
                RESET_ALL_FORMATTING;
    }

    private static String bullet(String command, String description) {
        return "  " +
                commandChip(command) +
                INFO_COLOR +
                " - " +
                description +
                RESET_ALL_FORMATTING +
                "\n";
    }

    private static String sectionTitle(String title) {
        return ACCENT_COLOR +
                SET_TEXT_BOLD +
                title +
                RESET_ALL_FORMATTING;
    }

    private static String divider() {
        return BOARD_EDGE_COLOR +
                "-".repeat(36) +
                RESET_ALL_FORMATTING +
                "\n";
    }

    private static String commandChip(String command) {
        return COMMAND_COLOR +
                SET_TEXT_BOLD +
                command +
                RESET_ALL_FORMATTING;
    }

    private static String infoLine(String text) {
        return INFO_COLOR +
                text +
                RESET_ALL_FORMATTING +
                "\n";
    }

    private static String defaultName(String name) {
        return name == null ? "-" : name;
    }

    public String highlightLegalMoves(GameData gameData, String color, ChessPosition position) {
        var board = gameData.game().getBoard();
        var moves = gameData.game().validMoves(position);
        boolean whitePerspective = !("black".equalsIgnoreCase(color));
        List<Integer> movesRow = new ArrayList<>();
        List<Integer> movesCol = new ArrayList<>();

        for (var move : moves) {
            movesRow.add(move.getEndPosition().getRow());
            movesCol.add(move.getEndPosition().getColumn());
        }

        int[] rows = new int[8];
        int[] cols = new int[8];
        for (int i = 0; i < 8; i++) {
            rows[i] = whitePerspective ? 8 - i : 1 + i;
            cols[i] = whitePerspective ? 1 + i : 8 - i;
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(ERASE_SCREEN);
        stringBuilder.append(sectionTitle("Game: " + (gameData.gameName() == null ? "Untitled" : gameData.gameName())));
        stringBuilder.append("\n");
        stringBuilder.append(infoLine("White: " + defaultName(gameData.whiteUsername()) +
                "  |  Black: " + defaultName(gameData.blackUsername()) +
                "  |  Perspective: " + (whitePerspective ? "White" : "Black")));
        stringBuilder.append(divider());
        stringBuilder.append(INFO_COLOR).append("   ");
        for (int col : cols) {
            char colLabel = (char) ('A' + col - 1);
            stringBuilder.append(INFO_COLOR).append(" ").append(colLabel).append(" ").append(RESET_ALL_FORMATTING);
        }
        stringBuilder.append(RESET_ALL_FORMATTING).append("\n");

        for (int rowValue : rows) {
            stringBuilder.append(String.format(" %d ", rowValue));
            for (int colValue : cols) {
                var piece = board.getPiece(new ChessPosition(rowValue, colValue));
                boolean whiteSquare = (rowValue + colValue) % 2 == 1;
                String bgColor;
                if (rowValue == position.getRow() && colValue == position.getColumn()) {
                    bgColor = whiteSquare
                            ? SET_BG_COLOR_YELLOW
                            : SET_BG_COLOR_DARK_YELLOW;
                } else if (movesRow.contains(rowValue) && movesCol.contains(colValue)) {
                    bgColor = whiteSquare
                            ? SET_BG_COLOR_GREEN
                            : SET_BG_COLOR_DARK_GREEN;
                } else {
                    bgColor = whiteSquare
                            ? EscapeSequences.SET_BG_COLOR_WHITE
                            : EscapeSequences.SET_BG_COLOR_DARK_GREY;
                }
                String textColor = whiteSquare
                        ? EscapeSequences.SET_TEXT_COLOR_BLACK
                        : EscapeSequences.SET_TEXT_COLOR_WHITE;

                stringBuilder.append(bgColor).append(textColor);
                if (piece == null) {
                    stringBuilder.append(EscapeSequences.EMPTY);
                } else if (piece.getTeamColor() == chess.ChessGame.TeamColor.WHITE) {
                    if (piece.getPieceType() == chess.ChessPiece.PieceType.KING) {
                        stringBuilder.append(EscapeSequences.WHITE_KING);
                    } else if (piece.getPieceType() == chess.ChessPiece.PieceType.QUEEN) {
                        stringBuilder.append(EscapeSequences.WHITE_QUEEN);
                    } else if (piece.getPieceType() == chess.ChessPiece.PieceType.BISHOP) {
                        stringBuilder.append(EscapeSequences.WHITE_BISHOP);
                    } else if (piece.getPieceType() == chess.ChessPiece.PieceType.KNIGHT) {
                        stringBuilder.append(EscapeSequences.WHITE_KNIGHT);
                    } else if (piece.getPieceType() == chess.ChessPiece.PieceType.ROOK) {
                        stringBuilder.append(EscapeSequences.WHITE_ROOK);
                    } else {
                        stringBuilder.append(EscapeSequences.WHITE_PAWN);
                    }
                } else {
                    if (piece.getPieceType() == chess.ChessPiece.PieceType.KING) {
                        stringBuilder.append(EscapeSequences.BLACK_KING);
                    } else if (piece.getPieceType() == chess.ChessPiece.PieceType.QUEEN) {
                        stringBuilder.append(EscapeSequences.BLACK_QUEEN);
                    } else if (piece.getPieceType() == chess.ChessPiece.PieceType.BISHOP) {
                        stringBuilder.append(EscapeSequences.BLACK_BISHOP);
                    } else if (piece.getPieceType() == chess.ChessPiece.PieceType.KNIGHT) {
                        stringBuilder.append(EscapeSequences.BLACK_KNIGHT);
                    } else if (piece.getPieceType() == chess.ChessPiece.PieceType.ROOK) {
                        stringBuilder.append(EscapeSequences.BLACK_ROOK);
                    } else {
                        stringBuilder.append(EscapeSequences.BLACK_PAWN);
                    }
                }
                stringBuilder.append(EscapeSequences.RESET_TEXT_COLOR);
                stringBuilder.append(EscapeSequences.RESET_BG_COLOR);
            }
            stringBuilder.append(String.format(" %d ", rowValue)).append("\n");
        }

        stringBuilder.append(INFO_COLOR).append("   ");
        for (int col : cols) {
            char colLabel = (char) ('A' + col - 1);
            stringBuilder.append(INFO_COLOR).append(" ").append(colLabel).append(" ").append(RESET_ALL_FORMATTING);
        }
        stringBuilder.append(RESET_ALL_FORMATTING).append("\n");
        stringBuilder.append(divider());
        stringBuilder.append(infoLine("You can still issue commands - type help to see them all."));
        stringBuilder.append(LOGGEDIN_HEADER);

        return stringBuilder.toString();

    }

    public String renderChessBoard(GameData gameData, String color) {
        if (gameData == null || gameData.game() == null) {
            return "No game available\n";
        }

        var board = gameData.game().getBoard();
        boolean whitePerspective = !("black".equalsIgnoreCase(color));
        int[] rows = new int[8];
        int[] cols = new int[8];
        for (int i = 0; i < 8; i++) {
            rows[i] = whitePerspective ? 8 - i : 1 + i;
            cols[i] = whitePerspective ? 1 + i : 8 - i;
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(ERASE_SCREEN);
        stringBuilder.append(sectionTitle("Game: " + (gameData.gameName() == null ? "Untitled" : gameData.gameName())));
        stringBuilder.append("\n");
        stringBuilder.append(infoLine("White: " + defaultName(gameData.whiteUsername()) +
                "  |  Black: " + defaultName(gameData.blackUsername()) +
                "  |  Perspective: " + (whitePerspective ? "White" : "Black")));
        stringBuilder.append(divider());

        stringBuilder.append(INFO_COLOR).append("   ");
        for (int col : cols) {
            char colLabel = (char) ('A' + col - 1);
            stringBuilder.append(INFO_COLOR).append(" ").append(colLabel).append(" ").append(RESET_ALL_FORMATTING);
        }
        stringBuilder.append(RESET_ALL_FORMATTING).append("\n");

        for (int rowValue : rows) {
            stringBuilder.append(String.format(" %d ", rowValue));
            for (int colValue : cols) {
                var piece = board.getPiece(new ChessPosition(rowValue, colValue));
                boolean whiteSquare = (rowValue + colValue) % 2 == 1;
                String bgColor = whiteSquare
                        ? EscapeSequences.SET_BG_COLOR_WHITE
                        : EscapeSequences.SET_BG_COLOR_DARK_GREY;
                String textColor = whiteSquare
                        ? EscapeSequences.SET_TEXT_COLOR_BLACK
                        : EscapeSequences.SET_TEXT_COLOR_WHITE;

                stringBuilder.append(bgColor).append(textColor);
                if (piece == null) {
                    stringBuilder.append(EscapeSequences.EMPTY);
                } else if (piece.getTeamColor() == chess.ChessGame.TeamColor.WHITE) {
                    if (piece.getPieceType() == chess.ChessPiece.PieceType.KING) {
                        stringBuilder.append(EscapeSequences.WHITE_KING);
                    } else if (piece.getPieceType() == chess.ChessPiece.PieceType.QUEEN) {
                        stringBuilder.append(EscapeSequences.WHITE_QUEEN);
                    } else if (piece.getPieceType() == chess.ChessPiece.PieceType.BISHOP) {
                        stringBuilder.append(EscapeSequences.WHITE_BISHOP);
                    } else if (piece.getPieceType() == chess.ChessPiece.PieceType.KNIGHT) {
                        stringBuilder.append(EscapeSequences.WHITE_KNIGHT);
                    } else if (piece.getPieceType() == chess.ChessPiece.PieceType.ROOK) {
                        stringBuilder.append(EscapeSequences.WHITE_ROOK);
                    } else {
                        stringBuilder.append(EscapeSequences.WHITE_PAWN);
                    }
                } else {
                    if (piece.getPieceType() == chess.ChessPiece.PieceType.KING) {
                        stringBuilder.append(EscapeSequences.BLACK_KING);
                    } else if (piece.getPieceType() == chess.ChessPiece.PieceType.QUEEN) {
                        stringBuilder.append(EscapeSequences.BLACK_QUEEN);
                    } else if (piece.getPieceType() == chess.ChessPiece.PieceType.BISHOP) {
                        stringBuilder.append(EscapeSequences.BLACK_BISHOP);
                    } else if (piece.getPieceType() == chess.ChessPiece.PieceType.KNIGHT) {
                        stringBuilder.append(EscapeSequences.BLACK_KNIGHT);
                    } else if (piece.getPieceType() == chess.ChessPiece.PieceType.ROOK) {
                        stringBuilder.append(EscapeSequences.BLACK_ROOK);
                    } else {
                        stringBuilder.append(EscapeSequences.BLACK_PAWN);
                    }
                }
                stringBuilder.append(EscapeSequences.RESET_TEXT_COLOR);
                stringBuilder.append(EscapeSequences.RESET_BG_COLOR);
            }
            stringBuilder.append(String.format(" %d ", rowValue)).append("\n");
        }

        stringBuilder.append(INFO_COLOR).append("   ");
        for (int col : cols) {
            char colLabel = (char) ('A' + col - 1);
            stringBuilder.append(INFO_COLOR).append(" ").append(colLabel).append(" ").append(RESET_ALL_FORMATTING);
        }
        stringBuilder.append(RESET_ALL_FORMATTING).append("\n");
        stringBuilder.append(divider());
        stringBuilder.append(infoLine("You can still issue commands - type help to see them all."));
        stringBuilder.append(LOGGEDIN_HEADER);

        return stringBuilder.toString();
    }
}
