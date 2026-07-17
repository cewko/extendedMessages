package com.github.cewko.extendedmessages.config;

import com.github.cewko.extendedmessages.Reference;

import java.io.File;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public final class ExtendedMessagesConfig {

    private static final String KEY_ENABLED = "enabled";
    private static final String KEY_SPLIT_ENABLED = "splitLongMessages";
    private static final String KEY_MESSAGE_DELAY = "messageDelaySeconds";

    private static final boolean DEFAULT_ENABLED = false;
    private static final boolean DEFAULT_SPLIT_ENABLED = true;
    private static final int DEFAULT_MESSAGE_DELAY = 3;

    private static Configuration configuration;

    private static Property enabledProperty;
    private static Property splitEnabledProperty;
    private static Property messageDelayProperty;

    private ExtendedMessagesConfig() {
        // prevent creating instances
    }

    public static void load(File file) {
        configuration = new Configuration(file);
        configuration.load();

        enabledProperty = configuration.get(
            Configuration.CATEGORY_GENERAL,
            KEY_ENABLED,
            DEFAULT_ENABLED,
            "Whether " + Reference.NAME + " is enabled"
        );

        splitEnabledProperty = configuration.get(
            Configuration.CATEGORY_GENERAL,
            KEY_SPLIT_ENABLED,
            DEFAULT_SPLIT_ENABLED,
            "Whether long messages are split into 100-character chunks"
        );

        messageDelayProperty = configuration.get(
            Configuration.CATEGORY_GENERAL,
            KEY_MESSAGE_DELAY,
            DEFAULT_MESSAGE_DELAY,
            "Delay in seconds between message chunks."
        );

        saveIfChanged();
    }

    public static boolean getEnabled() {
        ensureLoaded();
        return enabledProperty.getBoolean(DEFAULT_ENABLED);
    }

    public static boolean getSplitEnabled() {
        ensureLoaded();
        return splitEnabledProperty.getBoolean(DEFAULT_SPLIT_ENABLED);
    }

    public static int getMessageDelaySeconds() {
        ensureLoaded();

        int value = messageDelayProperty.getInt(DEFAULT_MESSAGE_DELAY);
        return Math.max(1, Math.min(30, value));
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

    public static void saveIfChanged() {
        if (configuration.hasChanged()) {
            configuration.save();
        }
    }
}