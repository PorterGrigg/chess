package client;

import java.util.Arrays;

public class GameREPL {
//    public String eval(String input) {
//        try {
//            //convert the input to lower case and seperate the arguments by spaces
//            String[] tokens = input.toLowerCase().split(" ");
//            //extract the first token of the input (which is the command from the user)
//            String cmd = (tokens.length > 0) ? tokens[0] : "help"; //default if no input is help
//            //extract everything else after the command which are the arguments for the command
//            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
//
//            //reditect the command to the correct response
//            return switch (cmd) { //will return whatever comes out of the function that is called
//                case "signin" -> signIn(params);
//                case "rescue" -> rescuePet(params);
//                case "list" -> listPets();
//                case "signout" -> signOut();
//                case "adopt" -> adoptPet(params);
//                case "adoptall" -> adoptAllPets();
//                case "quit" -> "quit";
//                default -> help();
//            };
//        } catch (ResponseException ex) {
//            return ex.getMessage();
//        }
//    }
}
