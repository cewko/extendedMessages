package com.github.cewko.extendedmessages.gui;

import com.github.cewko.extendedmessages.ExtendedMessages;
import com.github.cewko.extendedmessages.Reference;

public final class SettingsDraft {
    private boolean enabled;
    private boolean splitEnabled;

    private int messageDelaySeconds;
    private int commandDelaySeconds;

    private boolean messagePrefixEnabled;
    private String messagePrefix;

    private boolean commandPrefixEnabled;
    private String commandPrefix;

    private int messageHistoryLength;

    private SettingsDraft() {
    }

    public static SettingsDraft fromCurrentSettings() {
        SettingsDraft draft = new SettingsDraft();

        draft.enabled = ExtendedMessages.isEnabled();
        draft.splitEnabled = ExtendedMessages.isSplitEnabled();

        draft.messageDelaySeconds = ExtendedMessages.getMessageDelaySeconds();
        draft.commandDelaySeconds = ExtendedMessages.getCommandDelaySeconds();

        draft.messagePrefixEnabled = ExtendedMessages.isMessagePrefixEnabled();
        draft.messagePrefix = ExtendedMessages.getMessagePrefix();

        draft.commandPrefixEnabled = ExtendedMessages.isCommandPrefixEnabled();
        draft.commandPrefix = ExtendedMessages.getCommandPrefix();

        draft.messageHistoryLength = ExtendedMessages.getMessageHistoryLength();

        return draft;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void toggleEnabled() {
        enabled = !enabled;
    }

    public boolean isSplitEnabled() {
        return splitEnabled;
    }

    public void toggleSplitEnabled() {
        splitEnabled = !splitEnabled;
    }

    public int getMessageDelaySeconds() {
        return messageDelaySeconds;
    }

    public void setMessageDelaySeconds(int seconds) {
        messageDelaySeconds = seconds;
    }

    public int getCommandDelaySeconds() {
        return commandDelaySeconds;
    }

    public void setCommandDelaySeconds(int seconds) {
        commandDelaySeconds = seconds;
    }

    public boolean isMessagePrefixEnabled() {
        return messagePrefixEnabled;
    }

    public void toggleMessagePrefixEnabled() {
        messagePrefixEnabled = !messagePrefixEnabled;
    }

    public String getMessagePrefix() {
        return messagePrefix;
    }

    public void setMessagePrefix(String prefix) {
        messagePrefix = prefix;
        ExtendedMessages.normalizeMessagePrefix(prefix);
    }

    public boolean isCommandPrefixEnabled() {
        return commandPrefixEnabled;
    }

    public void toggleCommandPrefixEnabled() {
        commandPrefixEnabled = !commandPrefixEnabled;
    }

    public String getCommandPrefix() {
        return commandPrefix;
    }

    public void setCommandPrefix(String prefix) {
        commandPrefix = prefix;
        ExtendedMessages.normalizeCommandPrefix(prefix);
    }

    public int getMessageHistoryLength() {
        return messageHistoryLength;
    }

    public void setMessageHistoryLength(int length) {
        messageHistoryLength = length;
    }

    public void validate() {
        ExtendedMessages.normalizeMessagePrefix(messagePrefix);
        ExtendedMessages.normalizeCommandPrefix(commandPrefix);

        if (messageDelaySeconds < Reference.MIN_DELAY_SECONDS
                || messageDelaySeconds > Reference.MAX_DELAY_SECONDS) {
            throw new IllegalArgumentException(
                "Message cooldown must be between "
                    + Reference.MIN_DELAY_SECONDS
                    + " and "
                    + Reference.MAX_DELAY_SECONDS
                    + " seconds"
            );
        }

        if (commandDelaySeconds < Reference.MIN_DELAY_SECONDS
                || commandDelaySeconds > Reference.MAX_DELAY_SECONDS) {
            throw new IllegalArgumentException(
                "Command cooldown must be between "
                    + Reference.MIN_DELAY_SECONDS
                    + " and "
                    + Reference.MAX_DELAY_SECONDS
                    + " seconds"
            );
        }

        if (messageHistoryLength < Reference.MIN_MESSAGE_HISTORY_LENGTH
                || messageHistoryLength > Reference.MAX_MESSAGE_HISTORY_LENGTH) {
            throw new IllegalArgumentException(
                "History length must be between "
                    + Reference.MIN_MESSAGE_HISTORY_LENGTH
                    + " and "
                    + Reference.MAX_MESSAGE_HISTORY_LENGTH
                    + " lines"
            );
        }
    }

    public void apply() {
        validate();

        String normalizedMessagePrefix =
            ExtendedMessages.normalizeMessagePrefix(messagePrefix);

        String normalizedCommandPrefix =
            ExtendedMessages.normalizeCommandPrefix(commandPrefix);

        if (!normalizedMessagePrefix.equals(ExtendedMessages.getMessagePrefix())) {
            ExtendedMessages.setMessagePrefix(normalizedMessagePrefix);
        }

        if (!normalizedCommandPrefix.equals(ExtendedMessages.getCommandPrefix())) {
            ExtendedMessages.setCommandPrefix(normalizedCommandPrefix);
        }

        if (messageDelaySeconds != ExtendedMessages.getMessageDelaySeconds()) {
            ExtendedMessages.setMessageDelaySeconds(messageDelaySeconds);
        }

        if (commandDelaySeconds != ExtendedMessages.getCommandDelaySeconds()) {
            ExtendedMessages.setCommandDelaySeconds(commandDelaySeconds);
        }

        if (messageHistoryLength != ExtendedMessages.getMessageHistoryLength()) {
            ExtendedMessages.setMessageHistoryLength(messageHistoryLength);
        }

        if (messagePrefixEnabled != ExtendedMessages.isMessagePrefixEnabled()) {
            ExtendedMessages.toggleMessagePrefixEnabled();
        }

        if (commandPrefixEnabled != ExtendedMessages.isCommandPrefixEnabled()) {
            ExtendedMessages.toggleCommandPrefixEnabled();
        }

        if (splitEnabled != ExtendedMessages.isSplitEnabled()) {
            ExtendedMessages.toggleSplitEnabled();
        }

        if (enabled != ExtendedMessages.isEnabled()) {
            ExtendedMessages.toggleEnabled();
        }
    }
}
