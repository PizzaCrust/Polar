package net.polar.launch;

import net.minecraft.launchwrapper.IClassTransformer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public class PolarTransformer implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        Logger logger = LogManager.getLogger("PolarTransformer");
        ClassReader classReader = new ClassReader(basicClass);
        ClassNode classNode = new ClassNode();
        classReader.accept(classNode, 0);
        if (name.equals("net.minecraft.server.MinecraftServer")) {
            logger.info("server class found");
            for (MethodNode methodNode : classNode.methods) {
                if (methodNode.name.equals("newCmdManagerImpl")) {
                    logger.info("found method " + methodNode.name + " -> desc: " + methodNode.desc + "!");
                    /*
                        TODO
                     */
                }
            }
        }
        ClassWriter classWriter = new ClassWriter(0);
        classNode.accept(classWriter);
        return classWriter.toByteArray();
    }
}
