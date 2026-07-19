package com.github.cewko.extendedmessages.mixin;

import com.github.cewko.extendedmessages.ExtendedMessages;
import com.github.cewko.extendedmessages.Reference;
import com.github.cewko.extendedmessages.messaging.MessageQueue;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityPlayerSP.class)
public abstract class MixinEntityPlayerSP {

    @Inject(
        method = "sendChatMessage",
        at = @At("HEAD"),
        cancellable = true,
        require = 1
    )
    private void extendedmessages$interceptMessage(
        String message,
        CallbackInfo callback
    ) {
        MessageQueue queue = MessageQueue.getInstance();

        if (queue.isSendingQueuedMessage()) {
            return;
        }

        if (!ExtendedMessages.isEnabled()) {
            return;
        }

        Minecraft minecraft = Minecraft.getMinecraft();

        boolean directMode = !ExtendedMessages.isSplitEnabled();
        boolean queueBusy = !directMode && queue.shouldQueueNewMessages();

        if (handleConfiguredCommand(minecraft, message, callback, directMode, queueBusy)) {
            return;
        }

        if (queueBusy) {
            if (message.startsWith("/")) {
                callback.cancel();
                sendWarning(minecraft, "Wait for queued msgs to finish before using commands");
                return;
            }

            callback.cancel();
            queue.enqueueRegular(message);
            return;
        }

        if (message.startsWith("/") && message.length() > Reference.DEFAULT_MESSAGE_LIMIT
                && !directMode) {
            callback.cancel();
            sendWarning(minecraft, "Long commands cannot be split safely");
            return;
        }

        if (message.startsWith("/")) {
            return;
        }

        String messagePrefix = ExtendedMessages.isMessagePrefixEnabled()
            ? withTrailingSpace(ExtendedMessages.getMessagePrefix())
            : "";

        boolean needsPrefix = !messagePrefix.isEmpty();
        boolean tooLongForVanilla =
            messagePrefix.length() + message.length() > Reference.DEFAULT_MESSAGE_LIMIT;

        if (!needsPrefix && (!tooLongForVanilla || directMode)) {
            return;
        }

        if (directMode) {
            sendDirectWithPrefix(minecraft, callback, messagePrefix, message);
            return;
        }

        callback.cancel();
        queue.enqueueRegular(message);
    }

    private boolean handleConfiguredCommand(
        Minecraft minecraft, String message,
        CallbackInfo callback, boolean directMode, boolean queueBusy
    ) {
        if (!ExtendedMessages.isCommandPrefixEnabled()) {
            return false;
        }

        String commandPrefix = ExtendedMessages.getCommandPrefix();

        if (!startsWithCommand(message, commandPrefix)) {
            return false;
        }

        boolean tooLong = message.length() > Reference.DEFAULT_MESSAGE_LIMIT;

        if (directMode || (!tooLong && !queueBusy)) {
            return false;
        }

        String commandWithSpace = withTrailingSpace(commandPrefix);
        String body = message.substring(commandWithSpace.length());

        callback.cancel();
        MessageQueue.getInstance().enqueueCommand(commandPrefix, body);

        return true;
    }

    private void sendDirectWithPrefix(
        Minecraft minecraft, CallbackInfo callback,
        String prefix, String message
    ) {
        String prefixedMessage = prefix + message;

        if (prefixedMessage.length() > Reference.EXTENDED_MESSAGE_LIMIT) {
            callback.cancel();
            sendWarning(minecraft, "Prefixed message is longer than "
                + Reference.EXTENDED_MESSAGE_LIMIT
                + " characters"
            );
            return;
        }

        callback.cancel();
        MessageQueue.getInstance().sendDirect(prefixedMessage);
    }

    private boolean startsWithCommand(String message, String command) {
        String commandWithSpace = withTrailingSpace(command);

        return !commandWithSpace.isEmpty()
            && message.regionMatches(
                true, 0, commandWithSpace, 0, commandWithSpace.length()
            );
    }

    private String withTrailingSpace(String prefix) {
        String trimmed = prefix.trim();

        if (trimmed.isEmpty()) {
            return "";
        }

        return trimmed + " ";
    }

    private void sendWarning(Minecraft minecraft, String message) {
        minecraft.thePlayer.addChatMessage(new ChatComponentText(
            EnumChatFormatting.RED
                + "[" + Reference.NAME + "] " + message
        ));
    }
}
