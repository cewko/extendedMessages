package com.github.cewko.extendedmessages.gui.control;

import java.util.function.BooleanSupplier;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

public final class StyledToggleButton extends GuiButton {
    private final String label;
    private final BooleanSupplier getter;
    private final Runnable toggler;

    public StyledToggleButton(
        int id,
        int x,
        int y,
        int width,
        int height,
        String label,
        BooleanSupplier getter,
        Runnable toggler
    ) {
        super(id, x, y, width, height, "");
        this.label = label;
        this.getter = getter;
        this.toggler = toggler;
        updateText();
    }

    @Override
    public boolean mousePressed(Minecraft minecraft, int mouseX, int mouseY) {
        boolean pressed = super.mousePressed(minecraft, mouseX, mouseY);

        if (pressed) {
            toggler.run();
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
            + (getter.getAsBoolean()
                ? "\u00a7aON"
                : "\u00a7cOFF");
    }
}
