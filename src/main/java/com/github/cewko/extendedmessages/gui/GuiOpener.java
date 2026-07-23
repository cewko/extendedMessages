package com.github.cewko.extendedmessages.gui;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public final class GuiOpener {
    private static final GuiOpener INSTANCE = new GuiOpener();

    private boolean openRequested;

    private GuiOpener() {
    }

    public static GuiOpener getInstance() {
        return INSTANCE;
    }

    public void requestOpen() {
        openRequested = true;
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !openRequested) {
            return;
        }

        openRequested = false;

        Minecraft minecraft = Minecraft.getMinecraft();

        if (minecraft.thePlayer == null || minecraft.theWorld == null) {
            return;
        }

        minecraft.displayGuiScreen(new ExtendedMessagesGui());
    }
}