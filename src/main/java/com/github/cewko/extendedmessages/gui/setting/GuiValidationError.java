package com.github.cewko.extendedmessages.gui.setting;

public enum GuiValidationError {
    REQUIRED("required"),
    NUMBERS_ONLY("numbers only"),
    OUT_OF_RANGE("out of range"),
    TOO_LONG("too long"),
    NO_NEW_LINES("no new lines"),
    INVALID("invalid");

    private final String label;

    GuiValidationError(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static GuiValidationError fromException(IllegalArgumentException exception) {
        String message = exception.getMessage().toLowerCase();

        if (message.contains("required")) {
            return REQUIRED;
        }

        if (message.contains("at most")) {
            return TOO_LONG;
        }

        if (message.contains("new line")) {
            return NO_NEW_LINES;
        }

        return INVALID;
    }
}