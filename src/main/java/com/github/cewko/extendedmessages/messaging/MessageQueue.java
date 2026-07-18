package com.github.cewko.extendedmessages.messaging;

import com.github.cewko.extendedmessages.ExtendedMessages;
import com.github.cewko.extendedmessages.Reference;

import java.util.ArrayDeque;
import java.util.Deque;

import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public final class MessageQueue {
    private static final int PART_LENGTH = Reference.DEFAULT_MESSAGE_LIMIT;

    private static final MessageQueue INSTANCE = new MessageQueue();
    private final Deque<String> chunks = new ArrayDeque<String>();

    private World queuedWorld;
    private long nextSendAtMillis;

    private MessageQueue() {
    }

    public static MessageQueue getInstance() {
        return INSTANCE;
    }

    public void enqueue(String message) {
        Minecraft minecraft = Minecraft.getMinecraft();

        if (minecraft.thePlayer == null || minecraft.theWorld == null) {
            return;
        }

        if (queuedWorld != null && queuedWorld != minecraft.theWorld) {
            clear();
        }

        boolean sendImmediately = chunks.isEmpty();

        queuedWorld = minecraft.theWorld;

        for (int start = 0; start < message.length(); start += PART_LENGTH) {
            int end = Math.min(start + PART_LENGTH, message.length());

            chunks.addLast(message.substring(start, end));
        }

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
                || minecraft.theWorld != queuedWorld || !ExtendedMessages.isEnabled()
                || !ExtendedMessages.isSplitEnabled()) {
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

        minecraft.thePlayer.sendChatMessage(chunk);

        if (chunks.isEmpty()) {
            clear();
            return;
        }

        nextSendAtMillis = System.currentTimeMillis() + ExtendedMessages.getMessageDelaySeconds() * 1000L;
    }
}
