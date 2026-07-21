package com.github.cewko.extendedmessages;

import com.github.cewko.extendedmessages.config.ExtendedMessagesConfig;
import com.github.cewko.extendedmessages.messaging.MessageQueue;

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

    public static boolean enabled = Reference.DEFAULT_ENABLED;
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

    public static boolean toggleSplitEnabled() {
        splitEnabled = !splitEnabled;
        ExtendedMessagesConfig.saveSplitEnabled(splitEnabled);

        MessageQueue.getInstance().clear();

        return splitEnabled;
    }

    public static boolean toggleEnabled() {
        enabled = !enabled;
        ExtendedMessagesConfig.saveEnabled(enabled);

        if (!enabled) {
            MessageQueue.getInstance().clear();
        }

        return enabled;
    }

    private static void validateDelaySeconds(int seconds) {
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

    public static int getMessageDelaySeconds() {
        return messageDelaySeconds;
    }

    public static void setMessageDelaySeconds(int seconds) {
        validateDelaySeconds(seconds);

        messageDelaySeconds = seconds;
        ExtendedMessagesConfig.saveMessageDelaySeconds(seconds);
    }

    public static int getCommandDelaySeconds() {
        return commandDelaySeconds;
    }

    public static void setCommandDelaySeconds(int seconds) {
        validateDelaySeconds(seconds);
        commandDelaySeconds = seconds;
        ExtendedMessagesConfig.saveCommandDelaySeconds(seconds);
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

    public static boolean toggleMessagePrefixEnabled() {
        messagePrefixEnabled = !messagePrefixEnabled;

        ExtendedMessagesConfig.saveMessagePrefixEnabled(
            messagePrefixEnabled
        );

        return messagePrefixEnabled;
    }

    public static void setMessagePrefix(String prefix) {
        validatePrefix(prefix, "Message prefix");
        messagePrefix = prefix;

        ExtendedMessagesConfig.saveMessagePrefix(
            messagePrefix
        );
    }

    public static boolean toggleCommandPrefixEnabled() {
        commandPrefixEnabled = !commandPrefixEnabled;

        ExtendedMessagesConfig.saveCommandPrefixEnabled(
            commandPrefixEnabled
        );

        return commandPrefixEnabled;
    }

    public static void setCommandPrefix(String prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException("Command prefix cannot be null");
        }

        prefix = prefix.trim();

        if (prefix.isEmpty()) {
            throw new IllegalArgumentException("Command prefix cannot be empty");
        }

        if (!prefix.startsWith("/")) {
            prefix = "/" + prefix;
        }

        validatePrefix(prefix, "Command prefix");

        commandPrefix = prefix;

        ExtendedMessagesConfig.saveCommandPrefix(
            commandPrefix
        );
    }

    private static void validatePrefix(String prefix, String displayName) {
        if (prefix == null) {
            throw new IllegalArgumentException(displayName + " cannot be null");
        }

        if (prefix.indexOf("\n") >= 0 || prefix.indexOf("\r") >= 0) {
            throw new IllegalArgumentException(displayName + " cannot contain new lines");
        }

        if (prefix.length() >= Reference.DEFAULT_MESSAGE_LIMIT) {
            throw new IllegalArgumentException(
                displayName + " must be shorter than " + Reference.DEFAULT_MESSAGE_LIMIT);
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
        validateHistoryLength(length);
        messageHistoryLength = length;
        ExtendedMessagesConfig.saveMessageHistoryLength(length);
    }

    public static int getCurrentMessageHistoryLength() {
        return enabled ? messageHistoryLength : Reference.VANILLA_MESSAGE_HISTORY_LENGTH;
    }

    private static void validateHistoryLength(int length) {
        if (length < Reference.MIN_MESSAGE_HISTORY_LENGTH
                || length > Reference.MAX_MESSAGE_HISTORY_LENGTH) {
            throw new IllegalArgumentException(
                "history length must be between "
                    + Reference.MIN_MESSAGE_HISTORY_LENGTH
                    + " and "
                    + Reference.MAX_MESSAGE_HISTORY_LENGTH
                    + " lines"
            );
        }
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ExtendedMessagesConfig.load(
            event.getSuggestedConfigurationFile()
        );

        enabled = ExtendedMessagesConfig.getEnabled();
        splitEnabled = ExtendedMessagesConfig.getSplitEnabled();
        messageDelaySeconds = ExtendedMessagesConfig.getMessageDelaySeconds();
        commandDelaySeconds = ExtendedMessagesConfig.getCommandDelaySeconds();

        messagePrefixEnabled = ExtendedMessagesConfig.getMessagePrefixEnabled();
        messagePrefix = ExtendedMessagesConfig.getMessagePrefix();

        commandPrefixEnabled = ExtendedMessagesConfig.getCommandPrefixEnabled();
        commandPrefix = ExtendedMessagesConfig.getCommandPrefix();

        messageHistoryLength = ExtendedMessagesConfig.getMessageHistoryLength();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        ClientCommandHandler.instance.registerCommand(
            new ExtendedMessagesCommand()
        );

        MinecraftForge.EVENT_BUS.register(MessageQueue.getInstance());
        
        System.out.println("[" + Reference.NAME + "] Mod has loaded :3");
    }
}
