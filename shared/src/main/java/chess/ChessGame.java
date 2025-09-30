package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private TeamColor team;
    private ChessBoard board = new ChessBoard();
    public ChessGame() {
        team = TeamColor.WHITE;
        board.resetBoard();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return team == chessGame.team && Objects.equals(getBoard(), chessGame.getBoard());
    }

    @Override
    public int hashCode() {
        return Objects.hash(team, getBoard());
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return team;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.team = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        
        ChessPiece piece = board.getPiece(startPosition);
        List<ChessMove> temp = (List) piece.pieceMoves(board,startPosition);
        List<ChessMove> moves = new ArrayList<>();
        for (ChessMove move : temp){
            if (!keepsCheck(move)){
                moves.add(move);
            }
        }
        return moves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        if (board.getPiece(move.getStartPosition())==null){
            throw new InvalidMoveException("No Piece at position");
        }
        if (board.getPiece(move.getStartPosition()).getTeamColor() != team){
            throw new InvalidMoveException("Piece is not of your teams color");
        }
        Collection<ChessMove> validMoves = validMoves(move.getStartPosition());
        if (validMoves.contains(move)){
            int i_col = move.getStartPosition().getColumn();
            int i_row = move.getStartPosition().getRow();
            int e_col = move.getEndPosition().getColumn();
            int e_row = move.getEndPosition().getRow();
            forceMakeMove(e_row, e_col, i_row, i_col, move.getPromotionPiece());
            if (team == TeamColor.WHITE){
                team = TeamColor.BLACK;
            }
            else {
                team = TeamColor.WHITE;
            }
        }
        else {
            StringBuilder sb = new StringBuilder();
            sb.append(move);
            sb.append(" is not a valid move");
            throw new InvalidMoveException(sb.toString());
        }

    }

    private void forceMakeMove(int e_row, int e_col, int i_row, int i_col, ChessPiece.PieceType promotion) {
        ChessBoard temp = new ChessBoard();
        TeamColor clr = board.getPiece(new ChessPosition(i_row,i_col)).getTeamColor();
        ChessPiece.PieceType type = board.getPiece(new ChessPosition(i_row, i_col)).getPieceType();
        if (promotion != null) {
            type = promotion;
        }

        for (int row = 1; row <=8;row++){
            for (int col = 1; col<=8;col++){
                if (row == e_row && col == e_col){
                    ChessPiece piece = new ChessPiece(clr, type);
                    temp.addPiece(new ChessPosition(row, col), piece);
                }
                else if (!(row == i_row && col == i_col)){
                    ChessPiece temp_piece = board.getPiece(new ChessPosition(row, col));
                    if (temp_piece != null) {
                        ChessPiece piece = new ChessPiece(board.getPiece(new ChessPosition(row, col)).getTeamColor(), board.getPiece(new ChessPosition(row, col)).getPieceType());
                        temp.addPiece(new ChessPosition(row, col), piece);
                    }
                }
            }
        }
        setBoard(temp);
    }
    public Collection<ChessMove> getAllPosibleMovesStrict(TeamColor teamColor){
        List<ChessMove> moves = new ArrayList<>();
        for (int row = 1; row<=8;row++){
            for (int col = 1; col<=8;col++){
                ChessPiece piece = board.getPiece(new ChessPosition(row,col));
                if (piece != null && teamColor == piece.getTeamColor()) {
                    moves.addAll(validMoves(new ChessPosition(row,col)));
                }
            }
        }

        return moves;
    }


    public Collection<ChessMove> getAllPosibleMoves(TeamColor teamColor){
        List<ChessMove> moves = new ArrayList<>();
        for (int row = 1; row<=8;row++){
            for (int col = 1; col<=8;col++){
                ChessPiece piece = board.getPiece(new ChessPosition(row,col));
                if (piece != null && teamColor == piece.getTeamColor()) {
                    moves.addAll(piece.pieceMoves(this.board,new ChessPosition(row,col)));
                }
            }
        }
        
        return moves;
    }
    
    public ChessPosition getKingsPosition(TeamColor teamColor){
        for (int row =1; row<=8;row++){
            for (int col =1; col<=8;col++){
                ChessPiece piece = board.getPiece(new ChessPosition(row,col));
                if (piece != null &&teamColor == piece.getTeamColor() && piece.getPieceType() == ChessPiece.PieceType.KING){
                    return new ChessPosition(row, col);
                }
            }
        }
        return new ChessPosition(0,0); //TODO: Get Rid of This so that it is better 
    }

    public boolean keepsCheck(ChessMove move){
        TeamColor clr = this.board.getPiece(move.getStartPosition()).getTeamColor();
        //Simulate the move
        ChessGame simGame = new ChessGame();
        simGame.setBoard(this.board);
        //Manually set the board
        simGame.forceMakeMove(move.getEndPosition().getRow(),move.getEndPosition().getColumn(),move.getStartPosition().getRow(),move.getStartPosition().getColumn(),move.getPromotionPiece());
        if (simGame.isInCheck(clr)){ //We have an inf loop
            return true;
        }
        return false;
    }
    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        //Collect all the moves for the opposite team

        TeamColor opponent = TeamColor.WHITE;
        ChessPosition kingPosition = getKingsPosition(teamColor);
        if (teamColor == TeamColor.WHITE){
            opponent = TeamColor.BLACK;
        }
 
        List<ChessMove> moves = (List) getAllPosibleMoves(opponent);
        for (ChessMove move : moves){
            if (move.getEndPosition().equals(kingPosition)){
                return true;
            }
        }
        return false;
        
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (isInCheck(teamColor)){
            List moves = (List) getAllPosibleMovesStrict(teamColor); //I need to use valid moves here
            if (moves.isEmpty()){
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        List moves = (List) getAllPosibleMovesStrict(teamColor);
        if (moves.isEmpty() && !isInCheck(teamColor)){
            return true;
        }
        return false;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        //Go Piece by piece
        ChessBoard temp = new ChessBoard();
        for (int row = 1; row<=8; row++){
            for (int col = 1; col<=8; col++){
                ChessPiece piece = board.getPiece(new ChessPosition(row, col));
                if (piece != null){
                    temp.addPiece(new ChessPosition(row,col),new ChessPiece(piece.pieceColor,piece.type));
                }
            }
        }
        this.board = temp;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return this.board;
    }
}
