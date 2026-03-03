package results;

public record RegisterResult(String error, String message, String username, String authToken) {
}
