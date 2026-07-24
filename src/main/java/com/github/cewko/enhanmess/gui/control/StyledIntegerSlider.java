package com.github.cewko.enhanmess.gui.control;

import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;

import com.github.cewko.enhanmess.gui.GuiLayout;

public final class StyledIntegerSlider extends GuiButton {
    private final String label;
    private final int min;
    private final int max;
    private final IntSupplier getter;
    private final IntConsumer setter;

    private boolean dragging;

    public StyledIntegerSlider(
        int id,
        int x,
        int y,
        int width,
        int height,
        String label,
        int min,
        int max,
        IntSupplier getter,
        IntConsumer setter
    ) {
        super(id, x, y, width, height, "");
        this.label = label;
        this.min = min;
        this.max = max;
        this.getter = getter;
        this.setter = setter;
        updateText();
    }

    @Override
    public boolean mousePressed(Minecraft minecraft, int mouseX, int mouseY) {
        boolean pressed = super.mousePressed(minecraft, mouseX, mouseY);

        if (pressed) {
            dragging = true;
            updateValueFromMouse(mouseX);
        }

        return pressed;
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY) {
        dragging = false;
    }

    @Override
    protected void mouseDragged(Minecraft minecraft, int mouseX, int mouseY) {
        if (!visible) {
            return;
        }

        if (dragging) {
            updateValueFromMouse(mouseX);
        }

        minecraft.getTextureManager().bindTexture(buttonTextures);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        int handleX = xPosition + Math.round(
            getPercent() * (width - GuiLayout.SLIDER_HANDLE_WIDTH)
        );

        drawTexturedModalRect(
            handleX,
            yPosition,
            0,
            66,
            GuiLayout.SLIDER_HANDLE_WIDTH / 2,
            height
        );

        drawTexturedModalRect(
            handleX + GuiLayout.SLIDER_HANDLE_WIDTH / 2,
            yPosition,
            196,
            66,
            GuiLayout.SLIDER_HANDLE_WIDTH / 2,
            height
        );
    }

    @Override
    public void drawButton(Minecraft minecraft, int mouseX, int mouseY) {
        updateText();
        super.drawButton(minecraft, mouseX, mouseY);
    }

    @Override
    protected int getHoverState(boolean mouseOver) {
        return 0;
    }

    private void updateValueFromMouse(int mouseX) {
        float percent = (
            mouseX - xPosition - GuiLayout.SLIDER_HANDLE_WIDTH / 2.0F
        ) / (float) (width - GuiLayout.SLIDER_HANDLE_WIDTH);

        percent = Math.max(0.0F, Math.min(1.0F, percent));

        int newValue = Math.round(min + percent * (max - min));

        if (newValue != getter.getAsInt()) {
            setter.accept(newValue);
            updateText();
        }
    }

    private float getPercent() {
        if (max == min) {
            return 0.0F;
        }

        return (float) (getter.getAsInt() - min) / (float) (max - min);
    }

    private void updateText() {
        displayString = label + ": " + getter.getAsInt() + "s";
    }
}
