package com.github.cewko.extendedmessages;

import com.github.cewko.extendedmessages.config.ExtendedMessagesConfig;
import com.github.cewko.extendedmessages.messaging.MessageQueue;
import com.github.cewko.extendedmessages.gui.GuiOpener;

import net.minecraftforge.common.MinecraftForge;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

import net.minecraftforge.client.ClientCommandHandler;

@Mod(
    modid = Reference.MOD_ID,
    name = Reference.NAME,
    version = Reference.VERSION,
    acceptedMinecraftVersions = Reference.ACCEPTED_VERSIONS,
    clientSideOnly = true,
    useMetadata = true
)
public class ExtendedMessages {

    private static boolean enabled = Reference.DEFAULT_ENABLED;
    private static boolean splitEnabled = Reference.DEFAULT_SPLIT_ENABLED;
    private static int messageDelaySeconds = Reference.DEFAULT_MESSAGE_DELAY_SECONDS;
    private static int commandDelaySeconds = Reference.DEFAULT_COMMAND_DELAY_SECONDS;

    private static boolean messagePrefixEnabled = Reference.DEFAULT_MESSAGE_PREFIX_ENABLED;
    private static String messagePrefix = Reference.DEFAULT_MESSAGE_PREFIX;

    private static boolean commandPrefixEnabled = Reference.DEFAULT_COMMAND_PREFIX_ENABLED;
    private static String commandPrefix = Reference.DEFAULT_COMMAND_PREFIX;

    private static int messageHistoryLength = Reference.DEFAULT_MESSAGE_HISTORY_LENGTH;

    public static boolean isEnabled() {
        return enabled;
    }

    public static boolean isSplitEnabled() {
        return splitEnabled;
    }

    public static void setSplitEnabled(boolean value) {
        if (splitEnabled == value) {
            return;
        }

        splitEnabled = value;
        ExtendedMessagesConfig.saveSplitEnabled(value);

        MessageQueue.getInstance().clear();
    }

    public static boolean toggleSplitEnabled() {
        setSplitEnabled(!splitEnabled);
        return splitEnabled;
    }

    public static void setEnabled(boolean value) {
        if (enabled == value) {
            return;
        }

        enabled = value;
        ExtendedMessagesConfig.saveEnabled(value);

        if (!enabled) {
            MessageQueue.getInstance().clear();
        }
    }

    public static boolean toggleEnabled() {
        setEnabled(!enabled);
        return enabled;
    }

    public static int getMessageDelaySeconds() {
        return messageDelaySeconds;
    }

    public static void setMessageDelaySeconds(int seconds) {
        SettingsValidation.validateDelaySeconds(seconds);

        if (messageDelaySeconds != seconds) {
            messageDelaySeconds = seconds;
            ExtendedMessagesConfig.saveMessageDelaySeconds(seconds);
        }
    }

    public static int getCommandDelaySeconds() {
        return commandDelaySeconds;
    }

    public static void setCommandDelaySeconds(int seconds) {
        SettingsValidation.validateDelaySeconds(seconds);

        if (commandDelaySeconds != seconds) {
            commandDelaySeconds = seconds;
            ExtendedMessagesConfig.saveCommandDelaySeconds(seconds);
        }
    }

    public static boolean isMessagePrefixEnabled() {
        return messagePrefixEnabled;
    }

    public static String getMessagePrefix() {
        return messagePrefix;
    }

    public static boolean isCommandPrefixEnabled() {
        return commandPrefixEnabled;
    }

    public static String getCommandPrefix() {
        return commandPrefix;
    }

    public static void setMessagePrefixEnabled(boolean value) {
        if (messagePrefixEnabled == value) {
            return;
        }

        messagePrefixEnabled = value;
        ExtendedMessagesConfig.saveMessagePrefixEnabled(value);
    }

    public static boolean toggleMessagePrefixEnabled() {
        setMessagePrefixEnabled(!messagePrefixEnabled);
        return messagePrefixEnabled;
    }

