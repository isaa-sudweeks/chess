package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    ChessGame.TeamColor pieceColor;
    ChessPiece.PieceType type;

    public ChessPiece(final ChessGame.TeamColor pieceColor, final ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof final ChessPiece that)) {
            return false;
        }
        return this.pieceColor == that.pieceColor && this.type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.pieceColor, this.type);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        if (ChessGame.TeamColor.WHITE == pieceColor) {
            sb.append("(W)");
        } else if (ChessGame.TeamColor.BLACK == pieceColor) {
            sb.append("(B)");
        }
        if (PieceType.BISHOP == this.getPieceType()) {
            sb.append("B");
        } else if (PieceType.KING == this.getPieceType()) {
            sb.append("K");
        } else if (PieceType.PAWN == this.getPieceType()) {
            sb.append("P");
        } else if (PieceType.QUEEN == this.getPieceType()) {
            sb.append("Q");
        } else if (PieceType.KNIGHT == this.getPieceType()) {
            sb.append("Kn");
        } else if (PieceType.ROOK == this.getPieceType()) {
            sb.append("R");
        }
        return sb.toString();
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return this.pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return this.type;
    }

    // --- helpers to reduce duplication ---
    private boolean inBounds(final int row, final int col) {
        return 1 <= row && row <= 8 && 1 <= col && col <= 8;
    }

    private void addMoveIfFreeOrCapture(final List<ChessMove> moves,
                                        final ChessBoard board,
                                        final ChessPosition from,
                                        final int row,
                                        final int col,
                                        final PieceType promote,
                                        final ChessPiece piece) {
        if (!inBounds(row, col)) {
            return;
        }
        final ChessPiece oPiece = board.getPiece(new ChessPosition(row, col));
        if (null == oPiece || oPiece.pieceColor != piece.pieceColor) {
            moves.add(new ChessMove(from, new ChessPosition(row, col), promote));
        }
    }

    private void sweep(final List<ChessMove> moves,
                       final ChessBoard board,
                       final ChessPosition from,
                       final int startRow,
                       final int startCol,
                       final int dRow,
                       final int dCol,
                       final PieceType promote,
                       final ChessPiece piece) {
        int r = startRow + dRow;
        int c = startCol + dCol;
        while (inBounds(r, c)) {
            final ChessPiece oPiece = board.getPiece(new ChessPosition(r, c));
            if (null == oPiece) {
                moves.add(new ChessMove(from, new ChessPosition(r, c), promote));
            } else if (oPiece.pieceColor == piece.pieceColor) {
                break;
            } else {
                moves.add(new ChessMove(from, new ChessPosition(r, c), promote));
                break;
            }
            r += dRow;
            c += dCol;
        }
    }

    public Collection<ChessMove> pawnMoves(final ChessBoard board, final ChessPosition myPosition) {
        final ChessPiece piece = board.getPiece(myPosition);
        final List<ChessMove> moves = new ArrayList<>();

        final int x = myPosition.getColumn();
        final int y = myPosition.getRow();
        final List<PieceType> promotes = new ArrayList<>();

        if (PieceType.PAWN == piece.getPieceType()) {
            //White first
            if (ChessGame.TeamColor.WHITE == piece.getTeamColor()) {
                //Moving forward by 1 not promoting
                if (8 == (y + 1)) {
                    promotes.add(PieceType.QUEEN);
                    //promotes.add(PieceType.PAWN);
                    promotes.add(PieceType.BISHOP);
                    promotes.add(PieceType.KNIGHT);
                    promotes.add(PieceType.ROOK);
                }

                if (null == board.getPiece(new ChessPosition(y + 1, x))) {
                    if (promotes.isEmpty()) {
                        moves.add(new ChessMove(myPosition, new ChessPosition(y + 1, x), null));
                    }
                    for (final PieceType promote : promotes) {
                        moves.add(new ChessMove(myPosition, new ChessPosition(y + 1, x), promote));
                    }
                }
                if (2 == y && null == board.getPiece(new ChessPosition(y + 2, x)) && null == board.getPiece(new ChessPosition(y + 1, x))) {
                    if (promotes.isEmpty()) {
                        moves.add(new ChessMove(myPosition, new ChessPosition(2 + 2, x), null));
                    }
                    for (final PieceType promote : promotes) {
                        moves.add(new ChessMove(myPosition, new ChessPosition(y + 2, x), promote));
                    }
                }

                //Moving Diagonally to take
                //First left direction
                if (1 != x && null != board.getPiece(new ChessPosition(y + 1, x - 1)) && board.getPiece((new ChessPosition(y + 1, x - 1))).pieceColor != piece.pieceColor) {
                    if (promotes.isEmpty()) {
                        moves.add(new ChessMove(myPosition, new ChessPosition(y + 1, x - 1), null));
                    }
                    for (final PieceType promote : promotes) {
                        moves.add(new ChessMove(myPosition, new ChessPosition(y + 1, x - 1), promote));
                    }
                }
                //Right direction
                if (8 != x && null != board.getPiece(new ChessPosition(y + 1, x + 1)) && board.getPiece(new ChessPosition(y + 1, x + 1)).pieceColor != piece.pieceColor) {
                    if (promotes.isEmpty()) {
                        moves.add(new ChessMove(myPosition, new ChessPosition(y + 1, x + 1), null));
                    }
                    for (final PieceType promote : promotes) {
                        moves.add(new ChessMove(myPosition, new ChessPosition(y + 1, x + 1), promote));
                    }
                }
            } else if (ChessGame.TeamColor.BLACK == piece.getTeamColor()) {
                if (1 == (y - 1)) {
                    promotes.add(PieceType.QUEEN);
                    //promotes.add(PieceType.PAWN);
                    promotes.add(PieceType.BISHOP);
                    promotes.add(PieceType.KNIGHT);
                    promotes.add(PieceType.ROOK);
                }
                if (null == board.getPiece(new ChessPosition(y - 1, x))) {
                    if (promotes.isEmpty()) {
                        moves.add(new ChessMove(myPosition, new ChessPosition(y - 1, x), null));
                    }
                    for (final PieceType promote : promotes) {
                        moves.add(new ChessMove(myPosition, new ChessPosition(y - 1, x), promote));
                    }
                }
                if (7 == y && null == board.getPiece(new ChessPosition(y - 2, x)) && null == board.getPiece(new ChessPosition(y - 1, x))) {
                    if (promotes.isEmpty()) {
                        moves.add(new ChessMove(myPosition, new ChessPosition(7 - 2, x), null));
                    }
                    for (final PieceType promote : promotes) {
                        moves.add(new ChessMove(myPosition, new ChessPosition(y - 2, x), promote));
                    }
                }
                if (1 != x && null != board.getPiece(new ChessPosition(y - 1, x - 1))) {
                    if (promotes.isEmpty()) {
                        moves.add(new ChessMove(myPosition, new ChessPosition(y - 1, x - 1), null));
                    }
                    for (final PieceType promote : promotes) {
                        moves.add(new ChessMove(myPosition, new ChessPosition(y - 1, x - 1), promote));
                    }
                }
                //Right direction
                if (8 != x && null != board.getPiece(new ChessPosition(y - 1, x + 1))) {
                    if (promotes.isEmpty()) {
                        moves.add(new ChessMove(myPosition, new ChessPosition(y - 1, x + 1), null));
                    }
                    for (final PieceType promote : promotes) {
                        moves.add(new ChessMove(myPosition, new ChessPosition(y - 1, x + 1), promote));
                    }
                }
            }
        }
        return moves;
    }

    public Collection<ChessMove> rookMoves(final ChessBoard board, final ChessPosition myPosition) {
        final ChessPiece piece = board.getPiece(myPosition);
        final List<ChessMove> moves = new ArrayList<>();
        final int x = myPosition.getColumn();
        final int y = myPosition.getRow();
        final PieceType promote = null;

        if (PieceType.ROOK == piece.getPieceType() || PieceType.QUEEN == piece.getPieceType()) {
            // right, up, left, down
            sweep(moves, board, myPosition, y, x, 0, 1, promote, piece);
            sweep(moves, board, myPosition, y, x, 1, 0, promote, piece);
            sweep(moves, board, myPosition, y, x, 0, -1, promote, piece);
            sweep(moves, board, myPosition, y, x, -1, 0, promote, piece);
        }
        return moves;
    }

    public Collection<ChessMove> bishopMoves(final ChessBoard board, final ChessPosition myPosition) {
        final ChessPiece piece = board.getPiece(myPosition);
        final List<ChessMove> moves = new ArrayList<>();
        final int x = myPosition.getColumn();
        final int y = myPosition.getRow();
        final PieceType promote = null;

        if (PieceType.BISHOP == piece.getPieceType() || PieceType.QUEEN == piece.getPieceType()) {
            // diagonals: NE, NW, SE, SW (in row/col coordinates)
            sweep(moves, board, myPosition, y, x, 1, 1, promote, piece);
            sweep(moves, board, myPosition, y, x, 1, -1, promote, piece);
            sweep(moves, board, myPosition, y, x, -1, 1, promote, piece);
            sweep(moves, board, myPosition, y, x, -1, -1, promote, piece);
        }
        return moves;
    }

    public Collection<ChessMove> knightMoves(final ChessBoard board, final ChessPosition myPosition) {
        final ChessPiece piece = board.getPiece(myPosition);
        final List<ChessMove> moves = new ArrayList<>();
        final int x = myPosition.getColumn();
        final int y = myPosition.getRow();
        final PieceType promote = null;

        if (PieceType.KNIGHT == piece.getPieceType()) {
            final int[][] offsets = {
                    {2, -1}, {2, 1},
                    {1, -2}, {1, 2},
                    {-2, -1}, {-2, 1},
                    {-1, -2}, {-1, 2}
            };
            for (final int[] d : offsets) {
                addMoveIfFreeOrCapture(moves, board, myPosition, y + d[0], x + d[1], promote, piece);
            }
        }
        return moves;
    }

    public Collection<ChessMove> kingMoves(final ChessBoard board, final ChessPosition myPosition) {
        final ChessPiece piece = board.getPiece(myPosition);
        final List<ChessMove> moves = new ArrayList<>();
        final int x = myPosition.getColumn();
        final int y = myPosition.getRow();
        final PieceType promote = null;

        if (PieceType.KING == piece.getPieceType()) {
            for (int dc = -1; dc <= 1; dc++) {
                for (int dr = -1; dr <= 1; dr++) {
                    if (0 == dc && 0 == dr) {
                        continue;
                    }
                    addMoveIfFreeOrCapture(moves, board, myPosition, y + dr, x + dc, promote, piece);
                }
            }
        }
        return moves;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(final ChessBoard board, final ChessPosition myPosition) {
        //Instantiate variable
        final List<ChessMove> moves = new ArrayList<>();
        moves.addAll(this.pawnMoves(board, myPosition));
        moves.addAll(this.rookMoves(board, myPosition));
        moves.addAll(this.bishopMoves(board, myPosition));
        moves.addAll(this.knightMoves(board, myPosition));
        moves.addAll(this.kingMoves(board, myPosition));
        return moves;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }
}
