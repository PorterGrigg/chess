package client;

import chess.ChessGame;
import chess.ChessPiece;
import model.*;
import requests.*;
import results.*;

import java.util.*;

import static ui.EscapeSequences.*;

public class GameREPL {

    private String userName;
    private String userAuthToken;
    private final ServerFacade serverFacade;
    private State state;
    private int gameID;

    public GameREPL(ServerFacade givenServerFacade, String givenUserName, String givenUserAuthToken,
                    State givenState, int givenGameID) throws ResponseException {
        serverFacade = givenServerFacade;
        userAuthToken = givenUserAuthToken;
        userName = givenUserName;
        state = givenState;
        gameID = givenGameID;
    }

    public void run() {
        if (state == State.INGAME) {
            System.out.println(String.format("♕ You are now playing Chess!")); //%s!", gameName)); implement later
        } else {
            System.out.println(String.format("♕ You are now observing Chess!"));
        }
        System.out.print(help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();

            try { //draw the board at the beginning of every loop?
                drawBoard();
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }

            String line = scanner.nextLine(); //move to the next line and wait for input

            try {
                result = eval(line);
                System.out.print(BLUE + result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    private void printPrompt() { //this is what is printed before start listening for user input
        System.out.print("\n" + RESET + ">>> " + GREEN);
    }


    public String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help"; //default if no input is help
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);

            return switch (cmd) { //will return whatever comes out of the function that is called
                case "update" -> update(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String update(String... params) throws ResponseException { //this is here as a placeholder for later

        LogoutRequest request = new LogoutRequest(userAuthToken);
        serverFacade.logoutUser(request);

        return String.format("%s should never see this I think", userName);
    }

    private void drawBoard() throws ResponseException{
        drawWhiteBoard();
    }

    private void drawWhiteBoard() throws ResponseException{
        GameData game = getGame();
        var board = game.game().getBoard();

        System.out.println();
        // Rows 8 down to 1 (top to bottom)
        for (int row = 8; row >= 1; row--) {
            System.out.print(row + "  "); // Row label

            // Columns 1 to 8 (left to right)
            for (int col = 1; col <= 8; col++) {

                ChessPiece piece = board.getPiece(new chess.ChessPosition(row, col));

                if (piece == null) {
                    System.out.print(". ");
                } else {
                    char symbol = piece.getPieceType().toString().charAt(0);
                    // K,Q,R,B,N,P

                    if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                        symbol = Character.toUpperCase(symbol);
                    } else {
                        symbol = Character.toLowerCase(symbol);
                    }

                    System.out.print(symbol + " ");
                }
            }
            System.out.println();
        }

        // Column labels
        System.out.println("   a b c d e f g h\n");
    }

    public String help() {
        return """
                - quit
                - refresh (not implemented)
                """;
    }

    private GameData getGame() throws ResponseException{

        ListRequest request = new ListRequest(userAuthToken);
        ListResult result = serverFacade.listGames(request);

        ArrayList<GameData> games = result.games();

        for (GameData game : games){
            //System.out.println(String.format("checking game with ID %d for %d ", game.gameID(), gameID));
            if (game.gameID() == gameID){
                return game;
            }
        }
        throw new ResponseException(ResponseException.Code.ServerError, "Error: could not locate current game");
    }
}