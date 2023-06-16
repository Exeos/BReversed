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
        boolean detected = false;
        PatternScanner patternScanner = new PatternScanner();

        for (ClassNode classNode : getClasses()) {
            for (FieldNode field : classNode.fields) {
                if (field.name.equals(String.valueOf((char)5096)) && field.desc.equals("J")) {
                    addContext("Found flow field");
                    detected = true;
                    break;
                }
            }

            for (MethodNode methodNode : classNode.methods) {
                if (lightFlow0(patternScanner, methodNode) || lightFlow1(patternScanner, methodNode)) {
                    addContext("Detected Light flow pattern");
                    detected = true;
                }
                if (constantFlow1(patternScanner, methodNode)) {
                    addContext("Detected constant flow");
                    detected = true;
                }

                if (detected && context.size() > 1)
                    break;
            }
        }

        return detected;
    }

    private boolean lightFlow0(PatternScanner patternScanner, MethodNode methodNode) {
        patternScanner.setPattern(new int[] {
                GOTO,
                LABEL,
                POP,
                LABEL,
                GETSTATIC,
                P_NUMBER,
                LCMP,
                DUP,
                IFEQ,
                P_NUMBER,
                IF_ICMPNE
        });
        return !patternScanner.scanMethod(methodNode).isEmpty();
    }

    private boolean lightFlow1(PatternScanner patternScanner, MethodNode methodNode) {
        patternScanner.setPattern(new int[] {
                GETSTATIC,
                GOTO,
                LABEL,
                P_NUMBER,
                LDIV,
                LABEL,
                L2I,
                LOOKUPSWITCH,
                LABEL
        });
        return !patternScanner.scanMethod(methodNode).isEmpty();
    }

    private boolean constantFlow1(PatternScanner patternScanner, MethodNode methodNode) {
        patternScanner.setPattern(new int[] {
                P_NUMBER,
                P_NUMBER,
                LCMP,
                ISTORE,
                ILOAD,
                IFNE,
                LABEL,
                P_NUMBER,
                GOTO,
                LABEL
        });
        return !patternScanner.scanMethod(methodNode).isEmpty();
    }
}