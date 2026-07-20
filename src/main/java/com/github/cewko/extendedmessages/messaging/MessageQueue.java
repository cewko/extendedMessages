package com.github.cewko.extendedmessages.messaging;

import com.github.cewko.extendedmessages.ExtendedMessages;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public final class MessageQueue {
    private static final MessageQueue INSTANCE = new MessageQueue();
    private final Deque<String> chunks = new ArrayDeque<String>();

    private World queuedWorld;
    private long nextSendAtMillis;
    private boolean sendingQueuedMessage;

    private MessageQueue() {
    }

    public static MessageQueue getInstance() {
        return INSTANCE;
    }

    public void enqueueLines(List<String> lines) {
        Minecraft minecraft = Minecraft.getMinecraft();

        if (lines.isEmpty() || minecraft.thePlayer == null || minecraft.theWorld == null) {
            return;
        }

        if (queuedWorld != null && queuedWorld != minecraft.theWorld) {
            clear();
        }

        boolean sendImmediately = chunks.isEmpty() 
            && System.currentTimeMillis() >= nextSendAtMillis;

        queuedWorld = minecraft.theWorld;
        chunks.addAll(lines);

        if (sendImmediately) {
            sendNext(minecraft);
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END || chunks.isEmpty()) {
            return;
        }

        Minecraft minecraft = Minecraft.getMinecraft();

        if (minecraft.thePlayer == null || minecraft.theWorld == null
                || minecraft.theWorld != queuedWorld || !ExtendedMessages.isEnabled()) {
            clear();
            return;
        }

        if (System.currentTimeMillis() >= nextSendAtMillis) {
            sendNext(minecraft);
        }
    }

    public void clear() {
        chunks.clear();
        queuedWorld = null;
        nextSendAtMillis = 0L;
    }

    private void sendNext(Minecraft minecraft) {
        String chunk = chunks.pollFirst();

        if (chunk == null) {
            clear();
            return;
        }

        sendingQueuedMessage = true;

        try {
            minecraft.thePlayer.sendChatMessage(chunk);
        } finally {
            sendingQueuedMessage = false;
        }

        nextSendAtMillis = System.currentTimeMillis() + ExtendedMessages.getMessageDelaySeconds() * 1000L;

        if (chunks.isEmpty()) {
            queuedWorld = null;
            return;
        }

    }

    public boolean isSendingQueuedMessage() {
        return sendingQueuedMessage;
    }

    public boolean shouldQueueNewMessages() {
        return !chunks.isEmpty() || System.currentTimeMillis() < nextSendAtMillis;
    }
}
