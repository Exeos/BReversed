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
                detected.set(lightFlow0(patternScanner, methodNode) || lightFlow1(patternScanner, methodNode));

                if (detected.get()) {
                    addContext("Detected Light flow pattern");
                    break;
                }
            }
        }

        return detected.get();
    }

    private boolean lightFlow0(PatternScanner patternScanner, MethodNode methodNode) {
        patternScanner.setPattern(new int[] {
                GOTO,
                P_LABEL,
                POP,
                P_LABEL,
                GETSTATIC,
                P_ANY,
                LCMP,
                DUP,
                IFEQ,
                P_ANY,
                IF_ICMPNE
        });
        return patternScanner.scanMethod(methodNode) != null;
    }

    private boolean lightFlow1(PatternScanner patternScanner, MethodNode methodNode) {
        patternScanner.setPattern(new int[] {
                GETSTATIC,
                GOTO,
                P_LABEL,
                P_ANY,
                LDIV,
                P_LABEL,
                L2I,
                LOOKUPSWITCH
        });
        return patternScanner.scanMethod(methodNode) != null;
    }
}