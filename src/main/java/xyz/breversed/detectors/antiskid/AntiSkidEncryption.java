package xyz.breversed.detectors.antiskid;

import org.objectweb.asm.tree.ClassNode;
import xyz.breversed.api.asm.detection.AbstractDetector;

public class AntiSkidEncryption extends AbstractDetector {

    @Override
    protected boolean detect() {
        for (ClassNode classNode : getClasses())
            if (classNode.superName.equals("java/lang/ClassLoader") && classNode.name.startsWith("club/antiskid"))
                return true;

        return false;
    }
}
