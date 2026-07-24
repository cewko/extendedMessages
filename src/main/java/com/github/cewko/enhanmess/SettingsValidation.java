package com.github.cewko.enhanmess;

public final class SettingsValidation {
    private SettingsValidation() {
    }

    public static void validateDelaySeconds(int seconds) {
        if (seconds < Reference.MIN_DELAY_SECONDS
                || seconds > Reference.MAX_DELAY_SECONDS) {
            throw new IllegalArgumentException(
                "Delay must be between "
                    + Reference.MIN_DELAY_SECONDS
                    + " and "
                    + Reference.MAX_DELAY_SECONDS
                    + " secs"
            );
        }
    }

    public static void validateHistoryLength(int length) {
        if (length < Reference.MIN_MESSAGE_HISTORY_LENGTH
                || length > Reference.MAX_MESSAGE_HISTORY_LENGTH) {
            throw new IllegalArgumentException(
                "History length must be between "
                    + Reference.MIN_MESSAGE_HISTORY_LENGTH
                    + " and "
                    + Reference.MAX_MESSAGE_HISTORY_LENGTH
                    + " lines"
            );
        }
    }

    public static String normalizeMessagePrefix(String prefix) {
        return normalizePrefix(prefix, "Message prefix", false);
    }

    public static String normalizeCommandPrefix(String prefix) {
        return normalizePrefix(prefix, "Command prefix", true);
    }

    private static String normalizePrefix(
        String prefix,
        String displayName,
        boolean command
    ) {
        if (prefix == null) {
            throw new IllegalArgumentException(displayName + " cannot be null");
        }

        prefix = prefix.trim();

        if (prefix.isEmpty()) {
            throw new IllegalArgumentException(displayName + " is required");
        }

        if (command && !prefix.startsWith("/")) {
            prefix = "/" + prefix;
        }

        if (prefix.indexOf("\n") >= 0 || prefix.indexOf("\r") >= 0) {
            throw new IllegalArgumentException(displayName + " cannot contain new lines");
        }

        if (prefix.length() > Reference.MAX_PREFIX_LENGTH) {
            throw new IllegalArgumentException(
                displayName + " must be at most "
                    + Reference.MAX_PREFIX_LENGTH
                    + " characters"
            );
        }

        return prefix;
    }
}
