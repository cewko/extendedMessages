package com.github.cewko.enhanmess.gui.setting;

import java.io.IOException;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;

public interface GuiSetting {
    int getHeight();

    void init(
        List<GuiButton> buttons,
        FontRenderer fontRenderer,
        int id,
        int x,
        int y,
        int width
    );

    default void draw(Minecraft minecraft, int mouseX, int mouseY) {
    }

    default void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
    }

    default void keyTyped(char typedChar, int keyCode) throws IOException {
    }

    default void update() {
    }

    default boolean validate() {
        return true;
    }
}
