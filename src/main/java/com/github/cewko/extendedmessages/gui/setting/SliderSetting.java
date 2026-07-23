package com.github.cewko.extendedmessages.gui.setting;

import java.util.List;

import com.github.cewko.extendedmessages.gui.GuiLayout;
import com.github.cewko.extendedmessages.gui.control.StyledIntegerSlider;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;

public final class SliderSetting implements GuiSetting {
    public interface Value {
        int get();

        void set(int value);
    }

    private final String label;
    private final int min;
    private final int max;
    private final Value value;

    public SliderSetting(String label, int min, int max, Value value) {
        this.label = label;
        this.min = min;
        this.max = max;
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
        buttons.add(new StyledIntegerSlider(
            id,
            x,
            y,
            width,
            GuiLayout.CONTROL_HEIGHT,
            label,
            min,
            max,
            new StyledIntegerSlider.Value() {
                @Override
                public int get() {
                    return value.get();
                }

                @Override
                public void set(int newValue) {
                    value.set(newValue);
                }
            }
        ));
    }
}
