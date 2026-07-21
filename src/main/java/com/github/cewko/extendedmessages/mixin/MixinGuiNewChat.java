package com.github.cewko.extendedmessages.mixin;

import com.github.cewko.extendedmessages.ExtendedMessages;
import com.github.cewko.extendedmessages.Reference;
import net.minecraft.client.gui.GuiNewChat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(GuiNewChat.class)
public abstract class MixinGuiNewChat {
    @ModifyConstant(
        method = "setChatLine",
        constant = @Constant(intValue = Reference.VANILLA_MESSAGE_HISTORY_LENGTH),
        require = 2
    )
    private int extendedmessages$replaceMessageHistoryLength(int vanillaLimit) {
        return ExtendedMessages.getCurrentMessageHistoryLength();
    }
}