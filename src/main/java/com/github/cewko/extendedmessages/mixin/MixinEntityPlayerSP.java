package com.github.cewko.extendedmessages.mixin;

import com.github.cewko.extendedmessages.ExtendedMessages;
import com.github.cewko.extendedmessages.messaging.OutgoingMessageProcessor;
import com.github.cewko.extendedmessages.messaging.MessageQueue;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;

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

        if (OutgoingMessageProcessor.shouldBypass(message)) {
            return;
        }

        callback.cancel();
        OutgoingMessageProcessor.handle(Minecraft.getMinecraft(), message);
    }
}
