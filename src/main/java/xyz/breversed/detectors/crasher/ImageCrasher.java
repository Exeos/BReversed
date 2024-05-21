package xyz.breversed.detectors.crasher;

import org.objectweb.asm.tree.ClassNode;
import xyz.breversed.api.asm.detection.AbstractDetector;

public class ImageCrasher extends AbstractDetector {

    @Override
    protected boolean detect() {
        for (ClassNode classNode : getClasses()) {
            if (classNode.name.toLowerCase().contains("<html><img src="))
                return true;
        }
        for (String s : getResources().keySet()) {
            if (s.toLowerCase().contains("<html><img src="))
                return true;
        }
        return false;
    }
}