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
        return "/em toggle";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) 
        throws CommandException {
            
        if (args.length != 1 || !"toggle".equalsIgnoreCase(args[0])) {
            throw new WrongUsageException(getCommandUsage(sender));
        }

        boolean enabled = ExtendedMessages.toggleEnabled();

        String stateText = enabled
            ? EnumChatFormatting.GREEN + "enabled"
            : EnumChatFormatting.RED + "disabled";

        sender.addChatMessage(new ChatComponentText (
            EnumChatFormatting.GRAY
                + "[" + Reference.NAME + "] " + stateText
        ));
    }
    
}
