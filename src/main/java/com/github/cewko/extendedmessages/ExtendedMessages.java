package com.github.cewko.extendedmessages;

import com.github.cewko.extendedmessages.config.ExtendedMessagesConfig;
import com.github.cewko.extendedmessages.messaging.MessageQueue;

import net.minecraftforge.common.MinecraftForge;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
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
    private static boolean splitEnabled = true;
    private static int messageDelaySeconds = 3;

    public static boolean isEnabled() {
        return enabled;
    }

    public static boolean isSplitEnabled() {
        return splitEnabled;
    }

    public static boolean toggleSplitEnabled() {
        splitEnabled = !splitEnabled;
        ExtendedMessagesConfig.saveSplitEnabled(splitEnabled);

        if (!splitEnabled) {
            // clear message queue
        }

        return splitEnabled;
    }

    public static boolean toggleEnabled() {
        enabled = !enabled;
        ExtendedMessagesConfig.saveEnabled(enabled);

        if (!enabled) {
            // clear message queue
        }

        return enabled;
    }

    public static int getMessageDelaySeconds() {
        return messageDelaySeconds;
    }

    public static void setMessageDelaySeconds(int seconds) {
        if (seconds < 1 || seconds > 30) {
            throw new IllegalArgumentException(
                "Message delay must be between 1 and 30 secs"
            );
        }

        messageDelaySeconds = seconds;
        ExtendedMessagesConfig.saveMessageDelaySeconds(seconds);
    }

    public static int getCurrentMessageLimit() {
        return enabled
            ? Reference.EXTENDED_MESSAGE_LIMIT
            : Reference.DEFAULT_MESSAGE_LIMIT;
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ExtendedMessagesConfig.load(
            event.getSuggestedConfigurationFile()
        );

        enabled = ExtendedMessagesConfig.getEnabled();
        splitEnabled = ExtendedMessagesConfig.getSplitEnabled();
        messageDelaySeconds = ExtendedMessagesConfig.getMessageDelaySeconds();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        ClientCommandHandler.instance.registerCommand(
            new ExtendedMessagesCommand()
        );

        MinecraftForge.EVENT_BUS.register(MessageQueue.getInstance());
        
        System.out.println("[" + Reference.NAME + "] Mod has loaded :3");
    }
}