package com.github.cewko.extendedmessages.gui.setting;

import java.util.List;

import com.github.cewko.extendedmessages.gui.GuiLayout;
import com.github.cewko.extendedmessages.gui.control.StyledToggleButton;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;

public final class ToggleSetting implements GuiSetting {
    public interface Value {
        boolean get();

        void toggle();
    }

    private final String label;
    private final Value value;

    public ToggleSetting(String label, Value value) {
        this.label = label;
        this.value = value;
    }

    @Override
    public int getHeight() {
        return GuiLayout.TOGGLE_ROW_HEIGHT;
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
        buttons.add(new StyledToggleButton(
            id,
            x,
            y,
            width,
            GuiLayout.CONTROL_HEIGHT,
            label,
            new StyledToggleButton.Value() {
                @Override
                public boolean get() {
                    return value.get();
                }

                @Override
                public void toggle() {
                    value.toggle();
                }
            }
        ));
    }
}
