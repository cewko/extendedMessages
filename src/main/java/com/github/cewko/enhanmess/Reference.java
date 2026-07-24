package com.github.cewko.enhanmess;


public final class Reference {

    public static final String MOD_ID = "enhanmess";
    public static final String NAME = "EnhanMess";
    public static final String VERSION = "1.0.0";
    public static final String ACCEPTED_VERSIONS = "[1.8.9]";

    public static final String COMMAND_NAME = "enhanmess";
    public static final String COMMAND_ALIAS = "em";

    public static final int DEFAULT_MESSAGE_LIMIT = 100;
    public static final int EXTENDED_MESSAGE_LIMIT = 256;

    public static final int MIN_DELAY_SECONDS = 0;
    public static final int MAX_DELAY_SECONDS = 120;
    public static final int DEFAULT_MESSAGE_DELAY_SECONDS = 3;
    public static final int DEFAULT_COMMAND_DELAY_SECONDS = 1;
    public static final long SEND_SAFETY_MARGIN_MILLIS = 50L;

    public static final boolean DEFAULT_ENABLED = false;
    public static final boolean DEFAULT_SPLIT_ENABLED = true;

    public static final boolean DEFAULT_MESSAGE_PREFIX_ENABLED = false;
    public static final String DEFAULT_MESSAGE_PREFIX = "!";

    public static final boolean DEFAULT_COMMAND_PREFIX_ENABLED = true;
    public static final String DEFAULT_COMMAND_PREFIX = "/pc";

    public static final int VANILLA_MESSAGE_HISTORY_LENGTH = 100;
    public static final int MIN_MESSAGE_HISTORY_LENGTH = 100;
    public static final int MAX_MESSAGE_HISTORY_LENGTH = 65536;
    public static final int DEFAULT_MESSAGE_HISTORY_LENGTH = 1000;

    public static final int MAX_PREFIX_LENGTH = DEFAULT_MESSAGE_LIMIT - 2;
    public static final int GUI_PREFIX_INPUT_LIMIT = MAX_PREFIX_LENGTH + 1;

    private Reference() {
        // prevent creating instances
    }
}
