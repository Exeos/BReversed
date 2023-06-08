package xyz.breversed.detectors.antiskid;

import org.objectweb.asm.tree.ClassNode;
import xyz.breversed.api.asm.detection.AbstractDetector;
import xyz.breversed.api.asm.utils.ASMUtil;

public class AntiSkidNative extends AbstractDetector {

    @Override
    protected boolean detect() {
        for (ClassNode classNode : getClasses())
            if (classNode.name.startsWith("native0") && ASMUtil.getMethod(classNode, "registerNativesForClass", "(ILjava/lang/Class;)V", "(ILjava/lang/Class<*>;)V") != null)
                return true;

        return false;
    }
}
