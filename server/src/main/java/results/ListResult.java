package results;

import model.GameData;

public record ListResult(String error, String message, GameData games) {
} //will the way game data automatically prints out match the specifications?
