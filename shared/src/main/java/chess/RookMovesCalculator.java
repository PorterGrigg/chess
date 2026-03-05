package chess;

import java.util.ArrayList;
import java.util.List;

public class RookMovesCalculator implements PieceMovesCalculator {
    @Override
    public List<ChessMove> getPossibleMoves(ChessBoard squares, ChessPosition start, ChessPiece piece){
        List<ChessMove> moves = new ArrayList<>();
        //define possible movement directions, for Rook it is horizontal or vertical direcions
        //define possible movement distances, for Rook it is unlimited (to end of board or to another piece)

        //create a list of possible vector
        int[][] directions = {{1, 0}, {0, 1}, {-1, 0}, {0, -1}};

        //each in the list
        for (int[] dir : directions) {
            SingleDirectionMovesCalculator.addSingleDirectionMoves(squares, start, piece, moves, dir[0], dir[1]);
        }

        return moves;
    }
}
