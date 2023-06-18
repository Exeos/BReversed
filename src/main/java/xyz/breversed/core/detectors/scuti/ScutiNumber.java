package xyz.breversed.core.detectors.scuti;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import xyz.breversed.core.api.asm.detection.AbstractDetector;
import xyz.breversed.core.api.asm.pattern.PatternParts;
import xyz.breversed.core.api.asm.pattern.PatternScanner;

public class ScutiNumber extends AbstractDetector implements PatternParts {

    @Override
    protected boolean detect() {
        boolean detected = false;

        PatternScanner numberPattern = new PatternScanner(new int[] {
                P_NUMBER,
                P_NUMBER,
                IXOR,
                P_NUMBER,
                IXOR,
                P_NUMBER,
                IXOR
        });

        for (ClassNode classNode : getClasses())
            for (MethodNode methodNode : classNode.methods)
                if (!numberPattern.scanMethod(methodNode).isEmpty()) {
                    addContext("Number pattern");
                    detected = true;
                }

        return detected;
    }
}