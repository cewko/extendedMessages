package com.github.cewko.extendedmessages;

import com.github.cewko.extendedmessages.gui.GuiOpener;

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
        return Reference.COMMAND_NAME;
    }

    @Override
    public List<String> getCommandAliases() {
        return Collections.singletonList(Reference.COMMAND_ALIAS);
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/em [gui|toggle|split|delay <"
            + Reference.MIN_DELAY_SECONDS
            + "-"
            + Reference.MAX_DELAY_SECONDS
            + ">|history <"
            + Reference.MIN_MESSAGE_HISTORY_LENGTH
            + "-"
            + Reference.MAX_MESSAGE_HISTORY_LENGTH
            + ">|prefix <toggle|set|remove>|command <toggle|set <command>|delay <seconds>>]";
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

        if (args.length == 1 && "gui".equalsIgnoreCase(args[0])) {
            GuiOpener.getInstance().requestOpen();
            return;
        }

        if (args.length == 1 && "toggle".equalsIgnoreCase(args[0])) {
            boolean enabled = ExtendedMessages.toggleEnabled();
            sendLine(sender, "Remove 100-character limit: " + formatState(enabled));

            return;
        }

        if (args.length == 1 && "split".equalsIgnoreCase(args[0])) {
            boolean enabled = ExtendedMessages.toggleSplitEnabled();

            sendLine(sender, "Split long messages: " + formatState(enabled)
                + formatSplitRisk(enabled)
            );

            return;
        }

        if (args.length == 2 && "delay".equalsIgnoreCase(args[0])) {

            int seconds = parseInt(
                args[1],
                Reference.MIN_DELAY_SECONDS,
                Reference.MAX_DELAY_SECONDS
            );

            ExtendedMessages.setMessageDelaySeconds(seconds);

            sendLine(
                sender,
                "Message delay: "
                    + EnumChatFormatting.AQUA
                    + formatSeconds(seconds)
            );

            return;
        }

        if (args.length == 2 && "history".equalsIgnoreCase(args[0])) {
            int length = parseInt(
                args[1],
                Reference.MIN_MESSAGE_HISTORY_LENGTH,
                Reference.MAX_MESSAGE_HISTORY_LENGTH
            );

            ExtendedMessages.setMessageHistoryLength(length);

            sendLine(
                sender,
                "message history length: "
                + EnumChatFormatting.AQUA
                + length
                + EnumChatFormatting.GRAY
                + " lines"
            );

            return;
        }

        if ("prefix".equalsIgnoreCase(args[0])) {
            handlePrefixCommand(sender, args);
            return;
        }

        if ("command".equalsIgnoreCase(args[0])) {
            handleCommandSettings(sender, args);
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

    private void handleCommandSettings(
        ICommandSender sender, String[] args) throws CommandException {

            if (args.length == 1) {
                showConfiguredCommandStatus(sender);
                return;
            }

            if (args.length == 2 && "toggle".equalsIgnoreCase(args[1])) {
                boolean enabled = ExtendedMessages.toggleCommandPrefixEnabled();
                sendLine(sender, "Configured command: " + formatState(enabled));
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
                    "Configured command set to " + formatValue(
                        ExtendedMessages.getCommandPrefix()));

                return;
            }

            if (args.length == 3 && "delay".equalsIgnoreCase(args[1])) {
                int seconds = parseInt(
                    args[2],
                    Reference.MIN_DELAY_SECONDS,
                    Reference.MAX_DELAY_SECONDS
                );

                ExtendedMessages.setCommandDelaySeconds(seconds);

                sendLine(
                    sender,
                    "Command delay: "
                        + EnumChatFormatting.AQUA
                        + formatSeconds(seconds)
                );

                return;
            }

            throw new WrongUsageException(
                "/em command [toggle|set <command>|delay <seconds>]"
            );
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
                + formatSplitRisk(ExtendedMessages.isSplitEnabled())
        ));

        int delay = ExtendedMessages.getMessageDelaySeconds();
        int commandDelay = ExtendedMessages.getCommandDelaySeconds();

        sender.addChatMessage(new ChatComponentText(
            EnumChatFormatting.GRAY
                + "Message delay: "
                + EnumChatFormatting.AQUA
                + formatSeconds(delay)
        ));

        sender.addChatMessage(new ChatComponentText(
            EnumChatFormatting.GRAY
                + "Command delay: "
                + EnumChatFormatting.AQUA
                + formatSeconds(commandDelay)
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
            EnumChatFormatting.DARK_GRAY
                + "/em command delay <seconds>"
        ));

        sender.addChatMessage(new ChatComponentText(
            EnumChatFormatting.DARK_GRAY
                + "/em history <lines>"
        ));

        sender.addChatMessage(new ChatComponentText(
            EnumChatFormatting.GRAY + "Message prefix: "
                + formatState(ExtendedMessages.isMessagePrefixEnabled())
                + EnumChatFormatting.GRAY
                + " "
                + formatValue(ExtendedMessages.getMessagePrefix())
        ));

        sender.addChatMessage(new ChatComponentText(
            EnumChatFormatting.GRAY + "Configured command: "
                + formatState(ExtendedMessages.isCommandPrefixEnabled())
                + EnumChatFormatting.GRAY
                + " "
                + formatValue(ExtendedMessages.getCommandPrefix())
        ));

        sender.addChatMessage(new ChatComponentText(
            EnumChatFormatting.GRAY
                + "Message history length: "
                + EnumChatFormatting.AQUA
                + ExtendedMessages.getMessageHistoryLength()
                + EnumChatFormatting.GRAY
                + " lines"
        ));

        sender.addChatMessage(new ChatComponentText(
            EnumChatFormatting.DARK_GRAY
                + "/em gui"
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

    private void showConfiguredCommandStatus(ICommandSender sender) {
        sendLine(sender, "Configured command: "
            + formatState(
                ExtendedMessages.isCommandPrefixEnabled()
            )
        );

        sendLine(sender, "Configured command value: "
            + formatValue(
                ExtendedMessages.getCommandPrefix()
            )
        );

        sendLine(sender, "Command delay: "
            + EnumChatFormatting.AQUA
            + formatSeconds(
                ExtendedMessages.getCommandDelaySeconds()
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

    private String formatSplitRisk(boolean splitEnabled) {
        if (splitEnabled) {
            return "";
        }

        return EnumChatFormatting.RED
            + " (unsafe; servers might kick you)";
    }

    private String formatSeconds(int seconds) {
        return String.valueOf(seconds)
            + EnumChatFormatting.GRAY
            + (seconds == 1 ? " second" : " seconds");
    }

    private String formatValue(String value) {
        if (value.isEmpty()) {
            return EnumChatFormatting.DARK_GRAY + "<empty>";
        }
        return EnumChatFormatting.AQUA + "\"" + value + "\"";
    }
}
