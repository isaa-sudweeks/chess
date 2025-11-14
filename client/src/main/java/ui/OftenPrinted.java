package ui;

import chess.ChessPosition;
import model.GameData;

import static ui.EscapeSequences.ERASE_SCREEN;

public class OftenPrinted {
    public static String PRELOGINHELP =
            "help - List possible commands\n" +
                    "quit - Exits the program\n" +
                    "login <username> <password> - Login to play chess\n" +
                    "register <username> <password> <email> - to register for an account";

    public static String LOGINHELP =
            "help - List possible commands\n" +
                    "logout - logout of chess\n" +
                    "create <NAME> - Create a game with the given name\n" +
                    "list - list the games\n" +
                    "join <ID> [WHITE|BLACK] - join game <ID> on a certain side\n" +
                    "observe <ID> - Watch game <ID>\n" +
                    "quit - quit the program";
    public static String LOGGEDOUT_HEADER = "[LOGGED_OUT]>>> ";
    public static String LOGGEDIN_HEADER = "[LOGGED_IN]>>> ";

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
        stringBuilder.append("\n");

        stringBuilder.append("   ");
        for (int col : cols) {
            stringBuilder.append(String.format(" %d ", col));
        }
        stringBuilder.append("\n");

        for (int rowValue : rows) {
            char rowLabel = (char) ('A' + rowValue - 1);
            stringBuilder.append(" ").append(rowLabel).append(" ");
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
            stringBuilder.append(" ").append(rowLabel).append("\n");
        }

        stringBuilder.append("   ");
        for (int col : cols) {
            stringBuilder.append(String.format(" %d ", col));
        }
        stringBuilder.append("\n");

        return stringBuilder.toString();
    }
}
