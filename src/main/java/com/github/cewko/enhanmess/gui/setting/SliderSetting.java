package com.github.cewko.enhanmess.gui.setting;

import java.util.List;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

import com.github.cewko.enhanmess.gui.GuiLayout;
import com.github.cewko.enhanmess.gui.control.StyledIntegerSlider;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;

public final class SliderSetting implements GuiSetting {
    private final String label;
    private final int min;
    private final int max;
    private final IntSupplier getter;
    private final IntConsumer setter;

    public SliderSetting(
        String label,
        int min,
        int max,
        IntSupplier getter,
        IntConsumer setter
    ) {
        this.label = label;
        this.min = min;
        this.max = max;
        this.getter = getter;
        this.setter = setter;
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
            getter,
            setter
        ));
    }
}
