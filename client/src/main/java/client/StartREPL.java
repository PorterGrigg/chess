package client;

import chess.ChessGame;
import chess.ChessPiece;
import requests.*;
import results.*;

import java.util.Arrays;
import java.util.Scanner;
import static ui.EscapeSequences.*;

public class StartREPL {

    private String userName = null;
    private String userAuthToken = null;
    private final ServerFacade serverFacade;
    private State state = State.LOGGEDOUT;
    //private String serverURL;

    public StartREPL(String givenServerUrl) throws ResponseException {
        serverFacade = new ServerFacade(givenServerUrl);
        //serverURL = givenServerUrl;
    }

    public void run() {
        System.out.println("♕ Welcome to Chess! Login to start:)");
        System.out.print(help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
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
            //convert the input to lower case and seperate the arguments by spaces
            String[] tokens = input.toLowerCase().split(" ");
            //extract the first token of the input (which is the command from the user)
            String cmd = (tokens.length > 0) ? tokens[0] : "help"; //default if no input is help
            //extract everything else after the command which are the arguments for the command
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);

            //reditect the command to the correct response
            return switch (cmd) { //will return whatever comes out of the function that is called
                case "register" -> register(params);
                case "login" -> login(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String register(String... params) throws ResponseException {
        if (params.length == 3) { //username and password

            String username = params[0];
            String password = params[1];
            String email = params[2];

            RegisterRequest request = new RegisterRequest(username, password, email);
            RegisterResult result = serverFacade.registerUser(request);

            userName = result.username();
            userAuthToken = result.authToken();

            return  String.format("You are now signed out.", userName);
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: <yourname>");
    }

    public String login(String... params) throws ResponseException {
        if (params.length == 2) { //username and password
            String username = params[0];
            String password = params[1];

            LoginRequest request = new LoginRequest(username, password);
            LoginResult result = serverFacade.loginUser(request);

            userName = result.username();
            userAuthToken = result.authToken();

            //change state machine
            state = State.LOGGEDIN;
            new UserREPL(serverFacade, userName, userAuthToken, state).run();
            //this will return after the user logs out
            return  String.format("Hope you play again soon %s!", userName);
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: <yourname>");
    }

    public String help() {
        return """
                - register <USERNAME> <PASSWORD> <EMAIL>
                - login <USERNAME> <PASSWORD>
                - quit
                """;
    }

    private void assertSignedIn() throws ResponseException {
        if (state == State.LOGGEDOUT) {
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
