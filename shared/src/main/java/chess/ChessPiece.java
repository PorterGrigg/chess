package chess;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);

        switch (type) { //using the enum to switch between and redirect the getPossbleMoves to each piece type
            case KING:
                PieceMovesCalculator kingCalc = new KingMovesCalculator(); //because the getPossibleMoves function is for objects it requires me to make a new object to call the function on it?
                return kingCalc.getPossibleMoves(board, myPosition, this);
            case QUEEN:
                PieceMovesCalculator queenCalc = new QueenMovesCalculator();
                return queenCalc.getPossibleMoves(board, myPosition, this);
            case BISHOP:
                PieceMovesCalculator bishCalc = new BishopMovesCalculator();
                return bishCalc.getPossibleMoves(board, myPosition, this);
            case KNIGHT:
                PieceMovesCalculator kniCalc = new KnightMovesCalculator();
                return kniCalc.getPossibleMoves(board, myPosition, this);
            case ROOK:
                PieceMovesCalculator rookCalc = new RookMovesCalculator();
                return rookCalc.getPossibleMoves(board, myPosition, this);
            case PAWN:
                PieceMovesCalculator pawnCalc = new PawnMovesCalculator();
                return pawnCalc.getPossibleMoves(board, myPosition, this);
        }
        return List.of(); //requires a list to be returned in case none of above
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }
}
