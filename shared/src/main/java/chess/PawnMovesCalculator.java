package chess;

import java.util.ArrayList;
import java.util.List;

public class PawnMovesCalculator implements PieceMovesCalculator {
    @Override
    public List<ChessMove> getPossibleMoves(ChessBoard squares, ChessPosition start, ChessPiece piece){
        List<ChessMove> moves = new ArrayList<>();

        return moves; //for pawn also need to consider promotion to Queen, rook, bishop or knight (so if they make it to the opposite side of the board at any point then they promote)

    }
}
