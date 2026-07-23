package com.github.cewko.extendedmessages.gui.control;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

public final class StyledToggleButton extends GuiButton {
    public interface Value {
        boolean get();

        void toggle();
    }

    private final String label;
    private final Value value;

    public StyledToggleButton(
        int id,
        int x,
        int y,
        int width,
        int height,
        String label,
        Value value
    ) {
        super(id, x, y, width, height, "");
        this.label = label;
        this.value = value;
        updateText();
    }

    @Override
    public boolean mousePressed(Minecraft minecraft, int mouseX, int mouseY) {
        boolean pressed = super.mousePressed(minecraft, mouseX, mouseY);

        if (pressed) {
            value.toggle();
            updateText();
        }

        return pressed;
    }

    @Override
    public void drawButton(Minecraft minecraft, int mouseX, int mouseY) {
        updateText();
        super.drawButton(minecraft, mouseX, mouseY);
    }

    private void updateText() {
        displayString = label
            + ": "
            + (value.get()
                ? "\u00a7aON"
                : "\u00a7cOFF");
    }
}
