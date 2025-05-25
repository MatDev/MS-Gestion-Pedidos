package matdev.user.user_service.utils;

public final class LogSanitizer {
    private LogSanitizer() {}
    public static String clean(String input) {
        return input == null ? null : input.replaceAll("[\\n\\r\\t]", "_");
    }
}
