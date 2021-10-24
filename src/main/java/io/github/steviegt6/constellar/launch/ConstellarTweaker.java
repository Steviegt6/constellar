package io.github.steviegt6.constellar.launch;

import net.minecraft.launchwrapper.IArgumentTweaker;
import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.LaunchClassLoader;
import optifine.OptiFineClassTransformer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

// TODO: Configurable OptiFine
public class ConstellarTweaker implements ITweaker, IArgumentTweaker {
    private static final Logger LOGGER = LogManager.getLogger();

    private final ArrayList<String> Arguments = new ArrayList<>();

    @Override
    public void acceptOptions(List<String> args, File gameDir, final File assetsDir, String profile) {
        Arguments.addAll(args);

        addArgument("gameDir", gameDir);
        addArgument("assetsDir", assetsDir);
        addArgument("version", profile);
    }

    @Override
    public String getLaunchTarget() {
        return "net.minecraft.client.main.Main";
    }

    @Override
    public void injectIntoClassLoader(LaunchClassLoader classLoader) {
        // new OptiFineTweaker().injectIntoClassLoader(classLoader);
        // classLoader.registerTransformer("io.github.steviegt6.constellar.launch.ConstellarTransformer");
        // classLoader.registerTransformer("optifine.OptiFineClassTransformer");
        classLoader.registerTransformer(new OptiFineClassTransformer());

        LOGGER.info("Initializing Bootstraps...");
        MixinBootstrap.init();
        LOGGER.info("Adding mixin configuration...");
        MixinEnvironment environment = MixinEnvironment.getDefaultEnvironment();
        Mixins.addConfiguration("mixins.constellar.json");

        if (environment.getObfuscationContext() == null) {
            environment.setObfuscationContext("notch"); // Switch's to notch mappings
        }

        environment.setSide(MixinEnvironment.Side.CLIENT);
    }

    @Override
    public String[] getLaunchArguments() {
        return Arguments.toArray(new String[]{});
    }

    @Override
    public void modifyArguments(List<String> arguments) {
        // MC is unforgiving of duplicate arguments.
        clearDuplicate(arguments, "--version");
        clearDuplicate(arguments, "--assetsDir");
    }

    private void clearDuplicate(List<String> arguments, String dup) {
        int dupIndex = arguments.lastIndexOf(dup);

        if (arguments.stream().filter(x -> x.equals(dup)).count() == 2)
        {
            arguments.remove(dupIndex + 1);
            arguments.remove(dupIndex);
        }
    }

    private void addArgument(String label, Object value) {
        Arguments.add("--" + label);
        Arguments.add(value instanceof String ? (String) value : value instanceof File ? ((File) value).getAbsolutePath() : ".");
    }
}
