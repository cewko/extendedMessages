package com.github.cewko.extendedmessages.messaging;

import com.github.cewko.extendedmessages.ExtendedMessages;
import com.github.cewko.extendedmessages.Reference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public final class OutgoingMessageProcessor {
    private OutgoingMessageProcessor() {

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

        return message.length() <= ExtendedMessages.getCurrentPacketMessageLimit();
    }

    private static boolean startsWithCommand(String message, String command) {
        String commandWithSpace = withTrailingSpace(command);
        return message.equalsIgnoreCase(command)
            || message.regionMatches(true, 0, commandWithSpace, 0, commandWithSpace.length());
    }

    private static boolean isConfiguredCommandMessage(String message) {
        if (!ExtendedMessages.isCommandPrefixEnabled()) {
            return false;
        }

        String commandPrefix = withTrailingSpace(ExtendedMessages.getCommandPrefix());

        return !commandPrefix.isEmpty()
            && message.regionMatches(true, 0, commandPrefix, 0, commandPrefix.length())
            && !message.substring(commandPrefix.length()).trim().isEmpty();
    }

    private static String withTrailingSpace(String prefix) {
        String trimmed = prefix.trim();
        return trimmed.isEmpty() ? "" : trimmed + " ";
    }

    public static void handle(Minecraft minecraft, String message) {
        List<String> lines = buildLines(minecraft, message);

        if (!lines.isEmpty()) {
            MessageQueue.getInstance().enqueueLines(lines);
        }
    }

    private static List<String> buildLines(Minecraft minecraft, String message) {
        if (isConfiguredCommandMessage(message)) {
            String commandPrefix = withTrailingSpace(ExtendedMessages.getCommandPrefix());
            String body = message.substring((commandPrefix.length()));
            return buildSendableLines(minecraft, commandPrefix, body);
        }

        if (message.startsWith("/")) {
            warn(minecraft, "long commands cannot be split safely");
            return Collections.emptyList();
        }

        String messagePrefix = ExtendedMessages.isMessagePrefixEnabled()
            ? withTrailingSpace(ExtendedMessages.getMessagePrefix())
            : "";

        return buildSendableLines(minecraft, messagePrefix, message);
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
        if (!ExtendedMessages.isSplitEnabled()) {
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
