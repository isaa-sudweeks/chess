package ui;

import chess.ChessGame;
import chess.ChessPiece;
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

    public String renderChessBoard(GameData gameData, String color, ChessPosition position) {
        var board = gameData.game().getBoard();
        List<Integer> movesRow = new ArrayList<>();
        List<Integer> movesCol = new ArrayList<>();
        try {
            if (position != null) {
                var moves = gameData.game().validMoves(position);
                for (var move : moves) {
                    movesRow.add(move.getEndPosition().getRow());
                    movesCol.add(move.getEndPosition().getColumn());
                }
            }
        } catch (NullPointerException e) {
            return SET_TEXT_COLOR_RED + "No piece at that position" + RESET_ALL_FORMATTING + "\n" + LOGGEDIN_HEADER;
        }

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
                String bgColor;
                bgColor = setBackgroundColor(position, rowValue, colValue, whiteSquare, movesRow, movesCol);
                String textColor = whiteSquare
                        ? SET_TEXT_COLOR_BLACK
                        : SET_TEXT_COLOR_WHITE;

                stringBuilder.append(bgColor).append(textColor);
                if (piece == null) {
                    stringBuilder.append(EMPTY);
                } else if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                    if (piece.getPieceType() == ChessPiece.PieceType.KING) {
                        stringBuilder.append(WHITE_KING);
                    } else if (piece.getPieceType() == ChessPiece.PieceType.QUEEN) {
                        stringBuilder.append(WHITE_QUEEN);
                    } else if (piece.getPieceType() == ChessPiece.PieceType.BISHOP) {
                        stringBuilder.append(WHITE_BISHOP);
                    } else if (piece.getPieceType() == ChessPiece.PieceType.KNIGHT) {
                        stringBuilder.append(WHITE_KNIGHT);
                    } else if (piece.getPieceType() == ChessPiece.PieceType.ROOK) {
                        stringBuilder.append(WHITE_ROOK);
                    } else {
                        stringBuilder.append(WHITE_PAWN);
                    }
                } else {
                    if (piece.getPieceType() == ChessPiece.PieceType.KING) {
                        stringBuilder.append(BLACK_KING);
                    } else if (piece.getPieceType() == ChessPiece.PieceType.QUEEN) {
                        stringBuilder.append(BLACK_QUEEN);
                    } else if (piece.getPieceType() == ChessPiece.PieceType.BISHOP) {
                        stringBuilder.append(BLACK_BISHOP);
                    } else if (piece.getPieceType() == ChessPiece.PieceType.KNIGHT) {
                        stringBuilder.append(BLACK_KNIGHT);
                    } else if (piece.getPieceType() == ChessPiece.PieceType.ROOK) {
                        stringBuilder.append(BLACK_ROOK);
                    } else {
                        stringBuilder.append(BLACK_PAWN);
                    }
                }
                stringBuilder.append(RESET_TEXT_COLOR);
                stringBuilder.append(RESET_BG_COLOR);
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
        return stringBuilder.toString();
    }

    private String setBackgroundColor(
            ChessPosition position,
            int rowValue,
            int colValue,
            boolean whiteSquare,
            List<Integer> movesRow,
            List<Integer> movesCol) {

        String bgColor = "";
        if (position != null && rowValue == position.getRow() && colValue == position.getColumn()) {
            bgColor = whiteSquare
                    ? SET_BG_COLOR_YELLOW
                    : SET_BG_COLOR_DARK_YELLOW;
        } else if (!movesCol.isEmpty()) {
            for (int i = 0; i < movesRow.toArray().length; i++) {
                if (position != null && movesRow.get(i) == rowValue && movesCol.get(i) == colValue) {
                    bgColor = whiteSquare
                            ? SET_BG_COLOR_GREEN
                            : SET_BG_COLOR_DARK_GREEN;
                    break;
                } else {
                    bgColor = whiteSquare
                            ? SET_BG_COLOR_WHITE
                            : SET_BG_COLOR_DARK_GREY;
                }
            }
        } else {
            bgColor = whiteSquare
                    ? SET_BG_COLOR_WHITE
                    : SET_BG_COLOR_DARK_GREY;
        }
        return bgColor;
    }


}
