package dev.tomat.constellar.content.gui.resourcepack.buttons;

import dev.tomat.constellar.content.gui.GuiIconButton;
import dev.tomat.constellar.content.gui.resourcepack.ResourcePackUtils;
import net.minecraft.util.ResourceLocation;

public class OpenFolderButton extends GuiIconButton {
    public OpenFolderButton(int buttonId, int xPos, int yPos)
    {
        super(buttonId, xPos, yPos);
    }

    public ResourceLocation getIconTexture() {
        return ResourcePackUtils.ResourcePackTextures;
    }

    public int getSpreadsheetX() {
        int spritesheetColumn = 6;
        return ResourcePackUtils.RefreshIconSize * spritesheetColumn;
    }

    public int getSpreadsheetY() {
        return 0;
    }
}
