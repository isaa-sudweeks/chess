package passoff.chess;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ChessBoardTests extends EqualsTestingUtility<ChessBoard> {
    public ChessBoardTests() {
        super("ChessBoard", "boards");
    }

    @Test
    @DisplayName("Construct Empty ChessBoard")
    public void constructChessBoard() {
        final ChessBoard board = new ChessBoard();

        for (int row = 1; 8 >= row; row++) {
            for (int col = 1; 8 >= col; col++) {
                Assertions.assertNull(
                        board.getPiece(new ChessPosition(row, col)),
                        "Immediately upon construction, a ChessBoard should be empty."
                );
            }
        }

    }

    @Test
    @DisplayName("Add and Get Piece")
    public void getAddPiece() {
        final ChessPosition position = new ChessPosition(4, 4);
        final ChessPiece piece = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);

        final var board = new ChessBoard();
        board.addPiece(position, piece);

        final ChessPiece foundPiece = board.getPiece(position);

        Assertions.assertNotNull(foundPiece, "getPiece returned null for a position just added");
        Assertions.assertEquals(piece.getPieceType(), foundPiece.getPieceType(),
                "ChessPiece returned by getPiece had the wrong piece type");
        Assertions.assertEquals(piece.getTeamColor(), foundPiece.getTeamColor(),
                "ChessPiece returned by getPiece had the wrong team color");
    }

    @Test
    @DisplayName("Reset Board")
    public void defaultGameBoard() {
        final var expectedBoard = TestUtilities.defaultBoard();

        final var actualBoard = new ChessBoard();
        actualBoard.resetBoard();

        Assertions.assertEquals(expectedBoard, actualBoard, "Reset board did not create the correct board");
    }

    @Override
    protected ChessBoard buildOriginal() {
        final var basicBoard = new ChessBoard();
        basicBoard.resetBoard();
        return basicBoard;
    }

    @Override
    protected Collection<ChessBoard> buildAllDifferent() {
        final List<ChessBoard> differentBoards = new ArrayList<>();

        differentBoards.add(new ChessBoard()); // An empty board

        final ChessPiece.PieceType[] pieceSchedule = {
                ChessPiece.PieceType.ROOK, ChessPiece.PieceType.KNIGHT,
                ChessPiece.PieceType.BISHOP, ChessPiece.PieceType.QUEEN,
                ChessPiece.PieceType.KING, ChessPiece.PieceType.PAWN,
                ChessPiece.PieceType.KING, ChessPiece.PieceType.ROOK,
        };

        // Generate boards each with one piece added from a static list.
        // The color is assigned in a mixed pattern.
        ChessPiece.PieceType type;
        boolean isWhite;
        for (int col = 1; 8 >= col; col++) {
            for (int row = 1; 8 >= row; row++) {
                type = pieceSchedule[row - 1];
                isWhite = 0 == (row + col) % 2;
                differentBoards.add(this.createBoardWithPiece(row, col, type, isWhite));
            }
        }

        return differentBoards;
    }

    private ChessBoard createBoardWithPiece(final int row, final int col, final ChessPiece.PieceType type, final boolean isWhite) {
        final var board = new ChessBoard();

        final var teamColor = isWhite ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;
        final var piece = new ChessPiece(teamColor, type);

        final var position = new ChessPosition(row, col);
        board.addPiece(position, piece);

        return board;
    }

}
