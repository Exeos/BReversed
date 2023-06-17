package xyz.breversed.detectors.scuti;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import xyz.breversed.api.asm.detection.AbstractDetector;
import xyz.breversed.api.asm.pattern.PatternParts;
import xyz.breversed.api.asm.pattern.PatternScanner;
import xyz.breversed.api.asm.pattern.result.InsnResult;
import xyz.breversed.api.asm.utils.ASMUtil;

public class ScutiFastString extends AbstractDetector implements PatternParts {

    @Override
    protected boolean detect() {
        boolean detected = false;

        PatternScanner patternScanner = new PatternScanner(new int[] {
                LDC,
                INVOKESTATIC
        });

        for (ClassNode classNode : getClasses()) {
            for (MethodNode methodNode : classNode.methods) {
                for (InsnResult result : patternScanner.scanMethod(methodNode)) {
                    if (!(((LdcInsnNode) result.getFirst()).cst instanceof String))
                        continue;

                    MethodInsnNode decryptCall = (MethodInsnNode) result.getLast();
                    int key = getKeyByMethod(ASMUtil.getMethod(classNode, decryptCall));
                    if (key != -1) {
                        addContext("Found string decryption method & key");
                        detected = true;
                    }
                }
            }
        }

        return detected;
    }

    private int getKeyByMethod(MethodNode methodNode) {
        PatternScanner patternScanner = new PatternScanner(new int[] {
                INVOKEVIRTUAL,
                P_NUMBER
        });

        for (InsnResult result : patternScanner.scanMethod(methodNode)) {
            if (!ASMUtil.isIntPush(result.getLast()))
                continue;
            return ASMUtil.getIntValue(result.getLast());
        }

        return -1;
    }
}