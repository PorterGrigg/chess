package server;

import chess.*;

public class ServerMain {
    public static void main(String[] args) {
        Server server = new Server();
        int port = server.run(8080); // instructions start server on port 8080
        System.out.println("Chess Server running on port " + port);

        //This was starter code
        //var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        //System.out.println("♕ 240 Chess Server: " + piece);
    }

}
