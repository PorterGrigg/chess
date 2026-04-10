package client;

import chess.*;
import client.websocket.ServerMessageHandler;
import client.websocket.ServerToClientMessageHandler;
import client.websocket.WebSocketFacade;
import model.*;
import requests.*;
import results.*;
import ui.EscapeSequences;
import websocket.commands.MakeMoveCommand;
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
                    State givenState, ChessGame.TeamColor givenUserColor, int givenGameID) throws Exception {
        serverFacade = givenServerFacade;
        userAuthToken = givenUserAuthToken;
        userName = givenUserName;
        state = givenState;
        userColor = givenUserColor;
        gameID = givenGameID;
        webSocketFacade = new WebSocketFacade(givenServerFacade.getServerUrl(), new ServerToClientMessageHandler(this));
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

    public void printPrompt() { //this is what is printed before start listening for user input
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
                case "move" -> makeMove(params);
                case "resign" -> resignGame(params);
                case "highlight" -> highlightLegalMoves(params);
                case "quit" -> leaveGame(params);
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
                - move <START POSITION (ex: B1)> <END POSITION> <PROMOTION PIECE (If Applicable)>- make a move
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

    public String makeMove(String... params) throws ResponseException {
        if (state != State.INGAME){
            return "Sorry you are not currently in the game";
        }
        if (params.length == 2) { //start and end
            ChessPosition startPos;
            ChessPosition endPos;
            try {
                //save params
                startPos = getPosition(params[0]);
                endPos = getPosition(params[1]);
            }catch(InvalidMoveException ex){
                throw new ResponseException(ResponseException.Code.ClientError, "Expected: <START POSITION> <END POSITION> <PROMOTION PIECE (If Applicable)>");
            }


            //create chess move
            ChessMove newMove = new ChessMove(startPos, endPos, null);

            //make leave command
            UserGameCommand moveCommand = new MakeMoveCommand(userAuthToken, gameID, newMove);

            //send through facade
            webSocketFacade.sendUserCommand(moveCommand);

            //return from the call in the loop
            return  "Good move!";
        }
        else if (params.length == 3) { //given promotion also
            ChessPosition startPos;
            ChessPosition endPos;
            ChessPiece.PieceType promotion;

            try {
                //save params
                startPos = getPosition(params[0]);
                endPos = getPosition(params[1]);
                promotion = getPromotionPiece(params[2]);
            }catch(InvalidMoveException ex){
                throw new ResponseException(ResponseException.Code.ClientError, "Expected: <START POSITION> <END POSITION> <PROMOTION PIECE (If Applicable)>");
            }

            //create chess move
            ChessMove newMove = new ChessMove(startPos, endPos, promotion);

            //make leave command
            UserGameCommand moveCommand = new MakeMoveCommand(userAuthToken, gameID, newMove);

            //send through facade
            webSocketFacade.sendUserCommand(moveCommand);

            //return from the call in the loop
            return  "Good move!";
        }

        throw new ResponseException(ResponseException.Code.ClientError, "Expected: <START POSITION> <END POSITION> <PROMOTION PIECE (If Applicable)>");
    }

    public String resignGame(String... params) throws ResponseException {
        if (params.length == 0) {

            Scanner scanner = new Scanner(System.in);
            System.out.println("Are you sure you want to resign? (yes/no)");
            printPrompt();
            String input = scanner.nextLine();
            input.toLowerCase();
            if (!input.equals("yes")){
                return "Ok, keep playing your best!";
            }

            //make resign command
            UserGameCommand resignCommand = new UserGameCommand(UserGameCommand.CommandType.RESIGN,
                    userAuthToken, gameID);

            //send through facade
            webSocketFacade.sendUserCommand(resignCommand);

            //change state machine
            state = State.LOGGEDIN; //change to state for UserREPL

            //return from the call in the loop
            return  String.format("You shouldn't see this message from resign game %s!", userName);
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: nothing");
    }

    public String highlightLegalMoves(String... params) throws ResponseException {
        if (params.length == 1) { //username and password

            //get the start pos
            ChessPosition startPos;
            try {
                startPos = getPosition(params[0]);
            }catch(InvalidMoveException ex){
                throw new ResponseException(ResponseException.Code.ClientError, "Expected: <START POSITION> <END POSITION> <PROMOTION PIECE (If Applicable)>");
            }

            //get valid moves
            GameData gameData = getGame();
            ChessGame game = gameData.game();
            Collection<ChessMove> validMoves = game.validMoves(startPos);
            List<ChessPosition> validSpaces = getSpaces(validMoves);

            drawHighlightBoard(validSpaces);
        }
        else{
            throw new ResponseException(ResponseException.Code.ClientError, "Expected: <START POSITION>");
        }
        return "The legal moves are highlighted!";
    }

    private List<ChessPosition> getSpaces(Collection<ChessMove> validMoves){
        List<ChessPosition> spaces = new ArrayList<>();
        for (ChessMove move: validMoves){
            spaces.add(move.getEndPosition());
        }
        return spaces;
    }

    private void drawHighlightBoard(List<ChessPosition> validSpaces) throws ResponseException {
        if (userColor == ChessGame.TeamColor.WHITE) {
            drawHighlightedWhiteBoard(validSpaces);
        }
        else{
            drawHighlightedBlackBoard(validSpaces);
        }
    }

    private void drawHighlightedWhiteBoard(List<ChessPosition> validSpaces) throws ResponseException{
        GameData game = getGame();
        var board = game.game().getBoard();

        System.out.println();
        //rows 8 to 1 (top to bottom)
        for (int row = 8; row >= 1; row--) {
            System.out.print(SET_TEXT_COLOR_BLUE + row + "  " + RESET_TEXT_COLOR); //row label

            //columns 1 to 8 (left to right)
            for (int col = 1; col <= 8; col++) {
                boolean highlighted = false;
                for (ChessPosition pos: validSpaces){
                    if((pos.getColumn() == col) & (pos.getRow() == row)){
                        highlighted = true;
                    }
                }
                printSquare(board, row, col, highlighted);

            }
            System.out.println();
        }
        // Column labels
        System.out.println(SET_TEXT_COLOR_BLUE + "    A   B   C  D   E   F  G   H\n" + RESET_TEXT_COLOR);
    }

    private void drawHighlightedBlackBoard(List<ChessPosition> validSpaces) throws ResponseException{
        GameData game = getGame();
        var board = game.game().getBoard();

        System.out.println();
        //rows 1 to 8 (bottom to top)
        for (int row = 1; row <= 8; row++) {
            System.out.print(SET_TEXT_COLOR_BLUE + row + "  " + RESET_TEXT_COLOR); //row label

            //columns 8 to 1 (right to left)
            for (int col = 8; col >= 1; col--) {
                boolean highlighted = false;
                for (ChessPosition pos: validSpaces){
                    if((pos.getColumn() == col) & (pos.getRow() == row)){
                        highlighted = true;
                    }
                }
                printSquare(board, row, col, highlighted);
            }
            System.out.println();
        }

        // Column labels
        System.out.println(SET_TEXT_COLOR_BLUE + "    H   G   F  E   D   C  B   A\n" + RESET_TEXT_COLOR);
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
                printSquare(board, row, col, false);

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
                printSquare(board, row, col, false);
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

    private void printSquare(ChessBoard board, int row, int col, boolean highlighted){
        ChessPiece piece = board.getPiece(new chess.ChessPosition(row, col));

        String pieceSymbol;
        String squareColor;
        String textColor;

        if (highlighted){
            squareColor = SET_BG_COLOR_RED;
        }
        else if ((row+col) %2 ==0 ){
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
        //System.out.println("Connecting User");
        UserGameCommand connectCommand = new UserGameCommand(UserGameCommand.CommandType.CONNECT,
                userAuthToken, gameID);
        webSocketFacade.sendUserCommand(connectCommand);
    }

    private ChessPosition getPosition(String strPos) throws InvalidMoveException {

        //extract row and column
        char colChar = strPos.charAt(0);
        char rowChar = strPos.charAt(1);



        //convert string to ints
        String colStr = String.valueOf(colChar);
        int col = convertCol(colStr);
        if (col == 0){
            throw new InvalidMoveException("Wrong parameters");
        }
        int row = Character.getNumericValue(rowChar);

        ChessPosition position = new ChessPosition(row, col);

        return position;
    }

    private int convertCol(String colStr){
        int col=0;
        switch(colStr){
            case "a" -> col= 1;
            case "b" -> col= 2;
            case "c" -> col= 3;
            case "d" -> col= 4;
            case "e" -> col= 5;
            case "f" -> col= 6;
            case "g" -> col= 7;
            case "h" -> col= 8;
        }
        return col;
    }

    private ChessPiece.PieceType getPromotionPiece(String promotionStr){
        ChessPiece.PieceType promotion = ChessPiece.PieceType.PAWN;
        promotionStr = promotionStr.toLowerCase();
        switch(promotionStr){
            case "queen" -> promotion = ChessPiece.PieceType.QUEEN;
            case "bishop" -> promotion = ChessPiece.PieceType.BISHOP;
            case "rook" -> promotion = ChessPiece.PieceType.ROOK;
            case "knight" -> promotion = ChessPiece.PieceType.KNIGHT;
        }
        return promotion;
    }
}