package xyz.breversed.core.detectors.scuti;

import me.exeos.asmplus.pattern.PatternParts;
import me.exeos.asmplus.pattern.PatternScanner;
import me.exeos.asmplus.pattern.result.InsnResult;
import me.exeos.asmplus.utils.ASMUtils;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import xyz.breversed.core.api.asm.detection.AbstractDetector;

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
                    int key = getKeyByMethod(ASMUtils.getMethod(classNode, decryptCall.name, decryptCall.desc));
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
            if (ASMUtils.isIntPush(result.getLast()))
                return ASMUtils.getIntValue(result.getLast());
        }

        return -1;
    }
}
