package com.github.cewko.enhanmess.mixin;

import com.github.cewko.enhanmess.EnhanMess;
import com.github.cewko.enhanmess.Reference;
import net.minecraft.network.play.client.C01PacketChatMessage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(C01PacketChatMessage.class)
public abstract class MixinC01PacketChatMessage {

    @ModifyConstant(
        method = "<init>(Ljava/lang/String;)V",
        constant = @Constant(intValue = Reference.DEFAULT_MESSAGE_LIMIT),
        require = 2
    )

    private int enhanmess$modifyOutgoingLimit(int defaultLimit) {
        return EnhanMess.getCurrentPacketMessageLimit();
    }
}
