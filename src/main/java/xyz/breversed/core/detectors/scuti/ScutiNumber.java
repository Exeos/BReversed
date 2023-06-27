package xyz.breversed.core.detectors.scuti;

import me.exeos.asmplus.pattern.PatternParts;
import me.exeos.asmplus.pattern.PatternScanner;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import xyz.breversed.core.api.asm.detection.AbstractDetector;

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