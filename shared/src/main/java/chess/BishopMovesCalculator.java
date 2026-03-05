package chess;

import java.util.ArrayList;
import java.util.List;

public class BishopMovesCalculator implements PieceMovesCalculator { //moves diagonally until blocked (or captures)
    @Override
    public List<ChessMove> getPossibleMoves(ChessBoard squares, ChessPosition start, ChessPiece piece){
        List<ChessMove> moves = new ArrayList<>();
        //define possible movement directions, diagonals
        //define possible movement distances, for Bishop it is unlimited (to end of board or to another piece)

        //create a list of possible vectors
        int[][] directions = {{1, 1}, {-1, 1}, {1, -1}, {-1, -1}};

        //check each vector direction and add possible moves to the list as they are validated
        for (int[] dir : directions) {
            SingleDirectionMovesCalculator.addSingleDirectionMoves(squares, start, piece, moves, dir[0], dir[1]);
        }

        return moves;
    }
}
