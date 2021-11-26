package dev.tomat.constellar.mixins.gui;

import com.google.common.base.Strings;
import dev.tomat.common.reflection.FailedInvocationObject;
import dev.tomat.common.reflection.Reflector;
import dev.tomat.constellar.Constellar;
import dev.tomat.constellar.gui.BackgroundPanorama;
import dev.tomat.constellar.launch.ConstellarTweaker;
import dev.tomat.common.utils.ColorUtils;
import dev.tomat.common.utils.TextUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.realms.RealmsBridge;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Mixin(GuiMainMenu.class)
public abstract class GuiMainMenuMixin extends GuiScreen implements GuiYesNoCallback {
    private static final ResourceLocation ConstellarTitle = new ResourceLocation("constellar", "textures/gui/title/constellar.png");

    @Shadow private float updateCounter;

    @Shadow private String splashText;

    @Shadow private String openGLWarning1;
    @Shadow private int field_92022_t;
    @Shadow private int field_92021_u;
    @Shadow private int field_92020_v;
    @Shadow private int field_92019_w;
    @Shadow private int field_92024_r;
    @Shadow private String openGLWarning2;

    @Shadow private DynamicTexture viewportTexture;
    @Shadow private ResourceLocation backgroundTexture;
    @Final
    @Shadow private Object threadLock;
    @Shadow private int field_92023_s;


    /**
     * @author Tomat
     */
    @Overwrite
    public void drawScreen(int unknown1, int unknown2, float unknown3) {
        String copyrightText = "Copyright Mojang AB. Do not distribute!";

        GlStateManager.disableAlpha();

        if (BackgroundPanorama.Instance != null)
            BackgroundPanorama.Instance.render(unknown3);

        GlStateManager.enableAlpha();

        drawGradientRect(0, 0, width, height, -2130706433, 16777215);
        drawGradientRect(0, 0, width, height, 0, Integer.MIN_VALUE);

        int weirdWidthStuff = width / 2 - 274 / 2;
        int probablyHeight = 30;

        mc.getTextureManager().bindTexture(ConstellarTitle);

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        if ((double)updateCounter >= 1.0E-4D) {
            drawTexturedModalRect(weirdWidthStuff - 13, probablyHeight, 0, 0, 155, 44);
            drawTexturedModalRect(weirdWidthStuff + 142, probablyHeight, 0, 44, 154, 44);
        }

        GlStateManager.pushMatrix();
        GlStateManager.translate(width / 2F, probablyHeight, 0.0F);
        GlStateManager.rotate(0F, 0.0F, 0.0F, 1.0F);

        TextUtils.drawCenteredStringWithBorder(mc, splashText, 0, 46, ColorUtils.colorToInt(223, 173, 255, 255));
        GlStateManager.popMatrix();

        TextUtils.drawStringWithBorder(mc, copyrightText, width - fontRendererObj.getStringWidth(copyrightText) - 2, height - 10, -1);

        // Reimplementation of Forge patch: https://github.com/MinecraftForge/MinecraftForge/blob/1.8.9/patches/minecraft/net/minecraft/client/gui/GuiMainMenu.java.patch#L30
        List<String> brandings = getBrandingText();
        for (int i = 0; i < brandings.size(); i++) {
            String branding = brandings.get(i);

            if (!Strings.isNullOrEmpty(branding))
                TextUtils.drawStringWithBorder(mc, branding, 2, height - ((i + 1) * 10), -1);
        }

        if (openGLWarning1 != null && openGLWarning1.length() > 0)
        {
            drawRect(field_92022_t - 2, field_92021_u - 2, field_92020_v + 2, field_92019_w - 1, 1428160512);
            drawString(fontRendererObj, openGLWarning1, field_92022_t, field_92021_u, -1);
            drawString(fontRendererObj, openGLWarning2, (width - field_92024_r) / 2, (buttonList.get(0)).yPosition - 12, -1);
        }

        super.drawScreen(unknown1, unknown2, unknown3);
    }

    @Inject(method = "updateScreen", at = @At("HEAD"))
    public void updateScreen(CallbackInfo ci) {
        if (BackgroundPanorama.Instance != null) {
            BackgroundPanorama.Instance.Height = height;
            BackgroundPanorama.Instance.Width = width;
            BackgroundPanorama.Instance.Timer++;
        }
    }

