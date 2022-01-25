package dev.tomat.constellar.content.gui;

import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class GuiUtils {
    public static final ResourceLocation ButtonsTexture = new ResourceLocation("textures/gui/buttons.png");

    public static final int DefaultButtonHeight = 20;
    public static final int DefaultButtonWidth = 200;
    public static final int DefaultGuiOptionsButtonWidth = 150;
    public static final int DefaultButtonIconSize = 16;

    public static final int ButtonSpreadsheetY = 46;

    public static final int DefaultTitleTopPadding = 16;
    public static final int DefaultTextureSize = 256;

    // GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
    public static final int SourceFactor = 770;
    public static final int SourceFactorAlpha = 1;
    public static final int DestFactor = 771;
    public static final int DestFactorAlpha = 0;

    public static final int TextHeight = 10;

    public static void drawRectNormal(int x, int y, int width, int height, int color, PosMode mode) {
        switch (mode) {
            case CENTER:
                Gui.drawRect(x - (width / 2), y - (height / 2), x + (width / 2), y + (width / 2), color);
                break;

            case UNBIASED:
                Gui.drawRect(x, y, x + width, y + height, color);
                break;
        }
    }

    public static void drawRectNormal(int x, int y, int width, int height, int color) {
        Gui.drawRect(x, y, x + width, y + height + 10, color);
    }

    public static void blend() {
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GuiUtils.SourceFactor, GuiUtils.DestFactor, GuiUtils.SourceFactorAlpha, GuiUtils.DestFactorAlpha);
        GlStateManager.blendFunc(GuiUtils.SourceFactor, GuiUtils.DestFactor);
    }

    public static void playButtonPressSound(SoundHandler soundHandlerIn) {
        soundHandlerIn.playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1F));
    }

    public static void resetColor() {
        GlStateManager.color(1f, 1f, 1f, 1f);
    }

    public enum PosMode {
        CENTER,
        UNBIASED
    }
}