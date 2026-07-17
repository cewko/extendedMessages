package com.github.cewko.extendedmessages;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(
    modid = Reference.MOD_ID,
    name = Reference.NAME,
    version = Reference.VERSION,
    acceptedMinecraftVersions = Reference.ACCEPTED_VERSIONS,
    clientSideOnly = true,
    useMetadata = true
)
public class ExtendedMessages {

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        System.out.println("[" + Reference.NAME + "] Mod has loaded :3");
    }
}