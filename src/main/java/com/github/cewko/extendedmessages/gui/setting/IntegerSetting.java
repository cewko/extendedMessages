package com.github.cewko.extendedmessages.gui.setting;

import java.io.IOException;
import java.util.List;

import com.github.cewko.extendedmessages.gui.GuiLayout;
import com.github.cewko.extendedmessages.gui.control.StyledTextField;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;

public final class IntegerSetting implements GuiSetting {
    public interface Value {
        int get();

        void set(int value);
    }

    private final String label;
    private final int min;
    private final int max;
    private final Value value;

    private FontRenderer fontRenderer;
    private StyledTextField textField;
    private GuiValidationError error;
    private int x;
    private int y;

    public IntegerSetting(String label, int min, int max, Value value) {
        this.label = label;
        this.min = min;
        this.max = max;
        this.value = value;
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

        textField.setMaxStringLength(String.valueOf(max).length());
        textField.setText(String.valueOf(value.get()));
        textField.setPlaceholder(min + "-" + max);
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
        String text = textField.getText().trim();

        if (text.isEmpty()) {
            error = GuiValidationError.REQUIRED;
            return false;
        }

        try {
            int parsed = Integer.parseInt(text);

            if (parsed < min || parsed > max) {
                error = GuiValidationError.OUT_OF_RANGE;
                return false;
            }

            value.set(parsed);
            error = null;
            return true;
        } catch (NumberFormatException exception) {
            error = GuiValidationError.NUMBERS_ONLY;
            return false;
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
