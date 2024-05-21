package xyz.breversed.detectors.scuti;

import me.exeos.asmplus.pattern.PatternParts;
import me.exeos.asmplus.pattern.PatternScanner;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import xyz.breversed.api.asm.detection.AbstractDetector;

public class ScutiFlow extends AbstractDetector implements PatternParts {

    @Override
    protected boolean detect() {
        boolean detected = false;

        PatternScanner patternScanner = new PatternScanner();

        for (ClassNode classNode : getClasses()) {
            if (!classNode.name.startsWith("          ")) {
                for (MethodNode methodNode : classNode.methods) {
                    if (staticInitializerPattern(patternScanner, methodNode)) {
                        addContext("Scuti static initializer");
                        detected = true;
                    }
                    if (flowPattern(patternScanner, methodNode)) {
                        addContext("Main flow pattern");
                        detected = true;
                    }
                    if (secondFlowPattern(patternScanner, methodNode)) {
                        addContext("Second flow pattern");
                        detected = true;
                    }
                    if (thirdFlowPattern(patternScanner, methodNode)) {
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

    private boolean staticInitializerPattern(PatternScanner patternScanner, MethodNode methodNode) {
        patternScanner.setPattern(new int[] {
                P_NUMBER,
                P_NUMBER,
                IXOR,
                PUTSTATIC,
                P_NUMBER,
                P_NUMBER,
                IXOR,
                PUTSTATIC
        });
        return !patternScanner.scanMethod(methodNode).isEmpty();
    }

    private boolean flowPattern(PatternScanner patternScanner, MethodNode methodNode) {
        patternScanner.setPattern(new int[] {
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
        return !patternScanner.scanMethod(methodNode).isEmpty();
    }

    private boolean secondFlowPattern(PatternScanner patternScanner, MethodNode methodNode) {
        patternScanner.setPattern(new int[] {
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
        return !patternScanner.scanMethod(methodNode).isEmpty();
    }

    private boolean thirdFlowPattern(PatternScanner patternScanner, MethodNode methodNode) {
        patternScanner.setPattern(new int[] {
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
        return !patternScanner.scanMethod(methodNode).isEmpty();
    }
}