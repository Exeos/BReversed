package xyz.breversed.detectors.encloader;

import org.objectweb.asm.tree.ClassNode;
import xyz.breversed.api.asm.detection.AbstractDetector;

public class ClassLoaderDetector extends AbstractDetector {

    @Override
    protected boolean detect() {
        for (ClassNode classNode : getClasses()) {
            if (classNode.superName != null && classNode.superName.endsWith("ClassLoader")) {
                context.add("Found class with ClassLoader super: " + classNode.name);
                return true;
            }
        }
        return false;
    }
}
