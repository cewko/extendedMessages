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

    public enum Lane {
        REGULAR,
        CONFIGURED_COMMAND
    }

    private final Deque<String> regularLines = new ArrayDeque<String>();
    private final Deque<String> commandLines = new ArrayDeque<String>();

    private long nextRegularSendAtMillis;
    private long nextCommandSendAtMillis;

    private World queuedWorld;
    private boolean sendingQueuedMessage;

    private MessageQueue() {
    }

    public static MessageQueue getInstance() {
        return INSTANCE;
    }

    public void enqueueLines(List<String> lines, Lane lane) {
        Minecraft minecraft = Minecraft.getMinecraft();

        if (lines.isEmpty() || minecraft.thePlayer == null || minecraft.theWorld == null) {
            return;
        }

        if (queuedWorld != null && queuedWorld != minecraft.theWorld) {
            clear();
        }

        queuedWorld = minecraft.theWorld;
        getQueue(lane).addAll(lines);

        trySendReady(minecraft);
    }

    private Deque<String> getQueue(Lane lane) {
        return lane == Lane.REGULAR ? regularLines : commandLines;
    }

    private void trySendReady(Minecraft minecraft) {
        trySendLane(minecraft, Lane.REGULAR);
        trySendLane(minecraft, Lane.CONFIGURED_COMMAND);

        if (isEmpty()) {
            queuedWorld = null;
        }
    }

    private void trySendLane(Minecraft minecraft, Lane lane) {
        Deque<String> queue = getQueue(lane);

        if (queue.isEmpty() || System.currentTimeMillis() < getNextSendAt(lane)) {
            return;
        }

        String line = queue.pollFirst();

        sendingQueuedMessage = true;

        try {
            minecraft.thePlayer.sendChatMessage(line);
        } finally {
            sendingQueuedMessage = false;
        }

        setNextSendAt(
            lane, System.currentTimeMillis() + getDelaySeconds(lane) * 1000L
        );
    }

    private long getNextSendAt(Lane lane) {
        return lane == Lane.REGULAR ? nextRegularSendAtMillis : nextCommandSendAtMillis;
    }

    private void setNextSendAt(Lane lane, long value) {
        if (lane == Lane.REGULAR) {
            nextRegularSendAtMillis = value;
        } else {
            nextCommandSendAtMillis = value;
        }
    }

    private int getDelaySeconds(Lane lane) {
        return lane == Lane.REGULAR
            ? ExtendedMessages.getMessageDelaySeconds()
            : ExtendedMessages.getCommandDelaySeconds();
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END || isEmpty()) {
            return;
        }

        Minecraft minecraft = Minecraft.getMinecraft();

        if (minecraft.thePlayer == null || minecraft.theWorld == null
                || minecraft.theWorld != queuedWorld || !ExtendedMessages.isEnabled()) {
            clear();
            return;
        }

        trySendReady(minecraft);
    }

    private boolean isEmpty() {
        return regularLines.isEmpty() && commandLines.isEmpty();
    }

    public void clear() {
        regularLines.clear();
        commandLines.clear();
        queuedWorld = null;
        nextRegularSendAtMillis = 0L;
        nextCommandSendAtMillis = 0L;
    }

    public boolean isSendingQueuedMessage() {
        return sendingQueuedMessage;
    }
}
