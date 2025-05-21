package matdev.user.user_service.exeption;

public class ExpireTokenException extends RuntimeException {
    public ExpireTokenException(String message) {
        super(message);
    }

}
