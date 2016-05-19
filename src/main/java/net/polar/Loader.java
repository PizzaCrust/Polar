package net.polar;

import net.minecraft.launchwrapper.LaunchClassLoader;
import net.polar.event.PluginStartEvent;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FilenameFilter;
import java.util.jar.JarFile;

public class Loader {
    private static final File PLUGINS_DIR = new File(System.getProperty("user.dir"), "plugins");

    public static void init() {
        Logger logger = LogManager.getLogger("PolarLoader");
        if (!PLUGINS_DIR.exists()) {
            PLUGINS_DIR.mkdir();
        }
        logger.info("Loader initialized. Searching for plugins in /plugins...");
        File[] pluginFiles = PLUGINS_DIR.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".jar");
            }
        });
        logger.info("Detected {} plugin files inside of /plugins.", pluginFiles.length);
        LaunchClassLoader launchClassLoader = (LaunchClassLoader) Thread.currentThread().getContextClassLoader();
        for (File pluginFile : pluginFiles) {
            try {
                logger.info("Loading plugin file {}...", FilenameUtils.removeExtension(pluginFile.getName()));
                JarFile jarFile = new JarFile(pluginFile);
                String pluginClass = jarFile.getManifest().getMainAttributes().getValue("Plugin-Class");
                launchClassLoader.addURL(pluginFile.toURI().toURL());
                Class<?> pluginClassReflection = Class.forName(pluginClass);
                Loader utils = new Loader();
                if (!utils.hasEmptyConstructor(pluginClassReflection)) {
                    logger.error("Plugin file {} has no empty constructor in plugin main class!", FilenameUtils.removeExtension(pluginFile.getName()));
                    continue;
                }
                Object classInstance = pluginClassReflection.newInstance();
                PolarServerHandler.EVENT_BUS.register(classInstance);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        logger.info("Calling starting event on all plugins loaded...");
        PolarServerHandler.EVENT_BUS.post(new PluginStartEvent());
        logger.info("Loader has finished.");
    }

    private boolean hasEmptyConstructor(Class<?> theClass) {
        try {
            theClass.getConstructor(new Class[0]);
        } catch (NoSuchMethodException e) {
            return false;
        }
        return true;
    }
}
