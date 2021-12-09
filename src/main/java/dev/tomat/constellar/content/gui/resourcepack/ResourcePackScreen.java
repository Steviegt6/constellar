package dev.tomat.constellar.content.gui.resourcepack;

import com.google.common.collect.Lists;
import dev.tomat.common.utils.ColorUtils;
import dev.tomat.constellar.content.gui.GuiUtils;
import dev.tomat.constellar.content.gui.resourcepack.entries.ResourcePackEntryConstellar;
import dev.tomat.constellar.content.gui.resourcepack.entries.ResourcePackEntryDefault;
import dev.tomat.constellar.content.gui.resourcepack.entries.ResourcePackEntryFound;
import dev.tomat.constellar.content.gui.resourcepack.panels.AvailableResourcePackPanel;
import dev.tomat.constellar.content.gui.resourcepack.panels.SelectedResourcePackPanel;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.ResourcePackRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.List;

/**
 * Resource pack screen overhaul.
 *
 */
public class ResourcePackScreen extends GuiScreen {

    protected static final Logger logger = LogManager.getLogger();
    protected final GuiScreen parentScreen;

    protected List<ResourcePackEntry> availableResourcePacks;
    protected List<ResourcePackEntry> selectedResourcePacks;

    protected AvailableResourcePackPanel availableResourcePackPanel;
    protected SelectedResourcePackPanel selectedResourcePackPanel;

    protected boolean changed = false;

    public ResourcePackScreen(GuiScreen parentScreenIn)
    {
        parentScreen = parentScreenIn;
    }

    public void initGui()
    {
        //this.buttonList.add(new GuiOptionButton(2, this.width / 2 - 154, this.height - 48, I18n.format("resourcePack.openFolder", new Object[0])));
        //this.buttonList.add(new GuiOptionButton(1, this.width / 2 + 4, this.height - 48, I18n.format("gui.done", new Object[0])));

        if (!changed)
        {
            availableResourcePacks = Lists.newArrayList();
            selectedResourcePacks = Lists.newArrayList();

            ResourcePackRepository resourcePackRepository = mc.getResourcePackRepository();
            resourcePackRepository.updateRepositoryEntriesAll();

            List<ResourcePackRepository.Entry> resourcePacks = Lists.newArrayList(resourcePackRepository.getRepositoryEntriesAll());
            resourcePacks.removeAll(resourcePackRepository.getRepositoryEntries());

            for (ResourcePackRepository.Entry resourcePack : resourcePacks)
            {
                availableResourcePacks.add(new ResourcePackEntryFound(this, resourcePack));
            }

            for (ResourcePackRepository.Entry resourcePack : Lists.reverse(resourcePackRepository.getRepositoryEntries()))
            {
                selectedResourcePacks.add(new ResourcePackEntryFound(this, resourcePack));
            }

            selectedResourcePacks.add(new ResourcePackEntryDefault(this));
            selectedResourcePacks.add(new ResourcePackEntryConstellar(this));
        }

        this.availableResourcePackPanel = new AvailableResourcePackPanel(mc, ResourcePackUtils.ResourcePackEntryWidth, height, availableResourcePacks);
        this.availableResourcePackPanel.setSlotXBoundsFromLeft(width / 2 - (ResourcePackUtils.ResourcePackPanelPadding / 2) - ResourcePackUtils.ResourcePackEntryWidth);
        this.availableResourcePackPanel.registerScrollButtons(7, 8);

        this.selectedResourcePackPanel = new SelectedResourcePackPanel(mc, ResourcePackUtils.ResourcePackEntryWidth, height, selectedResourcePacks);
        this.selectedResourcePackPanel.setSlotXBoundsFromLeft(width / 2 + (ResourcePackUtils.ResourcePackPanelPadding / 2));
        this.selectedResourcePackPanel.registerScrollButtons(7, 8);
    }

    public void handleMouseInput() throws IOException
    {
        super.handleMouseInput();
        selectedResourcePackPanel.handleMouseInput();
        availableResourcePackPanel.handleMouseInput();
    }

