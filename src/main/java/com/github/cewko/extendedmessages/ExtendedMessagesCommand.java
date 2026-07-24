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
    private interface BooleanToggle {
        boolean toggle();
    }

    private interface StringSetter {
        String set(String value);
    }

    private interface IntSetter {
        void set(int value);
    }

    private interface IntFormatter {
        String format(int value);
    }

    private void handleIntSet(
        ICommandSender sender,
        String[] args,
        String usage,
        String displayName,
        int min,
        int max,
        IntSetter setter,
        IntFormatter formatter
    ) throws CommandException {
        if (args.length != 3 || !"set".equalsIgnoreCase(args[1])) {
            throw new WrongUsageException(usage);
        }

        int value = parseInt(args[2], min, max);

        try {
            setter.set(value);
        } catch (IllegalArgumentException exception) {
            throw new CommandException(exception.getMessage());
        }

        sendLine(sender, displayName + ": " + formatter.format(value));
    }

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
        return "/em | /em toggle | /em mp <toggle|set <prefix>>"
            + " | /em cp <toggle|set <command>>"
            + " | /em mc set <seconds>"
            + " | /em cc set <seconds>"
            + " | /em h set <lines>";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args)
        throws CommandException {

        if (args.length == 0) {
            GuiOpener.getInstance().requestOpen();
            return;
        }

        switch (args[0].toLowerCase()) {
            case "toggle":
                handleMainToggle(sender, args);
                return;
            case "mp":
                handlePrefixSetting(
                    sender,
                    args,
                    "/em mp <toggle|set <prefix>>",
                    "Message prefix",
                    new BooleanToggle() {
                        @Override
                        public boolean toggle() {
                            return ExtendedMessages.toggleMessagePrefixEnabled();
                        }
                    },
                    new StringSetter() {
                        @Override
                        public String set(String value) {
                            ExtendedMessages.setMessagePrefix(value);
                            return ExtendedMessages.getMessagePrefix();
                        }
                    }
                );
                return;
            case "cp":
                handlePrefixSetting(
                    sender,
                    args,
                    "/em cp <toggle|set <command>>",
                    "Command prefix",
                    new BooleanToggle() {
                        @Override
                        public boolean toggle() {
                            return ExtendedMessages.toggleCommandPrefixEnabled();
                        }
                    },
                    new StringSetter() {
                        @Override
                        public String set(String value) {
                            ExtendedMessages.setCommandPrefix(value);
                            return ExtendedMessages.getCommandPrefix();
                        }
                    }
                );
                return;
            case "mc":
                handleIntSet(
                    sender,
                    args,
                    "/em mc set <seconds>",
                    "Message cooldown",
                    Reference.MIN_DELAY_SECONDS,
                    Reference.MAX_DELAY_SECONDS,
                    new IntSetter() {
                        @Override
                        public void set(int value) {
                            ExtendedMessages.setMessageDelaySeconds(value);
                        }
                    },
                    new IntFormatter() {
                        @Override
                        public String format(int value) {
                            return EnumChatFormatting.AQUA + formatSeconds(value);
                        }
                    }
                );
                return;
            case "cc":
                handleIntSet(
                    sender,
                    args,
                    "/em cc set <seconds>",
                    "Command cooldown",
                    Reference.MIN_DELAY_SECONDS,
                    Reference.MAX_DELAY_SECONDS,
                    new IntSetter() {
                        @Override
                        public void set(int value) {
                            ExtendedMessages.setCommandDelaySeconds(value);
                        }
                    },
                    new IntFormatter() {
                        @Override
                        public String format(int value) {
                            return EnumChatFormatting.AQUA + formatSeconds(value);
                        }
                    }
                );
                return;
            case "h":
                handleIntSet(
                    sender,
                    args,
                    "/em h set <lines>",
                    "History length",
                    Reference.MIN_MESSAGE_HISTORY_LENGTH,
                    Reference.MAX_MESSAGE_HISTORY_LENGTH,
                    new IntSetter() {
                        @Override
                        public void set(int value) {
                            ExtendedMessages.setMessageHistoryLength(value);
                        }
                    },
                    new IntFormatter() {
                        @Override
                        public String format(int value) {
                            return EnumChatFormatting.AQUA
                                + String.valueOf(value)
                                + EnumChatFormatting.GRAY
                                + " lines";
                        }
                    }
                );
                return;

            default:
                throw new WrongUsageException(getCommandUsage(sender));
        }
    }

    private void handleMainToggle(ICommandSender sender, String[] args)
        throws CommandException {

        if (args.length != 1) {
            throw new WrongUsageException("/em toggle");
        }

        boolean enabled = ExtendedMessages.toggleEnabled();
        sendLine(sender, "Remove 100-character limit: " + formatState(enabled));
    }

    private void handlePrefixSetting(
        ICommandSender sender,
        String[] args,
        String usage,
        String displayName,
        BooleanToggle toggle,
        StringSetter setter
    ) throws CommandException {
        if (args.length < 2) {
            throw new WrongUsageException(usage);
        }

        switch (args[1].toLowerCase()) {
            case "toggle":
                if (args.length != 2) {
                    throw new WrongUsageException(usage);
                }

                boolean enabled = toggle.toggle();
                sendLine(sender, displayName + ": " + formatState(enabled));
                return;

            case "set":
                if (args.length < 3) {
                    throw new WrongUsageException(usage);
                }

                handleTextSet(sender, args, displayName, setter);
                return;

            default:
                throw new WrongUsageException(usage);
        }
    }

    private void handleTextSet(
        ICommandSender sender,
        String[] args,
        String displayName,
        StringSetter setter
    ) throws CommandException {
        String value = joinArgs(args, 2);
        String storedValue;

        try {
            storedValue = setter.set(value);
        } catch (IllegalArgumentException exception) {
            throw new CommandException(exception.getMessage());
        }

        sendLine(sender, displayName + " set to " + formatValue(storedValue));
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
