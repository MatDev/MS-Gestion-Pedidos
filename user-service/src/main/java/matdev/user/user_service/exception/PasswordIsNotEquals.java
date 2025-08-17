package matdev.user.user_service.exception;

public class PasswordIsNotEquals extends RuntimeException {
    public PasswordIsNotEquals(String message) {
        super(message);
    }
}
