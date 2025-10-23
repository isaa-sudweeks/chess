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
            for (int i = x + 1; 8 >= i; i++) {
                final ChessPiece oPiece = board.getPiece(new ChessPosition(y, i));
                if (null == oPiece) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(y, i), promote));
                } else if (oPiece.pieceColor == piece.pieceColor) {
                    break;
                } else {
                    moves.add(new ChessMove(myPosition, new ChessPosition(y, i), promote));
                    break;
                }
            }
            for (int i = y + 1; 8 >= i; i++) {
                final ChessPiece oPiece = board.getPiece(new ChessPosition(i, x));
                if (null == oPiece) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(i, x), promote));
                } else if (oPiece.pieceColor == piece.pieceColor) {
                    break;
                } else {
                    moves.add(new ChessMove(myPosition, new ChessPosition(i, x), promote));
                    break;
                }
            }

            for (int i = x - 1; 1 <= i; i--) {
                final ChessPiece oPiece = board.getPiece(new ChessPosition(y, i));
                if (null == oPiece) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(y, i), promote));
                } else if (oPiece.pieceColor == piece.pieceColor) {
                    break;
                } else {
                    moves.add(new ChessMove(myPosition, new ChessPosition(y, i), promote));
                    break;
                }
            }
            for (int i = y - 1; 1 <= i; i--) {
                final ChessPiece oPiece = board.getPiece(new ChessPosition(i, x));
                if (null == oPiece) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(i, x), promote));
                } else if (oPiece.pieceColor == piece.pieceColor) {
                    break;
                } else {
                    moves.add(new ChessMove(myPosition, new ChessPosition(i, x), promote));
                    break;
                }
            }
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
            for (int i = 1; 8 >= i + x && 8 >= i + y; i++) {
                final ChessPiece oPiece = board.getPiece(new ChessPosition(y + i, x + i));
                if (null == oPiece) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(y + i, x + i), promote));
                } else if (oPiece.pieceColor == piece.pieceColor) {
                    break;
                } else {
                    moves.add(new ChessMove(myPosition, new ChessPosition(y + i, x + i), promote));
                    break;
                }
            }
            for (int i = -1; 1 <= i + x && 1 <= i + y; i--) {
                final ChessPiece oPiece = board.getPiece(new ChessPosition(y + i, x + i));
                if (null == oPiece) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(y + i, x + i), promote));
                } else if (oPiece.pieceColor == piece.pieceColor) {
                    break;
                } else {
                    moves.add(new ChessMove(myPosition, new ChessPosition(y + i, x + i), promote));
                    break;
                }
            }
            for (int i = 1; 1 <= x - i && 8 >= i + y; i++) {
                final ChessPiece oPiece = board.getPiece(new ChessPosition(y + i, x - i));
                if (null == oPiece) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(y + i, x - i), promote));
                } else if (oPiece.pieceColor == piece.pieceColor) {
                    break;
                } else {
                    moves.add(new ChessMove(myPosition, new ChessPosition(y + i, x - i), promote));
                    break;
                }
            }
            for (int i = 1; 8 >= i + x && 1 <= y - i; i++) {
                final ChessPiece oPiece = board.getPiece(new ChessPosition(y - i, x + i));
                if (null == oPiece) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(y - i, x + i), promote));
                } else if (oPiece.pieceColor == piece.pieceColor) {
                    break;
                } else {
                    moves.add(new ChessMove(myPosition, new ChessPosition(y - i, x + i), promote));
                    break;
                }
            }
        }
        return moves;
    }

    public Collection<ChessMove> knightMoves(final ChessBoard board, final ChessPosition myPosition) {
        final ChessPiece piece = board.getPiece(myPosition);
        final List<ChessMove> moves = new ArrayList<>();
        final int x = myPosition.getColumn();
        final int y = myPosition.getRow();
        final PieceType promote = null;
        //There are eight possible moves for knights
        //Up 2 left 1
        if (PieceType.KNIGHT == piece.getPieceType()) {
            if ((8 >= y + 2 && 1 <= x - 1) && (null == board.getPiece(new ChessPosition(y + 2, x - 1)) || board.getPiece(new ChessPosition(y + 2, x - 1)).pieceColor != piece.pieceColor)) {
                moves.add(new ChessMove(myPosition, new ChessPosition(y + 2, x - 1), promote));
            }
            //Up 2 right 1
            if ((8 >= y + 2 && 8 >= x + 1) && (null == board.getPiece(new ChessPosition(y + 2, x + 1)) || board.getPiece(new ChessPosition(y + 2, x + 1)).pieceColor != piece.pieceColor)) {
                moves.add(new ChessMove(myPosition, new ChessPosition(y + 2, x + 1), promote));
            }
            //left 2 up 1
            if ((8 >= y + 1 && 1 <= x - 2) && (null == board.getPiece(new ChessPosition(y + 1, x - 2)) || board.getPiece(new ChessPosition(y + 1, x - 2)).pieceColor != piece.pieceColor)) {
                moves.add(new ChessMove(myPosition, new ChessPosition(y + 1, x - 2), promote));
            }
            //right 2 up 1
            if ((8 >= y + 1 && 8 >= x + 2) && (null == board.getPiece(new ChessPosition(y + 1, x + 2)) || board.getPiece(new ChessPosition(y + 1, x + 2)).pieceColor != piece.pieceColor)) {
                moves.add(new ChessMove(myPosition, new ChessPosition(y + 1, x + 2), promote));
            }
            //Down 2 left 1
            if ((1 <= y - 2 && 1 <= x - 1) && (null == board.getPiece(new ChessPosition(y - 2, x - 1)) || board.getPiece(new ChessPosition(y - 2, x - 1)).pieceColor != piece.pieceColor)) {
                moves.add(new ChessMove(myPosition, new ChessPosition(y - 2, x - 1), promote));
            }
            //Down 2 right 1
            if ((1 <= y - 2 && 8 >= x + 1) && (null == board.getPiece(new ChessPosition(y - 2, x + 1)) || board.getPiece(new ChessPosition(y - 2, x + 1)).pieceColor != piece.pieceColor)) {
                moves.add(new ChessMove(myPosition, new ChessPosition(y - 2, x + 1), promote));
            }
            //left 2 down 1
            if ((1 <= y - 1 && 1 <= x - 2) && (null == board.getPiece(new ChessPosition(y - 1, x - 2)) || board.getPiece(new ChessPosition(y - 1, x - 2)).pieceColor != piece.pieceColor)) {
                moves.add(new ChessMove(myPosition, new ChessPosition(y - 1, x - 2), promote));
            }
            //right 2 down 1
            if ((1 <= y - 1 && 8 >= x + 2) && (null == board.getPiece(new ChessPosition(y - 1, x + 2)) || board.getPiece(new ChessPosition(y - 1, x + 2)).pieceColor != piece.pieceColor)) {
                moves.add(new ChessMove(myPosition, new ChessPosition(y - 1, x + 2), promote));
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
        //There is simply just a box of moves around the king
        if (PieceType.KING == piece.getPieceType()) {
            for (int i = Math.max(1, x - 1); i <= Math.min(x + 1, 8); i++) {
                for (int j = Math.min(y + 1, 8); j >= Math.max(1, y - 1); j--) {
                    if (!(i == x && j == y)) {
                        if (null == board.getPiece(new ChessPosition(j, i)) || board.getPiece(new ChessPosition(j, i)).pieceColor != piece.pieceColor) {
                            moves.add(new ChessMove(myPosition, new ChessPosition(j, i), promote));
                        }
                    }
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
