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
        this.team = TeamColor.WHITE;
        this.board.resetBoard();
    }

    @Override
    public boolean equals(final Object o) {
        if (null == o || this.getClass() != o.getClass()) {
            return false;
        }
        final ChessGame chessGame = (ChessGame) o;
        return this.team == chessGame.team && Objects.equals(this.board, chessGame.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.team, this.board);
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return this.team;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(final TeamColor team) {
        this.team = team;
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(final ChessPosition startPosition) {

        final ChessPiece piece = this.board.getPiece(startPosition);
        final List<ChessMove> temp = (List) piece.pieceMoves(this.board, startPosition);
        final List<ChessMove> moves = new ArrayList<>();
        for (final ChessMove move : temp) {
            if (!this.keepsCheck(move)) {
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
    public void makeMove(final ChessMove move) throws InvalidMoveException {
        if (null == board.getPiece(move.getStartPosition())) {
            throw new InvalidMoveException("No Piece at position");
        }
        if (this.board.getPiece(move.getStartPosition()).getTeamColor() != this.team) {
            throw new InvalidMoveException("Piece is not of your teams color");
        }
        final Collection<ChessMove> validMoves = this.validMoves(move.getStartPosition());
        if (validMoves.contains(move)) {
            final int iCol = move.getStartPosition().getColumn();
            final int iRow = move.getStartPosition().getRow();
            final int eCol = move.getEndPosition().getColumn();
            final int eRow = move.getEndPosition().getRow();
            this.forceMakeMove(eRow, eCol, iRow, iCol, move.getPromotionPiece());
            if (TeamColor.WHITE == team) {
                this.team = TeamColor.BLACK;
            } else {
                this.team = TeamColor.WHITE;
            }
        } else {
            String sb = move +
                    " is not a valid move";
            throw new InvalidMoveException(sb);
        }

    }

    private void forceMakeMove(final int eRow, final int eCol, final int iRow, final int iCol, final ChessPiece.PieceType promotion) {
        final ChessBoard temp = new ChessBoard();
        final TeamColor clr = this.board.getPiece(new ChessPosition(iRow, iCol)).getTeamColor();
        ChessPiece.PieceType type = this.board.getPiece(new ChessPosition(iRow, iCol)).getPieceType();
        if (null != promotion) {
            type = promotion;
        }

        for (int row = 1; 8 >= row; row++) {
            for (int col = 1; 8 >= col; col++) {
                if (row == eRow && col == eCol) {
                    final ChessPiece piece = new ChessPiece(clr, type);
                    temp.addPiece(new ChessPosition(row, col), piece);
                } else if (!(row == iRow && col == iCol)) {
                    final ChessPiece chessPiece = this.board.getPiece(new ChessPosition(row, col));
                    if (null != chessPiece) {
                        final ChessPiece piece = new ChessPiece(this.board.getPiece(new ChessPosition(row, col)).getTeamColor(), this.board.getPiece(new ChessPosition(row, col)).getPieceType());
                        temp.addPiece(new ChessPosition(row, col), piece);
                    }
                }
            }
        }
        this.setBoard(temp);
    }

    public Collection<ChessMove> getAllPossibleMovesStrict(final TeamColor teamColor) {
        final List<ChessMove> moves = new ArrayList<>();
        for (int row = 1; 8 >= row; row++) {
            for (int col = 1; 8 >= col; col++) {
                final ChessPiece piece = this.board.getPiece(new ChessPosition(row, col));
                if (null != piece && teamColor == piece.getTeamColor()) {
                    moves.addAll(this.validMoves(new ChessPosition(row, col)));
                }
            }
        }

        return moves;
    }

    public Collection<ChessMove> getAllPossibleMoves(final TeamColor teamColor) {
        final List<ChessMove> moves = new ArrayList<>();
        for (int row = 1; 8 >= row; row++) {
            for (int col = 1; 8 >= col; col++) {
                final ChessPiece piece = this.board.getPiece(new ChessPosition(row, col));
                if (null != piece && teamColor == piece.getTeamColor()) {
                    moves.addAll(piece.pieceMoves(board, new ChessPosition(row, col)));
                }
            }
        }

        return moves;
    }

    public ChessPosition getKingsPosition(final TeamColor teamColor) {
        for (int row = 1; 8 >= row; row++) {
            for (int col = 1; 8 >= col; col++) {
                final ChessPiece piece = this.board.getPiece(new ChessPosition(row, col));
                if (null != piece && teamColor == piece.getTeamColor() && ChessPiece.PieceType.KING == piece.getPieceType()) {
                    return new ChessPosition(row, col);
                }
            }
        }
        return new ChessPosition(0, 0);
    }

    public boolean keepsCheck(final ChessMove move) {
        final TeamColor clr = board.getPiece(move.getStartPosition()).getTeamColor();
        //Simulate the move
        final ChessGame simGame = new ChessGame();
        simGame.setBoard(board);
        //Manually set the board
        simGame.forceMakeMove(move.getEndPosition().getRow(), move.getEndPosition().getColumn(), move.getStartPosition().getRow(), move.getStartPosition().getColumn(), move.getPromotionPiece());
        //We have an inf loop
        return simGame.isInCheck(clr);
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(final TeamColor teamColor) {
        //Collect all the moves for the opposite team

        TeamColor opponent = TeamColor.WHITE;
        final ChessPosition kingPosition = this.getKingsPosition(teamColor);
        if (TeamColor.WHITE == teamColor) {
            opponent = TeamColor.BLACK;
        }

        final List<ChessMove> moves = (List) this.getAllPossibleMoves(opponent);
        for (final ChessMove move : moves) {
            if (move.getEndPosition().equals(kingPosition)) {
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
    public boolean isInCheckmate(final TeamColor teamColor) {
        if (this.isInCheck(teamColor)) {
            final List moves = (List) this.getAllPossibleMovesStrict(teamColor); //I need to use valid moves here
            return moves.isEmpty();
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
    public boolean isInStalemate(final TeamColor teamColor) {
        final List moves = (List) this.getAllPossibleMovesStrict(teamColor);
        return moves.isEmpty() && !this.isInCheck(teamColor);
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(final ChessBoard board) {
        //Go Piece by piece
        final ChessBoard temp = new ChessBoard();
        for (int row = 1; 8 >= row; row++) {
            for (int col = 1; 8 >= col; col++) {
                final ChessPiece piece = board.getPiece(new ChessPosition(row, col));
                if (null != piece) {
                    temp.addPiece(new ChessPosition(row, col), new ChessPiece(piece.pieceColor, piece.type));
                }
            }
        }
        this.board = temp;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }
}
