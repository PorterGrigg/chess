package service;

public class UnauthorizedUserException extends RuntimeException {
    public UnauthorizedUserException(String message) {
        super("Error: Unauthorized");
    }
}
