package chess;

import java.util.ArrayList;
import java.util.List;

public class QueenMovesCalculator implements PieceMovesCalculator {
    @Override
    public List<ChessMove> getPossibleMoves(ChessBoard squares, ChessPosition start, ChessPiece piece){
        List<ChessMove> moves = new ArrayList<>();
        //define possible movement directions (vectors): for Queen it is all directions
        //define possible movement distances: for Queen it is unlimited (to end of board or to another piece)

        //create a list of possible vectors
        int[][] directions = {{1, 1}, {1, 0}, {0, 1}, {-1, 1}, {1, -1}, {-1, -1}, {-1, 0}, {0, -1}};

        for (int[] dir : directions) {
            SingleDirectionMovesCalculator.addSingleDirectionMoves(squares, start, piece, moves, dir[0], dir[1]);
        }

        return moves;
    }
}
