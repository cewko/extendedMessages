package com.github.cewko.extendedmessages.messaging;

import com.github.cewko.extendedmessages.ExtendedMessages;
import com.github.cewko.extendedmessages.Reference;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.TimeUnit;

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

    private long nextRegularSendAtNanos;
    private long nextCommandSendAtNanos;

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
    }

    private void trySendLane(Minecraft minecraft, Lane lane) {
        Deque<String> queue = getQueue(lane);
        long deadline = getNextSendAtNanos(lane);
        long now = System.nanoTime();

        if (queue.isEmpty() || (deadline != 0L && now - deadline < 0L)) {
            return;
        }

        String line = queue.pollFirst();

        sendingQueuedMessage = true;

        try {
            minecraft.thePlayer.sendChatMessage(line);
        } finally {
            sendingQueuedMessage = false;
        }

        setNextSendAtNanos(
            lane,
            System.nanoTime() + getCooldownNanos(lane)
        );
    }

    private long getNextSendAtNanos(Lane lane) {
        return lane == Lane.REGULAR
            ? nextRegularSendAtNanos
            : nextCommandSendAtNanos;
    }

    private void setNextSendAtNanos(Lane lane, long value) {
        if (lane == Lane.REGULAR) {
            nextRegularSendAtNanos = value;
        } else {
            nextCommandSendAtNanos = value;
        }
    }

    private long getCooldownNanos(Lane lane) {
        int delaySeconds = getDelaySeconds(lane);
        long cooldownNanos = TimeUnit.SECONDS.toNanos(delaySeconds);

        if (delaySeconds > 0) {
            cooldownNanos += TimeUnit.MILLISECONDS.toNanos(
                Reference.SEND_SAFETY_MARGIN_MILLIS
            );
        }

        return cooldownNanos;
    }

    private int getDelaySeconds(Lane lane) {
        return lane == Lane.REGULAR
            ? ExtendedMessages.getMessageDelaySeconds()
            : ExtendedMessages.getCommandDelaySeconds();
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        Minecraft minecraft = Minecraft.getMinecraft();

        if (queuedWorld != null && minecraft.theWorld != queuedWorld) {
            clear();
        }

        if (isEmpty()) {
            return;
        }

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
        nextRegularSendAtNanos = 0L;
        nextCommandSendAtNanos = 0L;
    }

    public boolean isSendingQueuedMessage() {
        return sendingQueuedMessage;
    }
}
