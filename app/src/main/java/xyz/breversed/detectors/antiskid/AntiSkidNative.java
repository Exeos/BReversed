package xyz.breversed.detectors.antiskid;

import me.exeos.asmplus.utils.ASMUtils;
import org.objectweb.asm.tree.ClassNode;
import xyz.breversed.api.asm.detection.AbstractDetector;

import java.io.File;
import java.io.IOException;

public class AntiSkidNative extends AbstractDetector {

    @Override
    protected boolean detect() {
        for (ClassNode classNode : getClasses())
            if (classNode.name.startsWith("native0") && ASMUtils.getMethod(classNode, "registerNativesForClass", "(ILjava/lang/Class;)V", "(ILjava/lang/Class<*>;)V") != null) {
                context.add("Native Loader: " + classNode.name);
                return true;
            }

        return false;
    }
}