    public static void setMessagePrefix(String prefix) {
        String normalized = SettingsValidation.normalizeMessagePrefix(prefix);

        if (!messagePrefix.equals(normalized)) {
            messagePrefix = normalized;
            ExtendedMessagesConfig.saveMessagePrefix(normalized);
        }
    }

    public static void setCommandPrefixEnabled(boolean value) {
        if (commandPrefixEnabled == value) {
            return;
        }

        commandPrefixEnabled = value;
        ExtendedMessagesConfig.saveCommandPrefixEnabled(value);
    }

    public static boolean toggleCommandPrefixEnabled() {
        setCommandPrefixEnabled(!commandPrefixEnabled);
        return commandPrefixEnabled;
    }

    public static void setCommandPrefix(String prefix) {
        String normalized = SettingsValidation.normalizeCommandPrefix(prefix);

        if (!commandPrefix.equals(normalized)) {
            commandPrefix = normalized;
            ExtendedMessagesConfig.saveCommandPrefix(normalized);
        }
    }

    public static int getCurrentMessageLimit() {
        return enabled
            ? Reference.EXTENDED_MESSAGE_LIMIT
            : Reference.DEFAULT_MESSAGE_LIMIT;
    }

    public static int getCurrentPacketMessageLimit() {
        if (!enabled) {
            return Reference.DEFAULT_MESSAGE_LIMIT;
        }

        return splitEnabled
            ? Reference.DEFAULT_MESSAGE_LIMIT
            : Reference.EXTENDED_MESSAGE_LIMIT;
    }

    public static int getMessageHistoryLength() {
        return messageHistoryLength;
    }

    public static void setMessageHistoryLength(int length) {
        SettingsValidation.validateHistoryLength(length);

        if (messageHistoryLength != length) {
            messageHistoryLength = length;
            ExtendedMessagesConfig.saveMessageHistoryLength(length);
        }
    }

    public static int getCurrentMessageHistoryLength() {
        return enabled ? messageHistoryLength : Reference.VANILLA_MESSAGE_HISTORY_LENGTH;
    }

    private static String loadMessagePrefix() {
        try {
            return SettingsValidation.normalizeMessagePrefix(
                ExtendedMessagesConfig.getMessagePrefix()
            );
        } catch (IllegalArgumentException exception) {
            return Reference.DEFAULT_MESSAGE_PREFIX;
        }
    }

    private static String loadCommandPrefix() {
        try {
            return SettingsValidation.normalizeCommandPrefix(
                ExtendedMessagesConfig.getCommandPrefix()
            );
        } catch (IllegalArgumentException exception) {
            return Reference.DEFAULT_COMMAND_PREFIX;
        }
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ExtendedMessagesConfig.load(event.getSuggestedConfigurationFile());

        enabled = ExtendedMessagesConfig.getEnabled();
        splitEnabled = ExtendedMessagesConfig.getSplitEnabled();

        messageDelaySeconds = ExtendedMessagesConfig.getMessageDelaySeconds();
        commandDelaySeconds = ExtendedMessagesConfig.getCommandDelaySeconds();

        messagePrefixEnabled = ExtendedMessagesConfig.getMessagePrefixEnabled();
        commandPrefixEnabled = ExtendedMessagesConfig.getCommandPrefixEnabled();

        messagePrefix = loadMessagePrefix();
        commandPrefix = loadCommandPrefix();

        ExtendedMessagesConfig.saveMessagePrefix(messagePrefix);
        ExtendedMessagesConfig.saveCommandPrefix(commandPrefix);

        messageHistoryLength = ExtendedMessagesConfig.getMessageHistoryLength();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        ClientCommandHandler.instance.registerCommand(
            new ExtendedMessagesCommand()
        );

        MinecraftForge.EVENT_BUS.register(MessageQueue.getInstance());
        MinecraftForge.EVENT_BUS.register(GuiOpener.getInstance());
        
        System.out.println("[" + Reference.NAME + "] Mod has loaded :3");
    }
}
