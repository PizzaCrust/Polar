package net.polar.obf;

import net.md_5.specialsource.SpecialSource;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class PolarDeobfuscator {
    private final File currentJar = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
    private final File targetJar = new File(currentJar.getParentFile(), "deobf-dev.jar");
    private final File mappingFile = new File(currentJar.getParentFile(), "minecraft.srg");

    private final Logger LOGGER = LogManager.getLogger("PolarDeobf");

    public void runSpecialSource() {
        if (targetJar.exists()) {
            targetJar.delete();
        }
        if (mappingFile.exists()) {
            mappingFile.delete();
        }
        LOGGER.info("Downloading mappings...");
        try {
            URL mappingURL = new URL("http://torchpowered.gq/pizza-map-1.10.srg");
            FileUtils.copyURLToFile(mappingURL, mappingFile);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
        LOGGER.info("Applying SpecialSource...");
        try {
            SpecialSource.main(new String[] {
                    "--quiet",
                    "--srg-in",
                    mappingFile.getAbsolutePath(),
                    "--in-jar",
                    currentJar.getAbsolutePath(),
                    "--out-jar",
                    targetJar.getAbsolutePath()
            });
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
        try {
            Files.copy(targetJar.toPath(), currentJar.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
        LOGGER.info("Restart Minecraft to run Polar again with the deobfuscated mappings!");
        System.exit(0);
    }
}
