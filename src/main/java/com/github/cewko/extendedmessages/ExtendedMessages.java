package com.github.cewko.extendedmessages;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

import net.minecraftforge.client.ClientCommandHandler;

@Mod(
    modid = Reference.MOD_ID,
    name = Reference.NAME,
    version = Reference.VERSION,
    acceptedMinecraftVersions = Reference.ACCEPTED_VERSIONS,
    clientSideOnly = true,
    useMetadata = true
)
public class ExtendedMessages {

    public static boolean enabled = false;

    public static boolean isEnabled() {
        return enabled;
    }

    public static boolean toggleEnabled() {
        enabled = !enabled;
        return enabled;
    }

    public static int getCurrentMessageLimit() {
        return enabled
            ? Reference.EXTENDED_MESSAGE_LIMIT
            : Reference.DEFAULT_MESSAGE_LIMIT;
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        ClientCommandHandler.instance.registerCommand(
            new ExtendedMessagesCommand()
        );

        System.out.println("[" + Reference.NAME + "] Mod has loaded :3");
    }
}