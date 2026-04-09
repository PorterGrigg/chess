package client;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import client.websocket.ServerMessageHandler;
import client.websocket.ServerToClientMessageHandler;
import client.websocket.WebSocketFacade;
import model.*;
import requests.*;
import results.*;
import ui.EscapeSequences;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.util.*;


import static ui.EscapeSequences.*;
import static ui.EscapeSequences.BLACK_PAWN;
import static ui.EscapeSequences.BLACK_ROOK;

public class GameREPL {

    private final String userName;
    private final String userAuthToken;
    private final ServerFacade serverFacade;
    private final WebSocketFacade webSocketFacade;
    private State state;
    private final ChessGame.TeamColor userColor;
    private final int gameID;

    public GameREPL(ServerFacade givenServerFacade, String givenUserName, String givenUserAuthToken,
                    State givenState, ChessGame.TeamColor givenUserColor, int givenGameID) throws ResponseException {
        serverFacade = givenServerFacade;
        userAuthToken = givenUserAuthToken;
        userName = givenUserName;
        state = givenState;
        userColor = givenUserColor;
        gameID = givenGameID;
        webSocketFacade = new WebSocketFacade(givenServerFacade.getServerUrl(), new ServerToClientMessageHandler());
    }

    public void run() {
        if (state == State.INGAME) {
            System.out.println(String.format("♕ You are now playing Chess!")); //%s!", gameName)); implement later
        } else {
            System.out.println(String.format("♕ You are now observing Chess!"));
        }
        System.out.print(help());

        //connect the user to the game
        try {
            connectUser();
        }catch(ResponseException ex){
            var msg = ex.toString();
            System.out.print(msg);
        }

        try { //draw the board at the beginning of entering game
            drawBoard();
        } catch (Throwable e) {
            var msg = e.toString();
            System.out.print(msg);
        }

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {

            printPrompt();
            String line = scanner.nextLine(); //move to the next line and wait for input

            try {
                result = eval(line);
                if ((state != State.INGAME) & (state != State.OBSERVEGAME)){
                    break; //stop the loop if they stop the game
                }
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
                case "redraw" -> redrawBoard(params);
                case "refresh" -> redrawBoard(params);
                case "leave" -> leaveGame(params);
//                case "move" -> makeMove(params);
//                case "resign" -> resignGame(params);
//                case "highlight" -> highlightLegalMoves(params);
//                case "quit" -> leaveGame(params);
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String help() {
        return """
                - redraw - redraw/refresh the board
                - leave - leave the game
                - move - make a move
                - resign - raise the white flag honorably and with dignity
                - highlight - highlight legal moves
                """;
        //- refresh (not implemented)
    }

    public String redrawBoard(String... params) throws ResponseException {
        if (params.length == 0) { //username and password
            drawBoard();
        }
        else{
            throw new ResponseException(ResponseException.Code.ClientError, "Expected: nothing");
        }
        return "Here is the updated board!";
    }

    public String leaveGame(String... params) throws ResponseException {
        if (params.length == 0) { //username and password

            //make leave command
            UserGameCommand leaveCommand = new UserGameCommand(UserGameCommand.CommandType.LEAVE,
                    userAuthToken, gameID);

            //send through facade
            webSocketFacade.sendUserCommand(leaveCommand);

            //change state machine
            state = State.LOGGEDIN; //change to state for UserREPL

            //return from the call in the loop
            return  String.format("You shouldn't see this message from leave game %s!", userName);
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: nothing");
    }


    private void drawBoard() throws ResponseException{
        if (userColor == ChessGame.TeamColor.WHITE) {
            drawWhiteBoard();
        }
        else{
            drawBlackBoard();
        }
    }

    private void drawWhiteBoard() throws ResponseException{
        GameData game = getGame();
        var board = game.game().getBoard();

        System.out.println();
        //rows 8 to 1 (top to bottom)
        for (int row = 8; row >= 1; row--) {
            System.out.print(SET_TEXT_COLOR_BLUE + row + "  " + RESET_TEXT_COLOR); //row label

            //columns 1 to 8 (left to right)
            for (int col = 1; col <= 8; col++) {
                printSquare(board, row, col);

            }
            System.out.println();
        }
        // Column labels
        System.out.println(SET_TEXT_COLOR_BLUE + "    A   B   C  D   E   F  G   H\n" + RESET_TEXT_COLOR);
    }

    private void drawBlackBoard() throws ResponseException{
        GameData game = getGame();
        var board = game.game().getBoard();

        System.out.println();
        //rows 1 to 8 (bottom to top)
        for (int row = 1; row <= 8; row++) {
            System.out.print(SET_TEXT_COLOR_BLUE + row + "  " + RESET_TEXT_COLOR); //row label

            //columns 8 to 1 (right to left)
            for (int col = 8; col >= 1; col--) {
                printSquare(board, row, col);
            }
            System.out.println();
        }

        // Column labels
        System.out.println(SET_TEXT_COLOR_BLUE + "    H   G   F  E   D   C  B   A\n" + RESET_TEXT_COLOR);
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

    private void printSquare(ChessBoard board, int row, int col){
        ChessPiece piece = board.getPiece(new chess.ChessPosition(row, col));

        String pieceSymbol;
        String squareColor;
        String textColor;

        if ((row+col) %2 ==0 ){
            squareColor = SET_BG_COLOR_DARK_BROWN;
        }
        else{
            squareColor = SET_BG_COLOR_LIGHT_BROWN;
        }

        if (piece == null) {
            textColor = squareColor;
            pieceSymbol = EMPTY;
        } else {
            ChessPiece.PieceType pieceType = piece.getPieceType();

            if (piece.getTeamColor() == ChessGame.TeamColor.WHITE){
                textColor = SET_TEXT_COLOR_WHITE;
            }
            else {
                textColor = SET_TEXT_COLOR_BLACK;
            }
            switch(pieceType){
                case KING: pieceSymbol = BLACK_KING;
                    break;
                case QUEEN: pieceSymbol = BLACK_QUEEN;
                    break;
                case BISHOP: pieceSymbol = BLACK_BISHOP;
                    break;
                case KNIGHT: pieceSymbol = BLACK_KNIGHT;
                    break;
                case ROOK: pieceSymbol = BLACK_ROOK;
                    break;
                case PAWN: pieceSymbol = BLACK_PAWN;
                    break;
                default: pieceSymbol = EMPTY;
            }
        }
        System.out.print(squareColor + textColor + pieceSymbol + EscapeSequences.RESET_BG_COLOR);
        //System
    }

    private void connectUser() throws ResponseException {
        UserGameCommand connectCommand = new UserGameCommand(UserGameCommand.CommandType.CONNECT,
                userAuthToken, gameID);
        webSocketFacade.sendUserCommand(connectCommand);
    }
}