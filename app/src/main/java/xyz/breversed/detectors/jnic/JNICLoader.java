package xyz.breversed.detectors.jnic;

import org.objectweb.asm.tree.ClassNode;
import xyz.breversed.api.asm.detection.AbstractDetector;

public class JNICLoader extends AbstractDetector {

    @Override
    protected boolean detect() {
        for (ClassNode classNode : getClasses()) {
            /* skip if class name doesn't match */
            if (JNICLoader.isJNICLoader(classNode.name)) {
                context.add("Found JNICLoader");
                return true;
            }
        }
        return false;
    }

    // also used in JNICDecryptor
    public static boolean isJNICLoader(String className) {
        return className.startsWith("dev/jnic/") && className.endsWith("JNICLoader");
    }
}
