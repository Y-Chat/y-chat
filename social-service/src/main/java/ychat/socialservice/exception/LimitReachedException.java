package ychat.socialservice.exceptions;

public class LimitReachedException extends Exception {
    public LimitReachedException(String message) {
        super(message);
    }
}
