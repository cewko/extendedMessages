package com.github.cewko.enhanmess.config;

import com.github.cewko.enhanmess.Reference;

import java.io.File;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public final class EnhanMessConfig {

    private static final String KEY_ENABLED = "enabled";
    private static final String KEY_SPLIT_ENABLED = "splitLongMessages";
    private static final String KEY_MESSAGE_DELAY = "messageDelaySeconds";
    private static final String KEY_COMMAND_DELAY = "commandDelaySeconds";

    private static final String KEY_MESSAGE_PREFIX_ENABLED = "messagePrefixEnabled";
    private static final String KEY_MESSAGE_PREFIX = "messagePrefix";

    private static final String KEY_COMMAND_PREFIX_ENABLED = "commandPrefixEnabled";
    private static final String KEY_COMMAND_PREFIX = "commandPrefix";

    private static final String KEY_MESSAGE_HISTORY_LENGTH = "messageHistoryLength";

    private static Configuration configuration;

    private static Property enabledProperty;
    private static Property splitEnabledProperty;
    private static Property messageDelayProperty;
    private static Property commandDelayProperty;

    private static Property messagePrefixEnabledProperty;
    private static Property messagePrefixProperty;

    private static Property commandPrefixEnabledProperty;
    private static Property commandPrefixProperty;

    private static Property messageHistoryLengthProperty;

    private EnhanMessConfig() {
        // prevent creating instances
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
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
            "Delay in seconds between regular message chunks."
        );

        commandDelayProperty = configuration.get(
            Configuration.CATEGORY_GENERAL,
            KEY_COMMAND_DELAY,
            Reference.DEFAULT_COMMAND_DELAY_SECONDS,
            "Delay in seconds between configured command message chunks."
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

        messageHistoryLengthProperty = configuration.get(
            Configuration.CATEGORY_GENERAL,
            KEY_MESSAGE_HISTORY_LENGTH,
            Reference.DEFAULT_MESSAGE_HISTORY_LENGTH,
            "maximum number of message history length"
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
        return clamp(
            value,
            Reference.MIN_DELAY_SECONDS,
            Reference.MAX_DELAY_SECONDS
        );
    }

    public static int getCommandDelaySeconds() {
        ensureLoaded();

        int value = commandDelayProperty.getInt(
            Reference.DEFAULT_COMMAND_DELAY_SECONDS
        );
        return clamp(
            value,
            Reference.MIN_DELAY_SECONDS,
            Reference.MAX_DELAY_SECONDS
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

    public static int getMessageHistoryLength() {
        ensureLoaded();
        
        int value = messageHistoryLengthProperty.getInt(
            Reference.DEFAULT_MESSAGE_HISTORY_LENGTH
        );

        return clamp(
            value,
            Reference.MIN_MESSAGE_HISTORY_LENGTH,
            Reference.MAX_MESSAGE_HISTORY_LENGTH
        );
    }

    private static void ensureLoaded() {
        if (configuration == null) {
            throw new IllegalStateException(
                Reference.NAME + " config has not been loaded"
            );
        }
    }

    public static void saveEnabled(boolean enabled) {
        save(enabledProperty, enabled);
    }

    public static void saveSplitEnabled(boolean enabled) {
        save(splitEnabledProperty, enabled);
    }

    public static void saveMessageDelaySeconds(int seconds) {
        save(messageDelayProperty, seconds);
    }

    public static void saveCommandDelaySeconds(int seconds) {
        save(commandDelayProperty, seconds);
    }

    public static void saveMessagePrefixEnabled(boolean enabled) {
        save(messagePrefixEnabledProperty, enabled);
    }

    public static void saveMessagePrefix(String prefix) {
        save(messagePrefixProperty, prefix);
    }

    public static void saveCommandPrefixEnabled(boolean enabled) {
        save(commandPrefixEnabledProperty, enabled);
    }

    public static void saveCommandPrefix(String prefix) {
        save(commandPrefixProperty, prefix);
    }

    public static void saveMessageHistoryLength(int length) {
        save(messageHistoryLengthProperty, length);
    }

    private static void save(Property property, boolean value) {
        ensureLoaded();
        property.set(value);
        saveIfChanged();
    }

    private static void save(Property property, int value) {
        ensureLoaded();
        property.set(value);
        saveIfChanged();
    }

    private static void save(Property property, String value) {
        ensureLoaded();
        property.set(value);
        saveIfChanged();
    }

    private static void saveIfChanged() {
        if (configuration.hasChanged()) {
            configuration.save();
        }
    }
}