    public boolean hasResourcePackEntry(ResourcePackEntry resourcePack)
    {
        return selectedResourcePacks.contains(resourcePack);
    }

    public List<ResourcePackEntry> getListContaining(ResourcePackEntry resourcePack)
    {
        return hasResourcePackEntry(resourcePack) ? selectedResourcePacks : availableResourcePacks;
    }

    public List<ResourcePackEntry> getAvailableResourcePackPanel()
    {
        return this.availableResourcePacks;
    }

    public List<ResourcePackEntry> getSelectedResourcePackPanel()
    {
        return this.selectedResourcePacks;
    }

    protected void actionPerformed(GuiButton button) {
        if (button.enabled)
        {
            /*
            if (button.id == 2)
            {
                File file1 = this.mc.getResourcePackRepository().getDirResourcepacks();
                String s = file1.getAbsolutePath();

                if (Util.getOSType() == Util.EnumOS.OSX)
                {
                    try
                    {
                        logger.info(s);
                        Runtime.getRuntime().exec(new String[] {"/usr/bin/open", s});
                        return;
                    }
                    catch (IOException ioexception1)
                    {
                        logger.error("Couldn't open file", ioexception1);
                    }
                }
                else if (Util.getOSType() == Util.EnumOS.WINDOWS)
                {
                    String s1 = String.format("cmd.exe /C start \"Open file\" \"%s\"", s);

                    try
                    {
                        Runtime.getRuntime().exec(s1);
                        return;
                    }
                    catch (IOException ioexception)
                    {
                        logger.error("Couldn't open file", ioexception);
                    }
                }

                boolean flag = false;

                try
                {
                    Class<?> oclass = Class.forName("java.awt.Desktop");
                    Object object = oclass.getMethod("getDesktop", new Class[0]).invoke(null);
                    oclass.getMethod("browse", new Class[] {URI.class}).invoke(object, file1.toURI());
                }
                catch (Throwable throwable)
                {
                    logger.error("Couldn't open link", throwable);
                    flag = true;
                }

                if (flag)
                {
                    logger.info("Opening via system class!");
                    Sys.openURL("file://" + s);
                }
            }
            else if (button.id == 1)
            {
                if (this.changed)
                {
                    List<ResourcePackRepository.Entry> list = Lists.newArrayList();

                    for (ResourcePackEntry resourcepacklistentry : this.selectedResourcePackPanel)
                    {
                        if (resourcepacklistentry instanceof ResourcePackEntryFound)
                        {
                            list.add(((ResourcePackEntryFound)resourcepacklistentry).getResourcePack());
                        }
                    }

                    Collections.reverse(list);
                    this.mc.getResourcePackRepository().setRepositories(list);
                    this.mc.gameSettings.resourcePacks.clear();
                    this.mc.gameSettings.incompatibleResourcePacks.clear();

                    for (ResourcePackRepository.Entry resourcepackrepository$entry : list)
                    {
                        this.mc.gameSettings.resourcePacks.add(resourcepackrepository$entry.getResourcePackName());

                        if (resourcepackrepository$entry.func_183027_f() != 1)
                        {
                            this.mc.gameSettings.incompatibleResourcePacks.add(resourcepackrepository$entry.getResourcePackName());
                        }
                    }

                    this.mc.gameSettings.saveOptions();
                    this.mc.refreshResources();
                }

                this.mc.displayGuiScreen(this.parentScreen);
            }*/
        }
    }

    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        availableResourcePackPanel.mouseClicked(mouseX, mouseY, mouseButton);
        selectedResourcePackPanel.mouseClicked(mouseX, mouseY, mouseButton);
    }

    protected void mouseReleased(int mouseX, int mouseY, int state)
    {
        super.mouseReleased(mouseX, mouseY, state);
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        drawBackground(0);
        availableResourcePackPanel.drawScreen(mouseX, mouseY, partialTicks);
        selectedResourcePackPanel.drawScreen(mouseX, mouseY, partialTicks);
        drawCenteredString(fontRendererObj, I18n.format("resourcePack.title"), width / 2, GuiUtils.DefaultTitleTopPadding, ColorUtils.White);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public void markChanged()
    {
        changed = true;
    }
}
