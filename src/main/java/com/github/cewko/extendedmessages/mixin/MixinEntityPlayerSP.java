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
    private void extendedMessages$interceptMessage(
        String message,
        CallbackInfo callback
    ) {
        if (!ExtendedMessages.isEnabled() || message.length() <= 100) {
            return;
        }

        Minecraft minecraft = Minecraft.getMinecraft();

        if (minecraft.isSingleplayer() || !ExtendedMessages.isSplitEnabled()) {
            return;
        }

        callback.cancel();

        if (message.charAt(0) == '/') {
            minecraft.thePlayer.addChatMessage(
                new ChatComponentText(
                    EnumChatFormatting.RED + "[" + Reference.NAME + "]"
                    + "Commands longer than 100 characters cannot be split safely"
                )
            );

            minecraft.thePlayer.addChatMessage(
                new ChatComponentText(
                    EnumChatFormatting.GRAY + "Use /em split only if long commands are supported by the server"
                )
            );

            return;
        }

        MessageQueue.getInstance().enqueue(message);

    }
}