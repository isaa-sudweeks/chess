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
    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ChessPiece that)) {
            return false;
        }
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        if (pieceColor == ChessGame.TeamColor.WHITE){
            sb.append("(W)");
        }
        else if (pieceColor == ChessGame.TeamColor.BLACK){
            sb.append("(B)");
        }
        if (this.getPieceType() == PieceType.BISHOP){
            sb.append("B");
        }
        else if (this.getPieceType() == PieceType.KING){
            sb.append("K");
        }
        else if (this.getPieceType() == PieceType.PAWN){
            sb.append("P");
        }
        else if (this.getPieceType() == PieceType.QUEEN){
            sb.append("Q");
        }
        else if (this.getPieceType() == PieceType.KNIGHT){
            sb.append("Kn");
        }
        else if (this.getPieceType() == PieceType.ROOK){
            sb.append("R");
        }
        return sb.toString();
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

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }
    public Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);
        List<ChessMove> moves = new ArrayList<>();

        int x = myPosition.getColumn();
        int y = myPosition.getRow();
        List<PieceType> promotes = new ArrayList<>();

        if (piece.getPieceType() == PieceType.PAWN){
            //White first
            if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                //Moving forward by 1 not promoting
                if ((y+1) == 8){
                    promotes.add(PieceType.QUEEN);
                    //promotes.add(PieceType.PAWN);
                    promotes.add(PieceType.BISHOP);
                    promotes.add(PieceType.KNIGHT);
                    promotes.add(PieceType.ROOK);
                }

                if (board.getPiece(new ChessPosition(y + 1, x)) == null) {
                    if (promotes.isEmpty()){
                        moves.add(new ChessMove(myPosition, new ChessPosition(y + 1, x), null));
                    }
                    for (PieceType promote : promotes) {
                        moves.add(new ChessMove(myPosition, new ChessPosition(y + 1, x), promote));
                    }
                }
                if (y == 2 && board.getPiece(new ChessPosition(y+2,x))==null && board.getPiece(new ChessPosition(y + 1, x)) == null) {
                    if (promotes.isEmpty()){
                        moves.add(new ChessMove(myPosition, new ChessPosition(y + 2, x), null));
                    }
                    for (PieceType promote : promotes) {
                        moves.add(new ChessMove(myPosition, new ChessPosition(y + 2, x), promote));
                    }
                }

                //Moving Diagonally to take
                //First left direction
                if (x != 1 && board.getPiece(new ChessPosition(y+1,x-1)) != null && board.getPiece((new ChessPosition(y+1, x-1))).getTeamColor() != piece.getTeamColor()){
                    if (promotes.isEmpty()){
                        moves.add(new ChessMove(myPosition, new ChessPosition(y + 1, x - 1), null));
                    }
                    for (PieceType promote : promotes) {
                        moves.add(new ChessMove(myPosition, new ChessPosition(y + 1, x - 1), promote));
                    }
                }
                //Right direction
                if (x != 8 && board.getPiece(new ChessPosition(y+1,x+1)) != null && board.getPiece(new ChessPosition(y+1,x+1)).getTeamColor() != piece.getTeamColor()){
                    if (promotes.isEmpty()){
                        moves.add(new ChessMove(myPosition, new ChessPosition(y+1,x+1),null));
                    }
                    for (PieceType promote : promotes) {
                        moves.add(new ChessMove(myPosition, new ChessPosition(y + 1, x + 1), promote));
                    }
                }
            }
            else if (piece.getTeamColor() == ChessGame.TeamColor.BLACK){
                if ((y-1) == 1){
                    promotes.add(PieceType.QUEEN);
                    //promotes.add(PieceType.PAWN);
                    promotes.add(PieceType.BISHOP);
                    promotes.add(PieceType.KNIGHT);
                    promotes.add(PieceType.ROOK);
                }
                if (board.getPiece(new ChessPosition(y-1, x)) == null) {
                    if (promotes.isEmpty()){
                        moves.add(new ChessMove(myPosition, new ChessPosition(y - 1, x), null));
                    }
                    for (PieceType promote : promotes) {
                        moves.add(new ChessMove(myPosition, new ChessPosition(y - 1, x), promote));
                    }
                }
                if (y == 7 && board.getPiece(new ChessPosition(y-2,x))==null && board.getPiece(new ChessPosition(y-1, x)) == null) {
                    if (promotes.isEmpty()){
                        moves.add(new ChessMove(myPosition, new ChessPosition(y - 2, x), null));
                    }
                    for (PieceType promote : promotes) {
                        moves.add(new ChessMove(myPosition, new ChessPosition(y - 2, x), promote));
                    }
                }
                if (x != 1 && board.getPiece(new ChessPosition(y-1,x-1)) != null){
                    if (promotes.isEmpty()){
                        moves.add(new ChessMove(myPosition, new ChessPosition(y - 1, x - 1), null));
                    }
                    for (PieceType promote : promotes) {
                        moves.add(new ChessMove(myPosition, new ChessPosition(y - 1, x - 1), promote));
                    }
                }
                //Right direction
                if (x != 8 && board.getPiece(new ChessPosition(y-1,x+1)) != null){
                    if (promotes.isEmpty()){
                        moves.add(new ChessMove(myPosition, new ChessPosition(y - 1, x + 1), null));
                    }
                    for (PieceType promote : promotes) {
                        moves.add(new ChessMove(myPosition, new ChessPosition(y - 1, x + 1), promote));
                    }
                }
            }
        }
        return moves;
    }

    public Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);
        List<ChessMove> moves = new ArrayList<>();
        int x = myPosition.getColumn();
        int y = myPosition.getRow();
        PieceType promote = null;
        if (piece.getPieceType() == PieceType.ROOK || piece.getPieceType() == PieceType.QUEEN) {
            for (int i = x; i <= 8; i++) {
                ChessPiece oPiece = board.getPiece(new ChessPosition(y, i));
                if (oPiece == null) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(y, i), promote));
                } else if (oPiece.pieceColor == piece.pieceColor) {
                    break;
                } else {
                    moves.add(new ChessMove(myPosition, new ChessPosition(y, i), promote));
                    break;
                }
            }
            for (int i = y; i <= 8; i++) {
                ChessPiece oPiece = board.getPiece(new ChessPosition(i, x));
                if (oPiece == null) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(i, x), promote));
                } else if (oPiece.pieceColor == piece.pieceColor) {
                    break;
                } else {
                    moves.add(new ChessMove(myPosition, new ChessPosition(i, x), promote));
                    break;
                }
            }

            for (int i = x; i >= 1; i--) {
                ChessPiece oPiece = board.getPiece(new ChessPosition(y, i));
                if (oPiece == null) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(y, i), promote));
                } else if (oPiece.pieceColor == piece.pieceColor) {
                    break;
                } else {
                    moves.add(new ChessMove(myPosition, new ChessPosition(y, i), promote));
                    break;
                }
            }
            for (int i = y; i >= 1; i--) {
                ChessPiece oPiece = board.getPiece(new ChessPosition(i, x));
                if (oPiece == null) {
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
    public Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);
        List<ChessMove> moves = new ArrayList<>();
        int x = myPosition.getColumn();
        int y = myPosition.getRow();
        PieceType promote = null;

        if (piece.getPieceType() == PieceType.BISHOP || piece.getPieceType() == PieceType.QUEEN) {
            for (int i = 1; i + x <= 8 && i + y <= 8; i++) {
                ChessPiece oPiece = board.getPiece(new ChessPosition(y + i, x + i));
                if (oPiece == null) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(y + i, x + i), promote));
                } else if (oPiece.pieceColor == piece.pieceColor) {
                    break;
                } else {
                    moves.add(new ChessMove(myPosition, new ChessPosition(y + i, x + i), promote));
                    break;
                }
            }
            for (int i = -1; i + x >= 1 && i + y >= 1; i--) {
                ChessPiece oPiece = board.getPiece(new ChessPosition(y + i, x + i));
                if (oPiece == null) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(y + i, x + i), promote));
                } else if (oPiece.pieceColor == piece.pieceColor) {
                    break;
                } else {
                    moves.add(new ChessMove(myPosition, new ChessPosition(y + i, x + i), promote));
                    break;
                }
            }
            for (int i = 1; x - i >= 1 && i + y <= 8; i++) {
                ChessPiece oPiece = board.getPiece(new ChessPosition(y + i, x - i));
                if (oPiece == null) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(y + i, x - i), promote));
                } else if (oPiece.pieceColor == piece.pieceColor) {
                    break;
                } else {
                    moves.add(new ChessMove(myPosition, new ChessPosition(y + i, x - i), promote));
                    break;
                }
            }
            for (int i = 1; i + x <= 8 && y - i >= 1; i++) {
                ChessPiece oPiece = board.getPiece(new ChessPosition(y - i, x + i));
                if (oPiece == null) {
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

    public Collection<ChessMove> knightMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);
        List<ChessMove> moves = new ArrayList<>();
        int x = myPosition.getColumn();
        int y = myPosition.getRow();
        PieceType promote = null;
        //There are eight possible moves for knights
        //Up 2 left 1
        if (piece.getPieceType() == PieceType.KNIGHT) {
            if ((y + 2 <= 8 && x - 1 >= 1) && (board.getPiece(new ChessPosition(y + 2, x - 1)) == null || board.getPiece(new ChessPosition(y + 2, x - 1)).getTeamColor() != piece.getTeamColor())) {
                moves.add(new ChessMove(myPosition, new ChessPosition(y + 2, x - 1), promote));
            }
            //Up 2 right 1
            if ((y + 2 <= 8 && x + 1 <= 8) && (board.getPiece(new ChessPosition(y + 2, x + 1)) == null || board.getPiece(new ChessPosition(y + 2, x + 1)).getTeamColor() != piece.getTeamColor())) {
                moves.add(new ChessMove(myPosition, new ChessPosition(y + 2, x + 1), promote));
            }
            //left 2 up 1
            if ((y + 1 <= 8 && x - 2 >= 1) && (board.getPiece(new ChessPosition(y + 1, x - 2)) == null || board.getPiece(new ChessPosition(y + 1, x - 2)).getTeamColor() != piece.getTeamColor())) {
                moves.add(new ChessMove(myPosition, new ChessPosition(y + 1, x - 2), promote));
            }
            //right 2 up 1
            if ((y + 1 <= 8 && x + 2 <= 8) && (board.getPiece(new ChessPosition(y + 1, x + 2)) == null || board.getPiece(new ChessPosition(y + 1, x + 2)).getTeamColor() != piece.getTeamColor())) {
                moves.add(new ChessMove(myPosition, new ChessPosition(y + 1, x + 2), promote));
            }
            //Down 2 left 1
            if ((y - 2 >= 1 && x - 1 >= 1) && (board.getPiece(new ChessPosition(y - 2, x - 1)) == null || board.getPiece(new ChessPosition(y - 2, x - 1)).getTeamColor() != piece.getTeamColor())) {
                moves.add(new ChessMove(myPosition, new ChessPosition(y - 2, x - 1), promote));
            }
            //Down 2 right 1
            if ((y - 2 >= 1 && x + 1 <= 8) && (board.getPiece(new ChessPosition(y - 2, x + 1)) == null || board.getPiece(new ChessPosition(y - 2, x + 1)).getTeamColor() != piece.getTeamColor())) {
                moves.add(new ChessMove(myPosition, new ChessPosition(y - 2, x + 1), promote));
            }
            //left 2 down 1
            if ((y - 1 >= 1 && x - 2 >= 1) && (board.getPiece(new ChessPosition(y - 1, x - 2)) == null || board.getPiece(new ChessPosition(y - 1, x - 2)).getTeamColor() != piece.getTeamColor())) {
                moves.add(new ChessMove(myPosition, new ChessPosition(y - 1, x - 2), promote));
            }
            //right 2 down 1
            if ((y - 1 >= 1 && x + 2 <= 8) && (board.getPiece(new ChessPosition(y - 1, x + 2)) == null || board.getPiece(new ChessPosition(y - 1, x + 2)).getTeamColor() != piece.getTeamColor())) {
                moves.add(new ChessMove(myPosition, new ChessPosition(y - 1, x + 2), promote));
            }
        }
        return moves;
    }

    public Collection<ChessMove> kingMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);
        List<ChessMove> moves = new ArrayList<>();
        int x = myPosition.getColumn();
        int y = myPosition.getRow();
        PieceType promote = null;
        //There is simply just a box of moves around the king
        if (piece.getPieceType() == PieceType.KING) {
            for (int i = Math.max(1, x - 1); i <= Math.min(x + 1, 8); i++) {
                for (int j = Math.min(y + 1, 8); j >= Math.max(1, y - 1); j--) {
                    if (!(i == x && j == y)) {
                        if (board.getPiece(new ChessPosition(j, i)) == null || board.getPiece(new ChessPosition(j, i)).getTeamColor() != piece.getTeamColor()) {
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
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        //Instantiate variable
        List<ChessMove> moves = new ArrayList<>();
        moves.addAll(pawnMoves(board, myPosition));
        moves.addAll(rookMoves(board,myPosition));
        moves.addAll(bishopMoves(board, myPosition));
        moves.addAll(knightMoves(board, myPosition));
        moves.addAll(kingMoves(board, myPosition));
        return moves;
    }
}
