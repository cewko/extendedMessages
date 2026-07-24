package com.github.cewko.enhanmess.gui.setting;

import java.util.List;
import java.util.function.BooleanSupplier;

import com.github.cewko.enhanmess.gui.GuiLayout;
import com.github.cewko.enhanmess.gui.control.StyledToggleButton;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;

public final class ToggleSetting implements GuiSetting {
    private final String label;
    private final BooleanSupplier getter;
    private final Runnable toggler;

    public ToggleSetting(
        String label,
        BooleanSupplier getter,
        Runnable toggler
    ) {
        this.label = label;
        this.getter = getter;
        this.toggler = toggler;
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
            getter,
            toggler
        ));
    }
}
