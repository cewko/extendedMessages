package com.github.cewko.enhanmess.mixin;

import com.github.cewko.enhanmess.EnhanMess;
import com.github.cewko.enhanmess.messaging.OutgoingMessageProcessor;
import com.github.cewko.enhanmess.messaging.MessageQueue;

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
    private void enhanmess$interceptMessage(
        String message,
        CallbackInfo callback
    ) {
        MessageQueue queue = MessageQueue.getInstance();

        if (queue.isSendingQueuedMessage()) {
            return;
        }

        if (!EnhanMess.isEnabled()) {
            return;
        }

        if (OutgoingMessageProcessor.shouldBypass(message)) {
            return;
        }

        callback.cancel();
        OutgoingMessageProcessor.handle(Minecraft.getMinecraft(), message);
    }
}
