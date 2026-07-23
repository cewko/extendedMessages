package com.github.cewko.extendedmessages.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.github.cewko.extendedmessages.ExtendedMessages;
import com.github.cewko.extendedmessages.Reference;
import com.github.cewko.extendedmessages.gui.setting.GuiSetting;
import com.github.cewko.extendedmessages.gui.setting.IntegerSetting;
import com.github.cewko.extendedmessages.gui.setting.SliderSetting;
import com.github.cewko.extendedmessages.gui.setting.TextSetting;
import com.github.cewko.extendedmessages.gui.setting.ToggleSetting;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public final class ExtendedMessagesGui extends GuiScreen {
    private static final int DONE_BUTTON_ID = 0;

    private final List<GuiSetting> settings = new ArrayList<GuiSetting>();
    private final List<GuiSetting> regularSettings = new ArrayList<GuiSetting>();
    private final List<GuiSetting> commandSettings = new ArrayList<GuiSetting>();

    private GuiSetting mainSetting;

    private int nextControlId;

    @Override
    public void initGui() {
        buttonList.clear();
        settings.clear();
        regularSettings.clear();
        commandSettings.clear();
        nextControlId = 1;

        addSettings();

        int panelX = width / 2 - GuiLayout.PANEL_WIDTH / 2;
        int topY = GuiLayout.FIRST_ROW_Y;
        int leftX = panelX;
        int rightX = panelX + GuiLayout.COLUMN_WIDTH + GuiLayout.COLUMN_GAP;
        int leftY = topY + GuiLayout.CONTROL_HEIGHT + GuiLayout.SECTION_GAP;
        int rightY = topY + GuiLayout.CONTROL_HEIGHT + GuiLayout.SECTION_GAP;

        initSetting(mainSetting, panelX, topY, GuiLayout.PANEL_WIDTH);

        for (GuiSetting setting : regularSettings) {
            initSetting(setting, leftX, leftY, GuiLayout.COLUMN_WIDTH);
            leftY += setting.getHeight() + GuiLayout.ROW_SPACING;
        }

        for (GuiSetting setting : commandSettings) {
            initSetting(setting, rightX, rightY, GuiLayout.COLUMN_WIDTH);
            rightY += setting.getHeight() + GuiLayout.ROW_SPACING;
        }

        buttonList.add(new GuiButton(
            DONE_BUTTON_ID,
            width / 2 - GuiLayout.COLUMN_WIDTH / 2,
            Math.max(leftY, rightY) + GuiLayout.SECTION_GAP,
            GuiLayout.COLUMN_WIDTH,
            GuiLayout.CONTROL_HEIGHT,
            "Done"
        ));
    }

    private void initSetting(GuiSetting setting, int x, int y, int width) {
        setting.init(buttonList, fontRendererObj, nextControlId++, x, y, width);
    }

    private void addSettings() {
        addMainSettings();
        addRegularSettings();
        addCommandSettings();
    }

    private void addMainSettings() {
        mainSetting = new ToggleSetting(
            "Remove 100-character limit", 
            new ToggleSetting.Value() {
                @Override
                public boolean get() {
                    return ExtendedMessages.isEnabled();
                }

                @Override
                public void toggle() {
                    ExtendedMessages.toggleEnabled();
                }
            }
        );

        settings.add(mainSetting);
    }

    private void addRegularSettings() {
        addRegularSetting(
            new ToggleSetting("Split long messages", 
            new ToggleSetting.Value() {
                @Override
                public boolean get() {
                    return ExtendedMessages.isSplitEnabled();
                }

                @Override
                public void toggle() {
                    ExtendedMessages.toggleSplitEnabled();
                }
            }
        ));

        addRegularSetting(
            new SliderSetting(
                "Message cooldown",
                Reference.MIN_DELAY_SECONDS,
                Reference.MAX_DELAY_SECONDS,
                new SliderSetting.Value() {
                    @Override
                    public int get() {
                        return ExtendedMessages.getMessageDelaySeconds();
                    }

                    @Override
                    public void set(int value) {
                        ExtendedMessages.setMessageDelaySeconds(value);
                    }
                }
            )
        );

        addRegularSetting(
            new ToggleSetting("Message prefix", 
            new ToggleSetting.Value() {
                @Override
                public boolean get() {
                    return ExtendedMessages.isMessagePrefixEnabled();
                }

                @Override
                public void toggle() {
                    ExtendedMessages.toggleMessagePrefixEnabled();
                }
            }
        ));

        addRegularSetting(
            new TextSetting(
                "Message prefix",
                Reference.GUI_PREFIX_INPUT_LIMIT,
                new TextSetting.Value() {
                    @Override
                    public String get() {
                        return ExtendedMessages.getMessagePrefix();
                    }

                    @Override
                    public void set(String value) {
                        ExtendedMessages.setMessagePrefix(value);
                    }
                }
            )
        );

        addRegularSetting(
            new IntegerSetting(
                "History length",
                Reference.MIN_MESSAGE_HISTORY_LENGTH,
                Reference.MAX_MESSAGE_HISTORY_LENGTH,
                new IntegerSetting.Value() {
                    @Override
                    public int get() {
                        return ExtendedMessages.getMessageHistoryLength();
                    }

                    @Override
                    public void set(int value) {
                        ExtendedMessages.setMessageHistoryLength(value);
                    }
                }
            )
        );
    }

    private void addCommandSettings() {
        addCommandSetting(
            new ToggleSetting(
                "Configured command", 
                new ToggleSetting.Value() {
                    @Override
                    public boolean get() {
                        return ExtendedMessages.isCommandPrefixEnabled();
                    }

                    @Override
                    public void toggle() {
                        ExtendedMessages.toggleCommandPrefixEnabled();
                    }
                }
            )
        );

        addCommandSetting(
            new SliderSetting(
                "Command cooldown",
                Reference.MIN_DELAY_SECONDS,
                Reference.MAX_DELAY_SECONDS,
                new SliderSetting.Value() {
                    @Override
                    public int get() {
                        return ExtendedMessages.getCommandDelaySeconds();
                    }

                    @Override
                    public void set(int value) {
                        ExtendedMessages.setCommandDelaySeconds(value);
                    }
                }
            )
        );

        addCommandSetting(new TextSetting(
            "Command prefix",
            Reference.GUI_PREFIX_INPUT_LIMIT,
            new TextSetting.Value() {
                @Override
                public String get() {
                    return ExtendedMessages.getCommandPrefix();
                }

                @Override
                public void set(String value) {
                    ExtendedMessages.setCommandPrefix(value);
                }
            }
        ));
    }

    private void addRegularSetting(GuiSetting setting) {
        regularSettings.add(setting);
        settings.add(setting);
    }

    private void addCommandSetting(GuiSetting setting) {
        commandSettings.add(setting);
        settings.add(setting);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == DONE_BUTTON_ID) {
            mc.displayGuiScreen(null);
            return;
        }

        validateSettings();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        for (GuiSetting setting : settings) {
            setting.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        for (GuiSetting setting : settings) {
            setting.keyTyped(typedChar, keyCode);
        }

        super.keyTyped(typedChar, keyCode);
    }

    private void validateSettings() {
        for (GuiSetting setting : settings) {
            setting.validate();
        }
    }

    @Override
    public void updateScreen() {
        super.updateScreen();

        for (GuiSetting setting : settings) {
            setting.update();
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        drawCenteredString(
            fontRendererObj,
            Reference.NAME + " - v" + Reference.VERSION,
            width / 2,
            GuiLayout.TITLE_Y,
            GuiColors.TEXT_PRIMARY
        );

        for (GuiSetting setting : settings) {
            setting.draw(mc, mouseX, mouseY);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
