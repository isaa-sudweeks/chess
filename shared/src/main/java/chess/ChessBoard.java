package chess;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    //private List<List<Object>> pieces = new ArrayList<>();
    private final Map<ChessPosition, ChessPiece> pieces = new HashMap<>();

    /**
     * Creates a fresh chessboard
     */
    public ChessBoard() {
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(final ChessPosition position, final ChessPiece piece) {
        this.pieces.put(position, piece);


    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(final ChessPosition position) {
        return this.pieces.get(position);
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        this.pieces.clear();
        //Start with white side
        //First Rank
        final ChessPiece rookW1 = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
        this.addPiece(new ChessPosition(1, 1), rookW1);

        final ChessPiece bishopW1 = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
        this.addPiece(new ChessPosition(1, 3), bishopW1);

        final ChessPiece knightW1 = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
        this.addPiece(new ChessPosition(1, 2), knightW1);

        final ChessPiece queenW = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN);
        this.addPiece(new ChessPosition(1, 4), queenW);

        final ChessPiece kingW = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING);
        this.addPiece(new ChessPosition(1, 5), kingW);

        final ChessPiece knightW2 = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
        this.addPiece(new ChessPosition(1, 7), knightW2);

        final ChessPiece bishopW2 = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
        this.addPiece(new ChessPosition(1, 6), bishopW2);

        final ChessPiece rookW2 = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
        this.addPiece(new ChessPosition(1, 8), rookW2);

        //Pawns
        for (int i = 1; 8 >= i; i++) {
            this.addPiece(new ChessPosition(2, i), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
        }

        //Black Side

        final ChessPiece rookB1 = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
        this.addPiece(new ChessPosition(8, 1), rookB1);

        final ChessPiece bishopB1 = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
        this.addPiece(new ChessPosition(8, 3), bishopB1);

        final ChessPiece knightB1 = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
        this.addPiece(new ChessPosition(8, 2), knightB1);

        final ChessPiece queenB = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN);
        this.addPiece(new ChessPosition(8, 4), queenB);

        final ChessPiece kingB = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING);
        this.addPiece(new ChessPosition(8, 5), kingB);

        final ChessPiece knightB2 = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
        this.addPiece(new ChessPosition(8, 7), knightB2);

        final ChessPiece bishopB2 = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
        this.addPiece(new ChessPosition(8, 6), bishopB2);

        final ChessPiece rookB2 = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
        this.addPiece(new ChessPosition(8, 8), rookB2);

        //Pawns
        for (int i = 1; 8 >= i; i++) {
            this.addPiece(new ChessPosition(7, i), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (null == o || this.getClass() != o.getClass()) {
            return false;
        }
        final ChessBoard that = (ChessBoard) o;
        return Objects.equals(this.pieces, that.pieces);
    }


    @Override
    public int hashCode() {
        return Objects.hashCode(this.pieces);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("|");
        for (int row = 1; 8 >= row; row++) {
            for (int col = 1; 8 >= col; col++) {
                final ChessPiece piece = this.getPiece(new ChessPosition(row, col));
                if (null == piece) {
                    sb.append("  ");
                } else if (ChessPiece.PieceType.KING == piece.getPieceType()) {
                    sb.append("K ");
                } else if (ChessPiece.PieceType.KNIGHT == piece.getPieceType()) {
                    sb.append("Kn");
                } else if (ChessPiece.PieceType.PAWN == piece.getPieceType()) {
                    sb.append("P ");
                } else if (ChessPiece.PieceType.BISHOP == piece.getPieceType()) {
                    sb.append("B ");
                } else if (ChessPiece.PieceType.QUEEN == piece.getPieceType()) {
                    sb.append("Q ");
                } else if (ChessPiece.PieceType.ROOK == piece.getPieceType()) {
                    sb.append("R ");
                }
                sb.append("|");
            }
            sb.append("\n|");
        }
        //sb.append(pieces);
        return sb.toString();
    }
}