package dev.tomat.constellar.content.gui.resourcepack.panels;

import dev.tomat.constellar.content.gui.resourcepack.ResourcePackEntry;
import dev.tomat.constellar.content.gui.resourcepack.ResourcePackPanel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

import java.util.List;

public class AvailableResourcePackPanel extends ResourcePackPanel {
    public AvailableResourcePackPanel(Minecraft mcIn, int widthIn, int heightIn, List<ResourcePackEntry> resourcePacks)
    {
        super(mcIn, widthIn, heightIn, resourcePacks);
    }

    protected String getListHeader()
    {
        return I18n.format("resourcePack.available.title");
    }
}
