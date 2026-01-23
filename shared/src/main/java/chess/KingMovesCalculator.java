package chess;

import java.util.ArrayList;
import java.util.List;

public class KingMovesCalculator implements PieceMovesCalculator {
    @Override
    public List<ChessMove> getPossibleMoves(ChessBoard squares, ChessPosition start, ChessPiece piece){
        List<ChessMove> moves = new ArrayList<>();

        //define possible movement directions (vectors): for Bishop it is (1, 1), (-1, 1), (1, -1), (-1, -1)
        //define possible movement distances: for Bishop it is unlimited (to end of board or to another piece)
        //check each vector direction and add possible moves to the list as they are validated


        //create a list of possible vectors
        int[][] directions = {{1, 1}};
        //for each vector in the list
        //(1, 1) vector
        //While I have not hit the end of the board or another piece keep checking spaces
        //if the next position is in the board bounds (else stop the while loop)
        //add the move
        //if there is a piece present here
        //stop the while loop

        return moves;

    }
}
