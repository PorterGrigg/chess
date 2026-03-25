package client;

import chess.ChessGame;
import chess.ChessPiece;
import requests.*;
import results.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import static ui.EscapeSequences.*;

public class UserREPL {

    private String userName;
    private String userAuthToken;
    private final ServerFacade serverFacade;
    private State state;
    private Map<String, Integer> gameLookup;
    //private String serverURL;

    public UserREPL(ServerFacade givenServerFacade, String givenUserName, String givenUserAuthToken, State givenState) throws ResponseException {
        serverFacade = givenServerFacade;
        userAuthToken = givenUserAuthToken;
        userName = givenUserName;
        state = givenState;
        gameLookup = new HashMap<>();
    }

    public void run() {
        System.out.println(String.format("♕ You are now logged in as %s!", userName));
        System.out.print(help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine(); //move to the next line and wait for input

            try {
                result = eval(line);
                if (state == State.LOGGEDOUT){
                    break; //stop the loop if they log out
                }
                System.out.print(BLUE + result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

//
//    public void notify(Notification notification) {
//        System.out.println(RED + notification.message());
//        printPrompt();
//    }

    private void printPrompt() { //this is what is printed before start listening for user input
        System.out.print("\n" + RESET + ">>> " + GREEN);
    }


    public String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help"; //default if no input is help
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);

            return switch (cmd) { //will return whatever comes out of the function that is called
                case "create" -> create(params);
                case "list" -> list(params);
                case "join" -> join(params);
                case "observe" -> observe(params);
                case "logout" -> logout(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String logout(String... params) throws ResponseException {

        LogoutRequest request = new LogoutRequest(userAuthToken);
        serverFacade.logoutUser(request);

        state = State.LOGGEDOUT;

        return  String.format("%s should never see this I think", userName);
    }

    public String create(String... params) throws ResponseException {
        if (params.length == 1) { //username and password
            String gameName = params[0];

            CreateRequest request = new CreateRequest(userAuthToken, gameName);
            CreateResult result = serverFacade.createGame(request);

            gameID = result.gameID();
            userAuthToken = result.authToken();

            //this will return after the user logs out
            return  String.format("Your game \"%s!\" has been created!", gameName);
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: <yourname>");
    }

    public String list(String... params) throws ResponseException {
        if (params.length == 1) { //username and password
            String gameName = params[0];

            CreateRequest request = new CreateRequest(userAuthToken, gameName);
            CreateResult result = serverFacade.createGame(request);

            gameID = result.gameID();
            userAuthToken = result.authToken();

            //this will return after the user logs out
            return  String.format("Your game \"%s!\" has been created!", gameName);
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: <yourname>");
    }

    public String help() {
        return """
                - create <NAME> - create a game
                - list - list all games
                - join <ID> [WHITE|BLACK]- join a game
                - observe <ID> - observe a game
                - logout
                - quit
                """;
    }

    private void assertSignedIn() throws ResponseException {
        if (state == State.SIGNEDOUT) {
            throw new ResponseException(ResponseException.Code.ClientError, "You must sign in");
        }
    }
//    public static void main(String[] args) {
//
//
//        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
//        System.out.println("♕ 240 Chess Client: " + piece);
//    }
}
