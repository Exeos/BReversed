package xyz.breversed.detectors.universal;

import org.objectweb.asm.tree.ClassNode;
import xyz.breversed.api.asm.detection.AbstractDetector;
import xyz.breversed.api.utils.CharUtil;

public class Renamer extends AbstractDetector {

    @Override
    protected boolean detect() {
        int bigClassNames = 0;
        int unAlphabeticClasses = 0;

        for (ClassNode classNode : getClasses()) {
            if (classNode.name.length() >= 20)
                bigClassNames++;

            String[] split = classNode.name.split("/");

            if (CharUtil.containsUnAlphabetic(split[split.length - 1].replace("$", "").replace("_", "").replace("-", "")))
                unAlphabeticClasses++;
        }

        if (bigClassNames >= getClasses().size() / 3)
            addContext("Big class names");

        if (unAlphabeticClasses >= getClasses().size() / 3)
            addContext("Unalphabetical class names");

        return bigClassNames + unAlphabeticClasses >= getClasses().size() / 2;
    }
}