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

    public GameREPL(ServerFacade givenServerFacade, String givenUserName, String givenUserAuthToken, State givenState) throws ResponseException {
        serverFacade = givenServerFacade;
        userAuthToken = givenUserAuthToken;
        userName = givenUserName;
        state = givenState;
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
            String line = scanner.nextLine(); //move to the next line and wait for input

            try {
                result = eval(line);
                if (state == State.LOGGEDOUT) {
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

        return  String.format("%s should never see this I think", userName);
    }

    private void drawBoard(){

    }

    public String help() {
        return """
                - quit
                - refresh (not implemented)
                """;
    }
}