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

    public void enqueueRegular(String message) {
        String messagePrefix = ExtendedMessages.isMessagePrefixEnabled()
            ? withTrailingSpace(ExtendedMessages.getMessagePrefix())
            : "";

        enqueueWithPrefix(messagePrefix, message);
    }

    public void enqueueCommand(String commandPrefix, String message) {
        enqueueWithPrefix(withTrailingSpace(commandPrefix), message);
    }

    public void sendDirect(String message) {
        Minecraft minecraft = Minecraft.getMinecraft();

        if (minecraft.thePlayer == null) {
            return;
        }

        sendingQueuedMessage = true;

        try {
            minecraft.thePlayer.sendChatMessage(message);
        } finally {
            sendingQueuedMessage = false;
        }
    }

    private void enqueueWithPrefix(String prefix, String message) {
        Minecraft minecraft = Minecraft.getMinecraft();

        if (minecraft.thePlayer == null || minecraft.theWorld == null) {
            return;
        }

        if (queuedWorld != null && queuedWorld != minecraft.theWorld) {
            clear();
        }

        int bodyLimit = Reference.DEFAULT_MESSAGE_LIMIT - prefix.length();

        if (bodyLimit <= 0) {
            return;
        }

        boolean sendImmediately = chunks.isEmpty() 
            && System.currentTimeMillis() >= nextSendAtMillis;

        queuedWorld = minecraft.theWorld;

        for (int start = 0; start < message.length(); start += bodyLimit) {
            int end = Math.min(start + bodyLimit, message.length());

            chunks.addLast(prefix + message.substring(start, end));
        }

        if (sendImmediately) {
            sendNext(minecraft);
        }
    }

    private String withTrailingSpace(String prefix) {
        String trimmed = prefix.trim();

        if (trimmed.isEmpty()) {
            return "";
        }

        return trimmed + " ";
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
