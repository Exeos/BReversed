package xyz.breversed.detectors.crasher;

import org.objectweb.asm.tree.ClassNode;
import xyz.breversed.api.asm.detection.AbstractDetector;

import java.util.concurrent.atomic.AtomicBoolean;

public class ImageCrasher extends AbstractDetector {

    @Override
    protected boolean detect() {
        final AtomicBoolean detected = new AtomicBoolean(false);
        for (ClassNode classNode : getClasses()) {
            if (classNode.name.toLowerCase().contains("<html><img src="))
                detected.set(true);
        }
        for (String s : getFiles().keySet()) {
            if (s.toLowerCase().contains("<html><img src="))
                detected.set(true);
        }
        return detected.get();
    }
}