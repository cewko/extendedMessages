package com.github.cewko.extendedmessages.gui.setting;

import com.github.cewko.extendedmessages.gui.GuiColors;

public final class GuiErrorLabel {
    private GuiErrorLabel() {
    }

    public static String text(String label, GuiValidationError error) {
        return error == null ? label : label + " (" + error.getLabel() + ")";
    }

    public static int color(GuiValidationError error) {
        return error == null ? GuiColors.TEXT_PRIMARY : GuiColors.TEXT_ERROR;
    }
}