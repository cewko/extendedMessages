package com.github.cewko.enhanmess.messaging;

import com.github.cewko.enhanmess.EnhanMess;
import com.github.cewko.enhanmess.Reference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public final class OutgoingMessageProcessor {
    private OutgoingMessageProcessor() {
    }

    private static final class ProcessedMessage {
        private final List<String> lines;
        private final MessageQueue.Lane lane;

        private ProcessedMessage(List<String> lines, MessageQueue.Lane lane) {
            this.lines = lines;
            this.lane = lane;
        }
    }

    public static boolean shouldBypass(String message) {
        if (!message.startsWith("/")) {
            return false;
        }

        if (startsWithCommand(
            message, "/" + Reference.COMMAND_NAME
        ) || startsWithCommand(
            message, "/" + Reference.COMMAND_ALIAS
        )) {
            return true;
        }

        if (isConfiguredCommandMessage(message)) {
            return false;
        }

        return message.length() <= EnhanMess.getCurrentPacketMessageLimit();
    }

    private static boolean startsWithCommand(String message, String command) {
        String commandWithSpace = withTrailingSpace(command);
        return message.equalsIgnoreCase(command)
            || message.regionMatches(true, 0, commandWithSpace, 0, commandWithSpace.length());
    }

    private static boolean isConfiguredCommandMessage(String message) {
        if (!EnhanMess.isCommandPrefixEnabled()) {
            return false;
        }

        String commandPrefix = withTrailingSpace(EnhanMess.getCommandPrefix());

        return !commandPrefix.isEmpty()
            && message.regionMatches(true, 0, commandPrefix, 0, commandPrefix.length())
            && !message.substring(commandPrefix.length()).trim().isEmpty();
    }

    private static String withTrailingSpace(String prefix) {
        String trimmed = prefix.trim();
        return trimmed.isEmpty() ? "" : trimmed + " ";
    }

    public static void handle(Minecraft minecraft, String message) {
        ProcessedMessage processed = process(minecraft, message);

        if (processed.lines.isEmpty()) {
            return;
        }

        MessageQueue.getInstance().enqueueLines(processed.lines, processed.lane);
    }

    private static ProcessedMessage process(Minecraft minecraft, String message) {
        if (isConfiguredCommandMessage(message)) {
            return processConfiguredCommand(minecraft, message);
        }

        if (message.startsWith("/")) {
            warn(minecraft, "long commands cannot be split safely");
            return new ProcessedMessage(
                Collections.<String>emptyList(),
                MessageQueue.Lane.REGULAR
            );
        }

        return processRegularMessage(minecraft, message);
    }

    private static ProcessedMessage processRegularMessage(Minecraft minecraft, String message) {
        String messagePrefix = EnhanMess.isMessagePrefixEnabled()
            ? withTrailingSpace(EnhanMess.getMessagePrefix())
            : "";

        return new ProcessedMessage(
            buildSendableLines(minecraft, messagePrefix, message),
            MessageQueue.Lane.REGULAR
        );
    }

    private static ProcessedMessage processConfiguredCommand(
        Minecraft minecraft,
        String message
    ) {
        String commandPrefix = withTrailingSpace(EnhanMess.getCommandPrefix());
        String body = message.substring(commandPrefix.length());

        return new ProcessedMessage(
            buildSendableLines(minecraft, commandPrefix, body),
            MessageQueue.Lane.CONFIGURED_COMMAND
        );
    }

    private static void warn(Minecraft minecraft, String message) {
        if (minecraft.thePlayer == null) {
            return;
        }

        minecraft.thePlayer.addChatMessage(new ChatComponentText(
            EnumChatFormatting.RED + "[" + Reference.NAME + "] " + message
        ));
    }

    private static List<String> buildSendableLines(
        Minecraft minecraft,
        String prefix,
        String body
    ) {
        if (!EnhanMess.isSplitEnabled()) {
            String line = prefix + body;

            if (line.length() > Reference.EXTENDED_MESSAGE_LIMIT) {
                warn(minecraft, "outgoing message would exceed "
                    + Reference.EXTENDED_MESSAGE_LIMIT
                    + " chars"
                );
                return Collections.emptyList();
            }

            return Collections.singletonList(line);
        }

        return splitWithPrefix(minecraft, prefix, body);
    }

    private static List<String> splitWithPrefix(
        Minecraft minecraft,
        String prefix,
        String body
    ) {
        int bodyLimit = Reference.DEFAULT_MESSAGE_LIMIT - prefix.length();

        if (bodyLimit <= 0) {
            warn(minecraft, "prefix is too long");
            return Collections.emptyList();
        }

        List<String> lines = new ArrayList<String>();

        for (int start = 0; start < body.length(); start += bodyLimit) {
            int end = Math.min(start + bodyLimit, body.length());
            lines.add(prefix + body.substring(start, end));
        }

        return lines;
    }
}
