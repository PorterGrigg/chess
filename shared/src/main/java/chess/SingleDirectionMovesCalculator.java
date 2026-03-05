package chess;

import java.util.List;

public class SingleDirectionMovesCalculator { //helper for Rook, Queen, and Bishop

    public static void addSingleDirectionMoves(ChessBoard squares, ChessPosition start, ChessPiece piece,
                                               List<ChessMove> moves, int rowAdvance, int colAdvance){
        int presRow = start.getRow();
        int presCol = start.getColumn();

        boolean endCondition = false;

        while (!endCondition) { //While I have not hit the end of the board or another piece keep checking spaces
            int nextRow = presRow + rowAdvance;
            int nextCol = presCol + colAdvance;
            ChessPosition nextPos = new ChessPosition(nextRow, nextCol);

            if (nextRow < 9 && nextCol < 9 && nextRow > 0 && nextCol > 0) { //if the next position is in the board bounds
                if (squares.getPiece(nextPos) != null) { //if there is a piece present here
                    endCondition = true; //stop the while loop

                    if (squares.getPiece(nextPos).getTeamColor() == piece.getTeamColor()) {
                        break; //same team so cannot move here
                    }
                }

                moves.add(new ChessMove(start, nextPos, null));
            }
            else {
                endCondition = true;
            }

            presRow = nextRow;
            presCol = nextCol;
        }
    }
}
