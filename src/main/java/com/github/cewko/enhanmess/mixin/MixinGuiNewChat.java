package com.github.cewko.enhanmess.mixin;

import com.github.cewko.enhanmess.EnhanMess;
import com.github.cewko.enhanmess.Reference;
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
    private int enhanmess$replaceMessageHistoryLength(int vanillaLimit) {
        return EnhanMess.getCurrentMessageHistoryLength();
    }
}