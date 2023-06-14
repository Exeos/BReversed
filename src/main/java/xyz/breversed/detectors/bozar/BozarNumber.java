package xyz.breversed.detectors.bozar;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;
import xyz.breversed.api.asm.detection.AbstractDetector;

public class BozarNumber extends AbstractDetector {

    @Override
    protected boolean detect() {
        for (ClassNode classNode : getClasses()) {
            for (MethodNode methodNode : classNode.methods) {
                for (AbstractInsnNode insnNode : methodNode.instructions) {
                    if (insnNode instanceof LdcInsnNode ldcInsn && ldcInsn.cst instanceof String value) {
                        if (value.replace("\0", "").isEmpty()) {
                            addContext("Detected num to string length pattern");
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
