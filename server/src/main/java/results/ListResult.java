package results;

import model.GameData;

import java.util.ArrayList;

public record ListResult(String error, String message, ArrayList<GameData> games) {
} //will the way game data automatically prints out match the specifications?