    public List<String> getBrandingText() {
        List<String> brandings = new ArrayList<>();

        brandings.add("Minecraft 1.8.9");
        brandings.add(Constellar.ClientNameReadable + " v" + Constellar.ClientVersion);

        if (!ConstellarTweaker.LoadContext.standalone(ConstellarTweaker.Context))
            brandings.addAll(getForgeBrandings());

        return brandings;
    }

    @SuppressWarnings("unchecked")
    private List<String> getForgeBrandings() {
        Reflector reflector = Constellar.REFLECTOR;
        Class<?> clazz = reflector.getClass("net.minecraftforge.fml.common.FMLCommonHandler");

        if (clazz == null)
            return new ArrayList<>();

        Method getInstance = reflector.getMethod(clazz, "instance");
        Method getBrandings = reflector.getMethod(clazz, "getBrandings", boolean.class);

        if (getInstance == null || getBrandings == null)
            return new ArrayList<>();

        Object fmlCommonHandlerInstance = reflector.invokeMethod(getInstance, null);

        if (fmlCommonHandlerInstance instanceof FailedInvocationObject)
            return new ArrayList<>();

        Object list = reflector.invokeMethod(getBrandings, fmlCommonHandlerInstance, false);

        if (list instanceof FailedInvocationObject)
            return new ArrayList<>();

        return (List<String>) list;
    }

    /**
     * @author Metacinnabar
     */
    @Overwrite
    private void addSingleplayerMultiplayerButtons(int y, int offset)
    {
        this.buttonList.add(new GuiButton(1, this.width / 2 - 100, y, I18n.format("menu.singleplayer")));
        this.buttonList.add(new GuiButton(2, this.width / 2 - 100, y + offset, I18n.format("menu.multiplayer")));
        // we don't want the realms button for 1.8.9
        //this.buttonList.add(this.realmsButton = new GuiButton(14, this.width / 2 - 100, p_73969_1_ + p_73969_2_ * 2, I18n.format("menu.online", new Object[0])));
    }

    /**
     * @author Metacinnabar
     */
    @Overwrite
    public void initGui()
    {
        this.viewportTexture = new DynamicTexture(256, 256);
        this.backgroundTexture = this.mc.getTextureManager().getDynamicTextureLocation("background", this.viewportTexture);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        if (calendar.get(Calendar.MONTH) + 1 == 12 && calendar.get(Calendar.DATE) == 24)
        {
            this.splashText = "Merry X-mas!";
        }
        else if (calendar.get(Calendar.MONTH) + 1 == 1 && calendar.get(Calendar.DATE) == 1)
        {
            this.splashText = "Happy new year!";
        }
        else if (calendar.get(Calendar.MONTH) + 1 == 10 && calendar.get(Calendar.DATE) == 31)
        {
            this.splashText = "OOoooOOOoooo! Spooky!";
        }

        int i = 24;
        int j = this.height / 4 + (i * 2);

        if (this.mc.isDemo())
        {
            this.addDemoButtons(j, i);
        }
        else
        {
            this.addSingleplayerMultiplayerButtons(j, i);
        }

        j += i;

        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, j + i, 98, 20, I18n.format("menu.options")));
        this.buttonList.add(new GuiButton(4, this.width / 2 + 2, j + i, 98, 20, I18n.format("menu.quit")));
        // this.buttonList.add(new GuiButtonLanguage(5, this.width / 2 - 124, j + 72 + 12));

        // huh
        synchronized (this.threadLock)
        {
            this.field_92023_s = this.fontRendererObj.getStringWidth(this.openGLWarning1);
            this.field_92024_r = this.fontRendererObj.getStringWidth(this.openGLWarning2);
            int k = Math.max(this.field_92023_s, this.field_92024_r);
            this.field_92022_t = (this.width - k) / 2;
            this.field_92021_u = (this.buttonList.get(0)).yPosition - 24;
            this.field_92020_v = this.field_92022_t + k;
            this.field_92019_w = this.field_92021_u + 24;
        }

        if (this.func_183501_a())
        {
            this.setGuiSize(this.width, this.height);
            this.initGui();
        }
    }

    @Shadow protected abstract boolean func_183501_a();

    @Shadow protected abstract void addDemoButtons(int j, int i);
}
