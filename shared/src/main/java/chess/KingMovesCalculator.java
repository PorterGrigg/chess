package chess;

import java.util.ArrayList;
import java.util.List;

public class KingMovesCalculator implements PieceMovesCalculator {
    @Override
    public List<ChessMove> getPossibleMoves(ChessBoard squares, ChessPosition start, ChessPiece piece){
        List<ChessMove> moves = new ArrayList<>();
        //define possible movement directions (vectors): for King it is all directions
        //define possible movement distances: for King it is 1 space

        //create a list of possible vectors
        int[][] directions = {{1, 1}, {1, 0}, {0, 1}, {-1, 1}, {1, -1}, {-1, -1}, {-1, 0}, {0, -1}};

        //for each vector in the list
        for(int[] dir: directions){
            int rowAdvance = dir[0];
            int colAdvance = dir[1];
            int presRow = start.getRow();
            int presCol = start.getColumn();

            int nextRow = presRow + rowAdvance;
            int nextCol = presCol + colAdvance;
            ChessPosition nextPos = new ChessPosition(nextRow, nextCol);

            //no need for a while loop
            if (nextRow < 9 && nextCol < 9 && nextRow > 0 && nextCol > 0) {//if the next position is in the board bounds (else stop the while loop)
                if (squares.getPiece(nextPos) != null) { //if there is a piece present here

                    if(squares.getPiece(nextPos).getTeamColor() == piece.getTeamColor()){ //if team colors are equal this is not a valid move
                        break;
                    }
                    //what to do to capture?
                }
                moves.add(new ChessMove(start, nextPos, null));

            }
            //else {
                //endCondition = true;
            //}

            presRow = nextRow; //got infinite loop because forgot to update these variables
            presCol = nextCol;
        }


        return moves;
    }
}
