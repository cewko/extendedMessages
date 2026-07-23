package com.github.cewko.extendedmessages.gui.control;

import com.github.cewko.extendedmessages.gui.GuiColors;
import com.github.cewko.extendedmessages.gui.GuiLayout;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;

public final class StyledTextField extends GuiTextField {
    private final FontRenderer fontRenderer;
    private final int x;
    private final int y;
    private final int height;

    private String placeholder = "";

    public StyledTextField(
        int id,
        FontRenderer fontRenderer,
        int x,
        int y,
        int width,
        int height
    ) {
        super(id, fontRenderer, x, y, width, height);

        this.fontRenderer = fontRenderer;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        setEnableBackgroundDrawing(true);
        setTextColor(GuiColors.TEXT_PRIMARY);
        setDisabledTextColour(GuiColors.TEXT_MUTED);
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    @Override
    public void drawTextBox() {
        if (!getVisible()) {
            return;
        }

        super.drawTextBox();

        if (getText().isEmpty() && !placeholder.isEmpty() && !isFocused()) {
            fontRenderer.drawStringWithShadow(
                placeholder,
                x + GuiLayout.TEXT_FIELD_PADDING,
                y + (height - GuiLayout.FONT_HEIGHT) / 2,
                GuiColors.TEXT_MUTED
            );
        }
    }
}
