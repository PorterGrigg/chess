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

    public ChessGame.TeamColor teamTurn;
    public ChessBoard gameBoard = new ChessBoard();

    public ChessGame() {

    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        if(teamTurn == TeamColor.BLACK){
            teamTurn = TeamColor.WHITE;
        }
        else{
            teamTurn = TeamColor.BLACK;
        }
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Moves any piece on a fake board without validating the move
     *
     * @param fakeBoard the fake board where we are moving
     * @param fakeMove move to make
     */
    private void moveWithoutValidate(ChessBoard fakeBoard, ChessMove fakeMove) {
        ChessPosition startPos = fakeMove.getStartPosition();
        ChessPosition endPos = fakeMove.getEndPosition();
        ChessPiece fakePiece = fakeBoard.getPiece(startPos);

        //set old position to null
        fakeBoard.addPiece(startPos, null);

        //set new position to new
        fakeBoard.addPiece(endPos, fakePiece);
    }

    /**
     * Determines if the given team is in check on the fake board (real board check is below)
     * @param board fake board
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean fakeBoardIsInCheck(ChessBoard board, TeamColor teamColor) {
        //pass in the team color and will tell you if the king is in check
        boolean inCheck = false;

        //find the king
        ChessPosition kingPos = findKing((teamColor));

        //see if there are any other pieces that put the king in check (check each direction that an attack could come from or check the possible moves of each opposing team piece to see if the end position will match the kings position)
        ChessPosition currPosition ;
        ChessPiece currPiece;
        for(int row = 1; row<9; row++){ //each row
            for(int col = 1; col< 9; col++){ //each column
                currPosition = new ChessPosition(row, col);
                currPiece = board.getPiece(currPosition);
                if (currPiece == null){ //can't call piece. anything on a null object
                }
                else if(currPiece.getTeamColor() == teamColor){
                }
                else{
                    if(checkPositionMatch(kingPos, currPiece, currPosition)){
                        inCheck = true;
                    }
                }
            }
        }

        return inCheck;
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        Collection<ChessMove> validMoves = new ArrayList<>();

        //check if there is even a piece
        ChessPiece currPiece = gameBoard.getPiece(startPosition);
        if(currPiece != null){
            Collection<ChessMove> allMoves = currPiece.pieceMoves(this.gameBoard, startPosition);
            TeamColor teamColor = currPiece.getTeamColor();

            //copy the board for each possible move, make the move and then see if the king is in check
            for(ChessMove currMove: allMoves){
                ChessBoard potentialBoard = this.gameBoard.clone();

                //move the king piec to the potential position in the fake board
                moveWithoutValidate(potentialBoard, currMove);

                if (!fakeBoardIsInCheck(potentialBoard, teamColor)){ //if it is not in check after moving to this spot on the potential board then the king has escaped
                    validMoves.add(currMove);
                }
            }
        }
        return validMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        //update the state of the board, move one piece from one position to another
        ChessPosition startPosition = move.getStartPosition();
        ChessPosition endPosition = move.getEndPosition();
        ChessPiece pieceToMove = gameBoard.getPiece(startPosition);

        //check if there is even a piece if not throw an exception
        if(pieceToMove == null){
            throw new InvalidMoveException("No piece at start position");
        }

        //check the move against valid moves and if not on the list then throw exception
        Collection<ChessMove> validMoves = validMoves(startPosition);
        if(!validMoves.contains(move)){
            throw new InvalidMoveException("Not a valid move");
        }

        //if it makes it through all the exceptions then it is a valid move and you can make that move
        //set old position to null
        this.gameBoard.addPiece(startPosition, null);

        //includes updating promotion too
        if(move.getPromotionPiece() != null){
            this.gameBoard.addPiece(endPosition, new ChessPiece(pieceToMove.getTeamColor(), move.getPromotionPiece()));
        }
        else{
            this.gameBoard.addPiece(endPosition, pieceToMove);
        }

    }

    /**
     * Determines the position of the team's king
     *
     * @param teamColor which team to check for check
     * @return ChessPosition
     */
    private ChessPosition findKing(TeamColor teamColor) {
        ChessPosition kingPosition = new ChessPosition(0, 0);
        ChessPosition currPosition ;
        ChessPiece currPiece;

        for(int row = 1; row<9; row++){ //each row
            for(int col = 1; col< 9; col++){ //each column
                currPosition = new ChessPosition(row, col);
                currPiece = this.gameBoard.getPiece(currPosition);
                if (currPiece == null){ //can't call piece. anything on a null object
                }
                else{
                    if(currPiece.getTeamColor() == teamColor && currPiece.getPieceType() == ChessPiece.PieceType.KING){
                        kingPosition = currPosition;
                    }
                }
            }
        }
        return kingPosition;
    }



    /**
     * Determines if the given piece can reach the specified position
     *
     * @param positionChecked the position to check
     * @param piece           the piece we are checking
     * @param startPosition   the position the piece starts at
     * @return True if the specified piece can reach the specified position
     */
    private boolean checkPositionMatch(ChessPosition positionChecked, ChessPiece piece, ChessPosition startPosition){
        boolean matched = false;
        Collection<ChessMove> pieceMoves = piece.pieceMoves(this.gameBoard, startPosition);
        ChessPosition endPos;
        for(ChessMove currMove: pieceMoves){
            endPos = currMove.getEndPosition();
            if (endPos.equals(positionChecked)){ //remember to use .equals for objects like positions
                matched = true;
            }
        }
        return matched;
    }



    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        //pass in the team color and will tell you if the king is in check
        boolean inCheck = false;

        //find the king
        ChessPosition kingPos = findKing((teamColor));

        //see if there are any other pieces that put the king in check (check each direction that an attack could come from or check the possible moves of each opposing team piece to see if the end position will match the kings position)
        ChessPosition currPosition ;
        ChessPiece currPiece;
        for(int row = 1; row<9; row++){ //each row
            for(int col = 1; col< 9; col++){ //each column
                currPosition = new ChessPosition(row, col);
                currPiece = this.gameBoard.getPiece(currPosition);
                if (currPiece == null){ //can't call piece. anything on a null object
                }
                else if(currPiece.getTeamColor() == teamColor){
                }
                else{
                    if(checkPositionMatch(kingPos, currPiece, currPosition)){
                        inCheck = true;
                    }
                }
            }
        }

        return inCheck;
    }

    /**
     * Determines if the king can escape Check
     *
     * @param teamColor which team to check for checkmate
     * @return True if the king can escape
     */
    private boolean kingCanEscape(TeamColor teamColor, ChessPosition kingPosition) {
        boolean kingEscapes = false;
        ChessPiece kingPiece = this.gameBoard.getPiece(kingPosition);

        //check all the king's moves
        Collection<ChessMove> kingMoves = kingPiece.pieceMoves(this.gameBoard, kingPosition);
        ChessPosition endPos;
        for(ChessMove currMove: kingMoves){
            endPos = currMove.getEndPosition();
            ChessBoard potentialBoard = this.gameBoard.clone();

            //**move the king piec to the potential position in the fake board**

            if (!fakeBoardIsInCheck(potentialBoard, teamColor)){ //if it is not in check after moving to this spot on the potential board then the king has escaped
                kingEscapes = true;
            }
        }



        //check the moves of all the other pieces of the same team


        return kingEscapes;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        //tells when game is over
        boolean inCheckmate = false;
        //current color's king is in check and there are no moves that will move him out of check and there are no moves his other pieces can make to remove him from check

        ChessPosition kingPos = findKing((teamColor));

        if (isInCheck(teamColor)) {


        }


        return inCheckmate;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        //king is NOT in check but there are no moves he or other pieces can make that will not put him in check
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        //get the piece at each position and set that to be equal on our board

        ChessPosition currPosition ;
        for(int row = 1; row<9; row++){ //each row
            for(int col = 1; col< 9; col++){ //each column
                currPosition = new ChessPosition(row, col);
                this.gameBoard.addPiece(currPosition, board.getPiece(currPosition));
            }
        }
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return gameBoard;
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return teamTurn == chessGame.teamTurn && Objects.equals(gameBoard, chessGame.gameBoard);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamTurn, gameBoard);
    }
}
