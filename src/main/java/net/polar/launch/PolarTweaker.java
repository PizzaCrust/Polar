package net.polar.launch;

import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.LaunchClassLoader;
import net.polar.obf.PolarDeobfuscator;

import java.io.File;
import java.util.List;

public class PolarTweaker implements ITweaker {
    @Override
    public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile) {}

    @Override
    public void injectIntoClassLoader(LaunchClassLoader classLoader) {
        if (getClass().getClassLoader().getResourceAsStream("net/minecraft/server/ServerMachineImpl.class") == null) {
            PolarDeobfuscator polarDeobfuscator = new PolarDeobfuscator();
            polarDeobfuscator.runSpecialSource();
            return;
        }
        classLoader.registerTransformer(PolarTransformer.class.getName());
        try {
            classLoader.findClass("net.minecraft.server.MinecraftServer");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getLaunchTarget() {
        return "net.minecraft.server.MinecraftServer";
    }

    @Override
    public String[] getLaunchArguments() {
        return new String[] { "nogui" };
    }
}
