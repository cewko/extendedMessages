package com.github.cewko.extendedmessages.mixin;

import com.github.cewko.extendedmessages.ExtendedMessages;
import net.minecraft.client.gui.GuiChat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(GuiChat.class)
public abstract class MixinGuiChat {

    @ModifyConstant(
        method = "initGui",
        constant = @Constant(intValue = 100)
    )
    private int extendedmessages$modifyChatLimit(int originalLimit) {
        return ExtendedMessages.isEnabled() ? 256 : originalLimit;
    }
}