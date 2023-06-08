package xyz.breversed.detectors.universal;

import org.objectweb.asm.tree.ClassNode;
import xyz.breversed.api.asm.detection.AbstractDetector;
import xyz.breversed.api.utils.CharUtil;

import java.util.concurrent.atomic.AtomicInteger;

public class Renamer extends AbstractDetector {

    @Override
    protected boolean detect() {
        final AtomicInteger bigClassNames = new AtomicInteger();
        final AtomicInteger unAlphabeticClasses = new AtomicInteger();

        for (ClassNode classNode : getClasses()) {
            if (classNode.name.length() >= 20)
                bigClassNames.incrementAndGet();

            String[] split = classNode.name.split("/");

            if (CharUtil.containsUnAlphabetic(split[split.length - 1].replace("$", "").replace("_", "").replace("-", "")))
                unAlphabeticClasses.incrementAndGet();
        }

        if (bigClassNames.get() >= getClasses().size() / 3)
            addContext("Big class names");

        if (unAlphabeticClasses.get() >= getClasses().size() / 3)
            addContext("Unalphabetical class names");

        return bigClassNames.get() + unAlphabeticClasses.get() >= getClasses().size() / 2;
    }
}