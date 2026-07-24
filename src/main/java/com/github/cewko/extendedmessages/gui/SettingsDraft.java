package com.github.cewko.extendedmessages.gui;

import com.github.cewko.extendedmessages.ExtendedMessages;
import com.github.cewko.extendedmessages.SettingsValidation;

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
        SettingsValidation.normalizeMessagePrefix(prefix);
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
        SettingsValidation.normalizeCommandPrefix(prefix);
    }

    public int getMessageHistoryLength() {
        return messageHistoryLength;
    }

    public void setMessageHistoryLength(int length) {
        messageHistoryLength = length;
    }

    public void validate() {
        SettingsValidation.normalizeMessagePrefix(messagePrefix);
        SettingsValidation.normalizeCommandPrefix(commandPrefix);
        SettingsValidation.validateDelaySeconds(messageDelaySeconds);
        SettingsValidation.validateDelaySeconds(commandDelaySeconds);
        SettingsValidation.validateHistoryLength(messageHistoryLength);
    }

    public void apply() {
        validate();

        String normalizedMessagePrefix =
            SettingsValidation.normalizeMessagePrefix(messagePrefix);

        String normalizedCommandPrefix =
            SettingsValidation.normalizeCommandPrefix(commandPrefix);

        ExtendedMessages.setMessagePrefix(normalizedMessagePrefix);
        ExtendedMessages.setCommandPrefix(normalizedCommandPrefix);
        ExtendedMessages.setMessageDelaySeconds(messageDelaySeconds);
        ExtendedMessages.setCommandDelaySeconds(commandDelaySeconds);
        ExtendedMessages.setMessageHistoryLength(messageHistoryLength);
        ExtendedMessages.setMessagePrefixEnabled(messagePrefixEnabled);
        ExtendedMessages.setCommandPrefixEnabled(commandPrefixEnabled);
        ExtendedMessages.setSplitEnabled(splitEnabled);
        ExtendedMessages.setEnabled(enabled);
    }
}
