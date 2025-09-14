package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
        PieceType promote = null;

        //TODO I forgot to check if the pieces are ours or the other teams
        if (piece.getPieceType() == PieceType.PAWN){
            //White first
            if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                //Moving forward by 1 not promoting
                if ((y+1) == 8){
                    promote = PieceType.QUEEN;
                }

                if (board.getPiece(new ChessPosition(y + 1, x)) == null) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(y + 1, x), promote));
                }
                if (y == 2 && board.getPiece(new ChessPosition(y+2,x))==null) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(y + 2, x), promote));
                }

                //Moving Diagonally to take
                //First left direction
                if (x != 1 && board.getPiece(new ChessPosition(y+1,x-1)) != null){
                    moves.add(new ChessMove(myPosition, new ChessPosition(y+1,x-1),promote));
                }
                //Right direction
                if (x != 8 && board.getPiece(new ChessPosition(y+1,x+1)) != null){
                    moves.add(new ChessMove(myPosition, new ChessPosition(y+1,x+1),promote));
                }
            }
            else if (piece.getTeamColor() == ChessGame.TeamColor.BLACK){
                if ((y-1) == 1){
                    promote = PieceType.QUEEN;
                }
                if (board.getPiece(new ChessPosition(y-1, x)) == null) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(y - 1, x), promote));
                }
                if (y == 7 && board.getPiece(new ChessPosition(y-2,x))==null) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(y - 2, x), promote));
                }
                if (x != 1 && board.getPiece(new ChessPosition(y-1,x-1)) != null){
                    moves.add(new ChessMove(myPosition, new ChessPosition(y-1,x-1),promote));
                }
                //Right direction
                if (x != 8 && board.getPiece(new ChessPosition(y-1,x+1)) != null){
                    moves.add(new ChessMove(myPosition, new ChessPosition(y-1,x+1),promote));
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

        for (int i = x; i <=8;i++){
            ChessPiece oPiece = board.getPiece(new ChessPosition(y, i));
            if (oPiece == null){
                moves.add(new ChessMove(myPosition, new ChessPosition(y,i), promote));
            }
            else if (oPiece.pieceColor == piece.pieceColor){
                break;
            }
            else {
                moves.add(new ChessMove(myPosition, new ChessPosition(y,i), promote));
                break;
            }
        }
        for (int i = y; i <=8;i++){
            ChessPiece oPiece = board.getPiece(new ChessPosition(i, x));
            if (oPiece == null){
                moves.add(new ChessMove(myPosition, new ChessPosition(i,x), promote));
            }
            else if (oPiece.pieceColor == piece.pieceColor){
                break;
            }
            else {
                moves.add(new ChessMove(myPosition, new ChessPosition(i,x), promote));
                break;
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

        for (int i = 0; i+x <=8 && i+y <=8;i++) {
                ChessPiece oPiece = board.getPiece(new ChessPosition(y+i, x+i));
                if (oPiece == null) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(y+i, x+i), promote));
                } else if (oPiece.pieceColor == piece.pieceColor) {
                    break;
                } else {
                    moves.add(new ChessMove(myPosition, new ChessPosition(y+i, x+i), promote));
                    break;
                }
        }
        for (int i = 0; i+x >=0 && i+y>=0;i--) {
            ChessPiece oPiece = board.getPiece(new ChessPosition(y+i, x+i));
            if (oPiece == null) {
                moves.add(new ChessMove(myPosition, new ChessPosition(y+i, x+i), promote));
            } else if (oPiece.pieceColor == piece.pieceColor) {
                break;
            } else {
                moves.add(new ChessMove(myPosition, new ChessPosition(y+i, x+i), promote));
                break;
            }
        }
        for (int i = 0; x-i >=0 && i+y<=8;i++) {
            ChessPiece oPiece = board.getPiece(new ChessPosition(y+i, x-i));
            if (oPiece == null) {
                moves.add(new ChessMove(myPosition, new ChessPosition(y+i, x-i), promote));
            } else if (oPiece.pieceColor == piece.pieceColor) {
                break;
            } else {
                moves.add(new ChessMove(myPosition, new ChessPosition(y+i, x-i), promote));
                break;
            }
        }
        for (int i = 0; i+x <=8 && y-i>=0;i++) {
            ChessPiece oPiece = board.getPiece(new ChessPosition(y-i, x+i));
            if (oPiece == null) {
                moves.add(new ChessMove(myPosition, new ChessPosition(y-i, x+i), promote));
            } else if (oPiece.pieceColor == piece.pieceColor) {
                break;
            } else {
                moves.add(new ChessMove(myPosition, new ChessPosition(y-i, x+i), promote));
                break;
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



        return moves;
    }
}
