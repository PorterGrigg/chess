package service;

public class AlreadyTakenException extends RuntimeException {
    public AlreadyTakenException(String message) {
        super("Error: username already taken");
    }
}
