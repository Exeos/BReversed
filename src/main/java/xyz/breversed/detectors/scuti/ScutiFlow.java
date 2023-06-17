package xyz.breversed.detectors.scuti;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import xyz.breversed.api.asm.detection.AbstractDetector;
import xyz.breversed.api.asm.pattern.PatternParts;
import xyz.breversed.api.asm.pattern.PatternScanner;

public class ScutiFlow extends AbstractDetector implements PatternParts {

    @Override
    protected boolean detect() {
        boolean detected = false;

        PatternScanner staticInitializerPattern = getStaticInitializerPattern();
        PatternScanner flowPattern = getFlowPattern();
        PatternScanner secondFlowPattern = getSecondFlowPattern();
        PatternScanner thirdFlowPattern = getThirdFlowPattern();

        for (ClassNode classNode : getClasses()) {
            if (!classNode.name.startsWith("          ")) {
                for (MethodNode methodNode : classNode.methods) {
                    if (!staticInitializerPattern.scanMethod(methodNode).isEmpty()) {
                        addContext("Scuti static initializer");
                        detected = true;
                    }
                    if (!flowPattern.scanMethod(methodNode).isEmpty()) {
                        addContext("Main flow pattern");
                        detected = true;
                    }
                    if (!secondFlowPattern.scanMethod(methodNode).isEmpty()) {
                        addContext("Second flow pattern");
                        detected = true;
                    }
                    if (!thirdFlowPattern.scanMethod(methodNode).isEmpty()) {
                        addContext("Third flow pattern");
                        detected = true;
                    }
                }
                continue;
            }
            addContext("Space class name");
            detected = true;
        }

        return detected;
    }

    private PatternScanner getStaticInitializerPattern() {
        return new PatternScanner(new int[] {
                P_NUMBER,
                P_NUMBER,
                IXOR,
                PUTSTATIC,
                P_NUMBER,
                P_NUMBER,
                IXOR,
                PUTSTATIC
        });
    }

    private PatternScanner getFlowPattern() {
        return new PatternScanner(new int[] {
                GETSTATIC,
                GETSTATIC,
                JUMP_INSN,
                ACONST_NULL,
                P_ANY,
                ATHROW,
                P_ANY,
                ATHROW,
                P_ANY
        });
    }

    private PatternScanner getSecondFlowPattern() {
        return new PatternScanner(new int[] {
                GOTO,
                P_ANY,
                GOTO,
                P_ANY,
                GETSTATIC,
                GETSTATIC,
                JUMP_INSN,
                ACONST_NULL,
                ATHROW
        });
    }

    private PatternScanner getThirdFlowPattern() {
        return new PatternScanner(new int[] {
                ATHROW,
                P_ANY,
                JUMP_INSN,
                P_ANY,
                GETSTATIC,
                GETSTATIC,
                JUMP_INSN,
                ACONST_NULL,
                ATHROW
        });
    }
}