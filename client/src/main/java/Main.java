import chess.ChessGame;
import chess.ChessPiece;

public class Main {
    public static void main(final String[] args) {
        final var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
    }
}