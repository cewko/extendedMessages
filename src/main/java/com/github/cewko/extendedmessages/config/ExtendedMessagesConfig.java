package com.github.cewko.extendedmessages.config;

import com.github.cewko.extendedmessages.Reference;

import java.io.File;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public final class ExtendedMessagesConfig {

    private static final String KEY_ENABLED = "enabled";
    private static final String KEY_SPLIT_ENABLED = "splitLongMessages";
    private static final String KEY_MESSAGE_DELAY = "messageDelaySeconds";

    private static final String KEY_MESSAGE_PREFIX_ENABLED = "messagePrefixEnabled";
    private static final String KEY_MESSAGE_PREFIX = "messagePrefix";

    private static final String KEY_COMMAND_PREFIX_ENABLED = "commandPrefixEnabled";
    private static final String KEY_COMMAND_PREFIX = "commandPrefix";

    private static Configuration configuration;

    private static Property enabledProperty;
    private static Property splitEnabledProperty;
    private static Property messageDelayProperty;

    private static Property messagePrefixEnabledProperty;
    private static Property messagePrefixProperty;

    private static Property commandPrefixEnabledProperty;
    private static Property commandPrefixProperty;

    private ExtendedMessagesConfig() {
        // prevent creating instances
    }

    public static void load(File file) {
        configuration = new Configuration(file);
        configuration.load();

        enabledProperty = configuration.get(
            Configuration.CATEGORY_GENERAL,
            KEY_ENABLED,
            Reference.DEFAULT_ENABLED,
            "Whether " + Reference.NAME + " is enabled"
        );

        splitEnabledProperty = configuration.get(
            Configuration.CATEGORY_GENERAL,
            KEY_SPLIT_ENABLED,
            Reference.DEFAULT_SPLIT_ENABLED,
            "Whether long messages are split into "
                + Reference.DEFAULT_MESSAGE_LIMIT
                + "-character chunks"
        );

        messageDelayProperty = configuration.get(
            Configuration.CATEGORY_GENERAL,
            KEY_MESSAGE_DELAY,
            Reference.DEFAULT_MESSAGE_DELAY_SECONDS,
            "Delay in seconds between message chunks."
        );

        messagePrefixEnabledProperty = configuration.get(
            Configuration.CATEGORY_GENERAL,
            KEY_MESSAGE_PREFIX_ENABLED,
            Reference.DEFAULT_MESSAGE_PREFIX_ENABLED,
            "Whether a prefix is added to regular messages"
        );

        messagePrefixProperty = configuration.get(
            Configuration.CATEGORY_GENERAL,
            KEY_MESSAGE_PREFIX,
            Reference.DEFAULT_MESSAGE_PREFIX,
            "Text added before regular messages"
        );

        commandPrefixEnabledProperty = configuration.get(
            Configuration.CATEGORY_GENERAL,
            KEY_COMMAND_PREFIX_ENABLED,
            Reference.DEFAULT_COMMAND_PREFIX_ENABLED,
            "Whether configured message command can be split"
        );

        commandPrefixProperty = configuration.get(
            Configuration.CATEGORY_GENERAL,
            KEY_COMMAND_PREFIX,
            Reference.DEFAULT_COMMAND_PREFIX,
            "Message command repeated before every command message chunk"
        );

        saveIfChanged();
    }

    public static boolean getEnabled() {
        ensureLoaded();
        return enabledProperty.getBoolean(Reference.DEFAULT_ENABLED);
    }

    public static boolean getSplitEnabled() {
        ensureLoaded();
        return splitEnabledProperty.getBoolean(Reference.DEFAULT_SPLIT_ENABLED);
    }

    public static int getMessageDelaySeconds() {
        ensureLoaded();

        int value = messageDelayProperty.getInt(
            Reference.DEFAULT_MESSAGE_DELAY_SECONDS
        );
        return Math.max(
            Reference.MIN_MESSAGE_DELAY_SECONDS,
            Math.min(Reference.MAX_MESSAGE_DELAY_SECONDS, value)
        );
    }

    public static boolean getMessagePrefixEnabled() {
        ensureLoaded();
        return messagePrefixEnabledProperty.getBoolean(
            Reference.DEFAULT_MESSAGE_PREFIX_ENABLED
        );
    }

    public static String getMessagePrefix() {
        ensureLoaded();
        return messagePrefixProperty.getString();
    }

    public static boolean getCommandPrefixEnabled() {
        ensureLoaded();
        return commandPrefixEnabledProperty.getBoolean(
            Reference.DEFAULT_COMMAND_PREFIX_ENABLED
        );
    }

    public static String getCommandPrefix() {
        ensureLoaded();
        return commandPrefixProperty.getString();
    }

    public static void ensureLoaded() {
        if (configuration == null) {
            throw new IllegalStateException(
                Reference.NAME + " config has not been loaded"
            );
        }
    }

    public static void saveEnabled(boolean enabled) {
        ensureLoaded();
        enabledProperty.set(enabled);
        saveIfChanged();
    }

    public static void saveSplitEnabled(boolean enabled) {
        ensureLoaded();
        splitEnabledProperty.set(enabled);
        saveIfChanged();
    }

    public static void saveMessageDelaySeconds(int seconds) {
        ensureLoaded();
        messageDelayProperty.set(seconds);
        saveIfChanged();
    }

    public static void saveMessagePrefixEnabled(boolean enabled) {
        ensureLoaded();
        messagePrefixEnabledProperty.set(enabled);
        saveIfChanged();
    }

    public static void saveMessagePrefix(String prefix) {
        ensureLoaded();
        messagePrefixProperty.set(prefix);
        saveIfChanged();
    }

    public static void saveCommandPrefixEnabled(boolean enabled) {
        ensureLoaded();
        commandPrefixEnabledProperty.set(enabled);
        saveIfChanged();
    }

    public static void saveCommandPrefix(String prefix) {
        ensureLoaded();
        commandPrefixProperty.set(prefix);
        saveIfChanged();
    }

    public static void saveIfChanged() {
        if (configuration.hasChanged()) {
            configuration.save();
        }
    }
}
