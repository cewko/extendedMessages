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
        return "/em [toggle|split|delay <1-30>]";
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

            int seconds = parseInt(args[1], 1, 30);

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

        throw new WrongUsageException(getCommandUsage(sender));
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
}
