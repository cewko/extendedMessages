package com.github.cewko.enhanmess.mixin;

import com.github.cewko.enhanmess.EnhanMess;
import com.github.cewko.enhanmess.Reference;
import net.minecraft.client.gui.GuiChat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(GuiChat.class)
public abstract class MixinGuiChat {
    @ModifyConstant(
        method = "initGui",
        constant = @Constant(intValue = Reference.DEFAULT_MESSAGE_LIMIT)
    )
    private int enhanmess$replaceVanillaChatLimit(int defaultLimit) {
        // GuiChat.initGui creates chat input box with vanilla's 100 char limit
        // when mod is enabled we replace that limit with our extended one
        return EnhanMess.getCurrentMessageLimit();
    }
}