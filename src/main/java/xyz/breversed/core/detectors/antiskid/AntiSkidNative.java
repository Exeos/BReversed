package xyz.breversed.core.detectors.antiskid;

import me.exeos.asmplus.utils.ASMUtils;
import org.objectweb.asm.tree.ClassNode;
import xyz.breversed.core.api.asm.detection.AbstractDetector;

public class AntiSkidNative extends AbstractDetector {

    @Override
    protected boolean detect() {
        for (ClassNode classNode : getClasses())
            if (classNode.name.startsWith("native0") && ASMUtils.getMethod(classNode, "registerNativesForClass", "(ILjava/lang/Class;)V", "(ILjava/lang/Class<*>;)V") != null)
                return true;

        return false;
    }
}
