package com.github.cewko.extendedmessages.mixin;

import com.github.cewko.extendedmessages.ExtendedMessages;
import com.github.cewko.extendedmessages.Reference;
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

    private int extendedmessages$modifyOutgoingLimit(int defaultLimit) {
        return ExtendedMessages.getCurrentMessageLimit();
    }
}