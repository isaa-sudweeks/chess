package chess;
import java.util.*;
/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    private List<List<Object>> pieces = new ArrayList<>(); //This is a list that contains all pieces with their positions

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
    public void addPiece(ChessPosition position, ChessPiece piece) {
        List<Object> temp = new ArrayList<>();
        temp.add(position);
        temp.add(piece);
        pieces.add(temp);

    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        for (List<Object> i : pieces){
            if(i.contains(position)){
                return (ChessPiece) i.get(1);
            }
        }
        return null;
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        pieces.clear();
        //Start with white side
        //First Rank
        ChessPiece rookW1 = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
        addPiece(new ChessPosition(1,1),rookW1);

        ChessPiece bishopW1 = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
        addPiece(new ChessPosition(1,2),bishopW1);

        ChessPiece knightW1 = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
        addPiece(new ChessPosition(1,3),knightW1);

        ChessPiece queenW = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN);
        addPiece(new ChessPosition(1,4),queenW);

        ChessPiece kingW = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING);
        addPiece(new ChessPosition(1,5),kingW);

        ChessPiece knightW2 = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
        addPiece(new ChessPosition(1,6),knightW2);

        ChessPiece bishopW2 = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
        addPiece(new ChessPosition(1,7),bishopW2);

        ChessPiece rookW2 = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
        addPiece(new ChessPosition(1,8),rookW2);

        //Pawns
        for (int i =1; i<=8;i++){
            addPiece(new ChessPosition(2,i),new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
        }

        //Black Side

        ChessPiece rookB1 = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
        addPiece(new ChessPosition(8,1),rookB1);

        ChessPiece bishopB1 = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
        addPiece(new ChessPosition(8,2),bishopW1);

        ChessPiece knightB1 = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
        addPiece(new ChessPosition(8,3), knightB1);

        ChessPiece queenB = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN);
        addPiece(new ChessPosition(8,4), queenB);

        ChessPiece kingB = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING);
        addPiece(new ChessPosition(8,5), kingB);

        ChessPiece knightB2 = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
        addPiece(new ChessPosition(8,6), knightB2);

        ChessPiece bishopB2 = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
        addPiece(new ChessPosition(8,7),bishopB2);

        ChessPiece rookB2 = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
        addPiece(new ChessPosition(8,8), rookB2);

        //Pawns
        for (int i =1; i<=8;i++){
            addPiece(new ChessPosition(7,i),new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != ChessBoard.class) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        if (that.pieces.equals(pieces)){
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(pieces);
    }
}
