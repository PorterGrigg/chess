package client;

import chess.ChessGame;

import model.*;
import requests.*;
import results.*;

import java.util.*;

import static ui.EscapeSequences.*;

public class UserREPL {

    private final String userName;
    private final String userAuthToken;
    private final String userPassword;
    private final ServerFacade serverFacade;
    private State state;
    private final Map<Integer, Integer> gameLookup;
    //private String serverURL;

    public UserREPL(ServerFacade givenServerFacade, String givenUserName, String givenUserAuthToken,
                    String givenPassword, State givenState) throws ResponseException {
        serverFacade = givenServerFacade;
        userAuthToken = givenUserAuthToken;
        userName = givenUserName;
        state = givenState;
        gameLookup = new HashMap<>();
        userPassword = givenPassword;
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
            serverFacade.createGame(request);

            //this will return after the user logs out
            return  String.format("Your game \"%s!\" has been created!", gameName);
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: <yourname>");
    }

    public String observe(String... params) throws ResponseException {
        if (params.length == 1) { //username and password
            int gameNum = Integer.parseInt(params[0]);

            int gameID = getGameID(gameNum);

            //change state machine
            state = State.OBSERVEGAME;
            new GameREPL(serverFacade, userName, userAuthToken, state, ChessGame.TeamColor.WHITE, gameID).run();

            //this will return after the user quits the game
            state = State.LOGGEDIN;
            return  String.format("Feel free to observe another game %s!", userName);
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: <gameNumber>");
    }

    public String join(String... params) throws ResponseException {
        if (params.length == 2) { //username and password
            int gameNum = Integer.parseInt(params[0]);
            String playerColor = params[1];

            int gameID = getGameID(gameNum);

            ChessGame.TeamColor translatedPlayerColor = getPlayerColor(playerColor);

            JoinRequest request = new JoinRequest(userAuthToken, translatedPlayerColor, gameID);
            serverFacade.joinGame(request);

            //change state machine
            state = State.INGAME;
            new GameREPL(serverFacade, userName, userAuthToken, state, translatedPlayerColor, gameID).run();

            //this will return after the user quits the game
            state = State.LOGGEDIN;
            return  String.format("Hope you play again soon %s!", userName);
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: <gameNumber> <color>");
    }

    public String list(String... params) throws ResponseException {

        ListRequest request = new ListRequest(userAuthToken);
        ListResult result = serverFacade.listGames(request);

        ArrayList<GameData> games = result.games();

        return  updateGameLookupandGetString(games);
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

    private String updateGameLookupandGetString (ArrayList<GameData> games){
        int index = 1;
        String gameListString = "";

        for (GameData game : games){
            String white;
            String black;
            if (game.whiteUsername() == null){
                white = "None";
            }
            else{
                white = game.whiteUsername();
            }
            if (game.blackUsername() == null){
                black = "None";
            }
            else{
                black = game.blackUsername();
            }

            gameListString = gameListString + String.format("%d. %s — White Player: %s, Black Player: %s %n",
                    index,
                    game.gameName(),
                    white,
                    black
            );

            gameLookup.put(index, game.gameID());
            index++;
        }
        return gameListString;
    }

    private int getGameID(int gameNum) throws ResponseException{
        int gameID;
        if (gameLookup.containsKey(gameNum)){
            gameID = gameLookup.get(gameNum);
        }
        else{
            throw new ResponseException(ResponseException.Code.ClientError, "Error: number out of range");
        }
        return gameID;
    }

    private ChessGame.TeamColor getPlayerColor(String input) throws ResponseException{
        ChessGame.TeamColor playerColor;
        String color = input.toLowerCase();
        if (color.equals("white")){
            playerColor = ChessGame.TeamColor.WHITE;
        }
        else if (color.equals("black")){
            playerColor = ChessGame.TeamColor.BLACK;
        }
        else{
            throw new ResponseException(ResponseException.Code.ClientError, "Error: invalid color");
        }
        return playerColor;
    }

    private void printPrompt() { //this is what is printed before start listening for user input
        System.out.print("\n" + RESET + ">>> " + GREEN);
    }

}
