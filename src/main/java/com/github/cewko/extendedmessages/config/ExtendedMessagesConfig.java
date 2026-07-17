package com.github.cewko.extendedmessages.config;

import com.github.cewko.extendedmessages.Reference;

import java.io.File;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public final class ExtendedMessagesConfig {

    private static final String KEY_ENABLED = "enabled";
    private static final boolean DEFAULT_ENABLED = false;

    private static Configuration configuration;
    private static Property enabledProperty;

    private ExtendedMessagesConfig() {

    }

    public static boolean load(File file) {
        configuration = new Configuration(file);
        configuration.load();

        enabledProperty = configuration.get(
            Configuration.CATEGORY_GENERAL,
            KEY_ENABLED,
            DEFAULT_ENABLED,
            "Whether " + Reference.NAME + " is enabled"
        );

        boolean enabled = enabledProperty.getBoolean(DEFAULT_ENABLED);

        if (configuration.hasChanged()) {
            configuration.save();
        }

        return enabled;
    }

    public static void saveEnabled(boolean enabled) {
        if (configuration == null || enabledProperty == null) {
            throw new IllegalStateException(
                Reference.NAME + " config has not been loaded"
            );
        }

        enabledProperty.set(enabled);

        if (configuration.hasChanged()) {
            configuration.save();
        }
    }
}