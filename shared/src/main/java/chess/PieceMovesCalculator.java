package chess;

import java.util.List;

public interface PieceMovesCalculator {
    List<ChessMove> getPossibleMoves(ChessBoard squares, ChessPosition start, ChessPiece piece); //declaration of the possible moves function that will return a list of moves
}
