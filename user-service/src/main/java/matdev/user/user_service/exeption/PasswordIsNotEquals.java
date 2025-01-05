package matdev.user.user_service.exeption;

public class PasswordIsNotEquals extends RuntimeException {
    public PasswordIsNotEquals(String message) {
        super(message);
    }
}
