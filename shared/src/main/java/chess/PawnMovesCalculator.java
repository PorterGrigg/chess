package chess;

import java.util.ArrayList;
import java.util.List;

public class PawnMovesCalculator implements PieceMovesCalculator {
    @Override
    public List<ChessMove> getPossibleMoves(ChessBoard squares, ChessPosition start, ChessPiece piece){ //for pawn also need to consider the promotion to queen
        List<ChessMove> moves = new ArrayList<>();
        //define possible movements: for Pawn can move forward one space anytime there is not a same team piece
        //                               can move forward diagonally one space if there is an opposing team piece
        //                               can move forward two spaces if in starting position
        //

        //Normal movement
        int rowAdvance = 0;
        int colAdvance = 0;
        int[] colOptions = {-1, 1};
        //advance different if black or white
        if(piece.getTeamColor() == ChessGame.TeamColor.WHITE){
            rowAdvance = 1;
        }
        else { //black moves down the board
            rowAdvance = -1;
        }

        int presRow = start.getRow();
        int presCol = start.getColumn();
        int nextRow = presRow + rowAdvance;
        int nextCol = presCol + colAdvance; //no column advance for normal movement
        ChessPosition nextPos = new ChessPosition(nextRow, nextCol);

        if (nextRow < 8 && nextCol < 9 && nextRow > 1 && nextCol > 0) { //less than 8 and greater than 1 here because if reach 8 then a promotion would need to happen
            if (squares.getPiece(nextPos) == null) { //can only move forward if no piece in front
                moves.add(new ChessMove(start, nextPos, null));
            }
        }

        //Promotion necessary
        if ((piece.getTeamColor() == ChessGame.TeamColor.WHITE && nextRow == 8) || (piece.getTeamColor() == ChessGame.TeamColor.BLACK && nextRow == 1) ){
            if (squares.getPiece(nextPos) == null) { //if there is not a piece present here
                moves.add(new ChessMove(start, nextPos, ChessPiece.PieceType.QUEEN));
                moves.add(new ChessMove(start, nextPos, ChessPiece.PieceType.ROOK));
                moves.add(new ChessMove(start, nextPos, ChessPiece.PieceType.BISHOP));
                moves.add(new ChessMove(start, nextPos, ChessPiece.PieceType.KNIGHT));
            }
        }





        //diagonal capture
        for(int option: colOptions){
            colAdvance = option;
            nextCol = presCol + colAdvance;
            nextPos = new ChessPosition(nextRow, nextCol);

            if (nextRow < 8 && nextCol < 9 && nextRow > 1 && nextCol > 0) { //less than 8 and greater than 1 here because if reach 8 then a promotion would need to happen
                if(squares.getPiece(nextPos) != null && squares.getPiece(nextPos).getTeamColor() != piece.getTeamColor()){
                    moves.add(new ChessMove(start, nextPos, null));
                }
            }

            //Promotion and diagonal capture
            if (nextCol<9 && nextCol>0) { //within game board
                if ((piece.getTeamColor() == ChessGame.TeamColor.WHITE && nextRow == 8) || (piece.getTeamColor() == ChessGame.TeamColor.BLACK && nextRow == 1)) { //check that it will promote
                    if(squares.getPiece(nextPos) != null && squares.getPiece(nextPos).getTeamColor() != piece.getTeamColor()) { //if there is an opposing piece here
                        moves.add(new ChessMove(start, nextPos, ChessPiece.PieceType.QUEEN));
                        moves.add(new ChessMove(start, nextPos, ChessPiece.PieceType.ROOK));
                        moves.add(new ChessMove(start, nextPos, ChessPiece.PieceType.BISHOP));
                        moves.add(new ChessMove(start, nextPos, ChessPiece.PieceType.KNIGHT));
                    }
                }
            }
        }

        //start movement
        if ((piece.getTeamColor() == ChessGame.TeamColor.WHITE && presRow == 2) || (piece.getTeamColor() == ChessGame.TeamColor.BLACK && presRow == 7) ){ //in start position and can move 2 if no pieces in front
            if(squares.getPiece(nextPos) == null) {
                if(piece.getTeamColor() == ChessGame.TeamColor.WHITE){
                    rowAdvance = 2;
                }
                else { //black moves down the board
                    rowAdvance = -2;
                }

                //presRow = start.getRow();
                //presCol = start.getColumn();
                nextRow = presRow + rowAdvance;
                nextCol = presCol;//no column advance for normal movement
                nextPos = new ChessPosition(nextRow, nextCol);

                if (squares.getPiece(nextPos) == null) { //can only move forward if no piece in front
                    moves.add(new ChessMove(start, nextPos, null));
                }

                //first move capture
                if(squares.getPiece(nextPos) != null && squares.getPiece(nextPos).getTeamColor() != piece.getTeamColor()){
                    moves.add(new ChessMove(start, nextPos, null));
                }
            }
        }
        return moves;
    }
}
