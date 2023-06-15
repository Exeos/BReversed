package xyz.breversed.detectors.bozar;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;
import xyz.breversed.api.asm.detection.AbstractDetector;
import xyz.breversed.api.asm.pattern.PatternParts;
import xyz.breversed.api.asm.pattern.PatternScanner;

import java.util.concurrent.atomic.AtomicBoolean;

public class BozarFlow extends AbstractDetector implements PatternParts {

    @Override
    protected boolean detect() {
        AtomicBoolean detected = new AtomicBoolean(false);
        PatternScanner patternScanner = new PatternScanner();

        for (ClassNode classNode : getClasses()) {
            for (FieldNode field : classNode.fields) {
                if (field.desc.equals("J")) {
                    addContext("Found flow field");
                    break;
                }
            }

            for (MethodNode methodNode : classNode.methods) {
                if (lightFlow0(patternScanner, methodNode) || lightFlow1(patternScanner, methodNode)) {
                    addContext("Detected Light flow pattern");
                    detected.set(true);
                }
                if (constantFlow1(patternScanner, methodNode)) {
                    addContext("Detected constant flow");
                    detected.set(true);
                }

                if (detected.get() && context.size() == 3)
                    break;
            }
        }

        return detected.get();
    }

    private boolean lightFlow0(PatternScanner patternScanner, MethodNode methodNode) {
        patternScanner.setPattern(new int[] {
                GOTO,
                LABEL,
                POP,
                LABEL,
                GETSTATIC,
                P_ANY,
                LCMP,
                DUP,
                IFEQ,
                P_ANY,
                IF_ICMPNE
        });
        return !patternScanner.scanMethod(methodNode).isEmpty();
    }

    private boolean lightFlow1(PatternScanner patternScanner, MethodNode methodNode) {
        patternScanner.setPattern(new int[] {
                GETSTATIC,
                GOTO,
                LABEL,
                P_ANY,
                LDIV,
                LABEL,
                L2I,
                LOOKUPSWITCH
        });
        return !patternScanner.scanMethod(methodNode).isEmpty();
    }

    private boolean constantFlow1(PatternScanner patternScanner, MethodNode methodNode) {
        patternScanner.setPattern(new int[] {
                P_ANY,
                P_ANY,
                LCMP,
                ISTORE,
                ILOAD,
                IFNE,
                LABEL,
                P_ANY,
                GOTO,
                LABEL,
        });
        return !patternScanner.scanMethod(methodNode).isEmpty();
    }
}