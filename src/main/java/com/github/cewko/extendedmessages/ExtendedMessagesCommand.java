package com.github.cewko.extendedmessages;

import java.util.Collections;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.CommandException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public final class ExtendedMessagesCommand extends CommandBase {

    @Override
    public String getCommandName() {
        return "extendedmessages";
    }

    @Override
    public List<String> getCommandAliases() {
        return Collections.singletonList("em");
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/em [toggle|split|delay <"
            + Reference.MIN_MESSAGE_DELAY_SECONDS
            + "-"
            + Reference.MAX_MESSAGE_DELAY_SECONDS
            + ">|prefix <toggle|set|remove>|party <toggle|set>]";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) 
        throws CommandException {
            
        if (args.length == 0) {
            showStatus(sender);
            return;
        }

        if (args.length == 1 && "toggle".equalsIgnoreCase(args[0])) {

            boolean enabled = ExtendedMessages.toggleEnabled();

            sendLine(sender, "Remove 100-character limit: " + formatState(enabled));

            return;
        }

        if (args.length == 1 && "split".equalsIgnoreCase(args[0])) {

            boolean enabled = ExtendedMessages.toggleSplitEnabled();

            sendLine(sender, "Split long messages: " + formatState(enabled));

            return;
        }

        if (args.length == 2 && "delay".equalsIgnoreCase(args[0])) {

            int seconds = parseInt(
                args[1],
                Reference.MIN_MESSAGE_DELAY_SECONDS,
                Reference.MAX_MESSAGE_DELAY_SECONDS
            );

            ExtendedMessages.setMessageDelaySeconds(seconds);

            sendLine(
                sender,
                "Message delay: "
                    + EnumChatFormatting.AQUA
                    + seconds
                    + EnumChatFormatting.GRAY
                    + (seconds == 1 ? " second" : " seconds")
            );

            return;
        }

        if ("prefix".equalsIgnoreCase(args[0])) {
            handlePrefixCommand(sender, args);
            return;
        }

        if ("party".equalsIgnoreCase(args[0])) {
            handlePartyCommand(sender, args);
            return;
        }

        throw new WrongUsageException(getCommandUsage(sender));
    }

    private void handlePrefixCommand(
        ICommandSender sender, String[] args) throws CommandException {

            if (args.length == 1) {
                showPrefixStatus(sender);
                return;
            }

            if (args.length == 2 && "toggle".equalsIgnoreCase(args[1])) {
                boolean enabled = ExtendedMessages.toggleMessagePrefixEnabled();

                sendLine(sender, "Message prefix: " + formatState(enabled));

                return;
            }

            if (args.length == 2 && "remove".equalsIgnoreCase(args[1])) {
                ExtendedMessages.setMessagePrefix("");
                sendLine(sender, "Message prefix removed");
                return;
            }

            if (args.length >= 3 && "set".equalsIgnoreCase(args[1])) {
                String prefix = joinArgs(args, 2);

                try {
                    ExtendedMessages.setMessagePrefix(prefix);
                } catch (IllegalArgumentException exception) {
                    throw new CommandException(
                        exception.getMessage()
                    );
                }

                sendLine(sender, 
                    "Message prefix set to " + formatValue(ExtendedMessages.getMessagePrefix())
                );

                return;
            }

            throw new WrongUsageException(
                "/em prefix [toggle|set <text>|remove]"
            );
        }

    private void handlePartyCommand(
        ICommandSender sender, String[] args) throws CommandException {

            if (args.length == 1) {
                showPartyStatus(sender);
                return;
            }

            if (args.length == 2 && "toggle".equalsIgnoreCase(args[1])) {
                boolean enabled = ExtendedMessages.toggleCommandPrefixEnabled();
                sendLine(sender, "Party command: " + formatState(enabled));
                return;
            }

            if (args.length >= 3 && "set".equalsIgnoreCase(args[1])) {
                String commandPrefix = joinArgs(args, 2);

                try {
                    ExtendedMessages.setCommandPrefix(commandPrefix);
                } catch (IllegalArgumentException exception) {
                    throw new CommandException(
                        exception.getMessage()
                    );
                }

                sendLine(sender, 
                    "Party command set to " + formatValue(
                        ExtendedMessages.getCommandPrefix()));

                return;
            }

            throw new WrongUsageException("/em party [toggle|set <command>]");
        }

    private String joinArgs(String[] args, int startIndex) {
        StringBuilder result = new StringBuilder();

        for (int i = startIndex; i < args.length; i++) {
            if (i > startIndex) {
                result.append(" ");
            }

            result.append(args[i]);
        }

        return result.toString();
    }

    private void showStatus(ICommandSender sender) {
        sender.addChatMessage(new ChatComponentText(
            EnumChatFormatting.AQUA
                + "[" + Reference.NAME + "]"
        ));

        sender.addChatMessage(new ChatComponentText(
            EnumChatFormatting.GRAY
                + "Remove 100-character limit: "
                + formatState(ExtendedMessages.isEnabled())
        ));

        sender.addChatMessage(new ChatComponentText(
            EnumChatFormatting.GRAY
                + "Split long messages: "
                + formatState(ExtendedMessages.isSplitEnabled())
        ));

        int delay = ExtendedMessages.getMessageDelaySeconds();

        sender.addChatMessage(new ChatComponentText(
            EnumChatFormatting.GRAY
                + "Message delay: "
                + EnumChatFormatting.AQUA
                + delay
                + EnumChatFormatting.GRAY
                + (delay == 1 ? " second" : " seconds")
        ));

        sender.addChatMessage(new ChatComponentText(
            EnumChatFormatting.DARK_GRAY
                + "/em toggle"
        ));

        sender.addChatMessage(new ChatComponentText(
            EnumChatFormatting.DARK_GRAY
                + "/em split"
        ));

        sender.addChatMessage(new ChatComponentText(
            EnumChatFormatting.DARK_GRAY
                + "/em delay <seconds>"
        ));

        sender.addChatMessage(new ChatComponentText(
            EnumChatFormatting.GRAY + "Message prefix: "
                + formatState(ExtendedMessages.isMessagePrefixEnabled())
                + EnumChatFormatting.GRAY
                + " "
                + formatValue(ExtendedMessages.getMessagePrefix())
        ));

        sender.addChatMessage(new ChatComponentText(
            EnumChatFormatting.GRAY + "Party command: "
                + formatState(ExtendedMessages.isCommandPrefixEnabled())
                + EnumChatFormatting.GRAY
                + " "
                + formatValue(ExtendedMessages.getCommandPrefix())
        ));
    }

    private void showPrefixStatus(ICommandSender sender) {
        sendLine(sender, "Message prefix: "
            + formatState(
                ExtendedMessages.isMessagePrefixEnabled()
            )
        );

        sendLine(sender, "Message prefix value: "
            + formatValue(
                ExtendedMessages.getMessagePrefix()
            )
        );
    }

    private void showPartyStatus(ICommandSender sender) {
        sendLine(sender, "Party command: "
            + formatState(
                ExtendedMessages.isCommandPrefixEnabled()
            )
        );

        sendLine(sender, "Party command value: "
            + formatValue(
                ExtendedMessages.getCommandPrefix()
            )
        );
    }

    private void sendLine(ICommandSender sender, String message) {
        sender.addChatMessage(new ChatComponentText(
            EnumChatFormatting.GRAY
                + "[" + Reference.NAME + "] "
                + message
        ));
    }

    private String formatState(boolean enabled) {
        return enabled ? EnumChatFormatting.GREEN + "ON" : EnumChatFormatting.RED + "OFF";
    }

    private String formatValue(String value) {
        if (value.isEmpty()) {
            return EnumChatFormatting.DARK_GRAY + "<empty>";
        }
        return EnumChatFormatting.AQUA + "\"" + value + "\"";
    }
}
