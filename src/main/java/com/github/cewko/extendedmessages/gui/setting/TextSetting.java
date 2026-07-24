package com.github.cewko.extendedmessages.gui.setting;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.github.cewko.extendedmessages.gui.GuiLayout;
import com.github.cewko.extendedmessages.gui.control.StyledTextField;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;

public final class TextSetting implements GuiSetting {
    private final String label;
    private final int maxLength;
    private final Supplier<String> getter;
    private final Consumer<String> setter;

    private FontRenderer fontRenderer;
    private StyledTextField textField;
    private GuiValidationError error;
    private int x;
    private int y;

    public TextSetting(
        String label,
        int maxLength,
        Supplier<String> getter,
        Consumer<String> setter
    ) {
        this.label = label;
        this.maxLength = maxLength;
        this.getter = getter;
        this.setter = setter;
    }

    @Override
    public int getHeight() {
        return GuiLayout.INPUT_ROW_HEIGHT;
    }

    @Override
    public void init(
        List<GuiButton> buttons,
        FontRenderer fontRenderer,
        int id,
        int x,
        int y,
        int width
    ) {
        this.fontRenderer = fontRenderer;
        this.x = x;
        this.y = y;

        textField = new StyledTextField(
            id,
            fontRenderer,
            x,
            y + GuiLayout.INPUT_FIELD_Y_OFFSET,
            width,
            GuiLayout.CONTROL_HEIGHT
        );

        textField.setMaxStringLength(maxLength);
        textField.setText(getter.get());
    }

    @Override
    public void draw(Minecraft minecraft, int mouseX, int mouseY) {
        fontRenderer.drawString(
            GuiErrorLabel.text(label, error),
            x + GuiLayout.SETTING_LABEL_PADDING,
            y + GuiLayout.SETTING_LABEL_Y_OFFSET,
            GuiErrorLabel.color(error)
        );

        textField.drawTextBox();
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        textField.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) throws IOException {
        if (textField.textboxKeyTyped(typedChar, keyCode)) {
            saveIfValid();
        }
    }

    @Override
    public void update() {
        textField.updateCursorCounter();
    }

    private boolean saveIfValid() {
        try {
            setter.accept(textField.getText());
            error = null;
            return true;
        } catch (IllegalArgumentException exception) {
            error = GuiValidationError.fromException(exception);
            return false;
        }
    }

    @Override
    public boolean validate() {
        return saveIfValid();
    }
}
