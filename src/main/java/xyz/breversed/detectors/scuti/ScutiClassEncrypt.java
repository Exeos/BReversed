package xyz.breversed.detectors.scuti;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import xyz.breversed.api.asm.detection.AbstractDetector;
import xyz.breversed.api.asm.pattern.PatternParts;
import xyz.breversed.api.asm.pattern.PatternScanner;
import xyz.breversed.api.asm.pattern.result.InsnResult;
import xyz.breversed.api.asm.utils.ASMUtil;

public class ScutiClassEncrypt extends AbstractDetector implements PatternParts {

    @Override
    protected boolean detect() {
        boolean detected = false;

        for (ClassNode classNode : getClasses()) {
            int key = getStringDecryptionKey(classNode);
            if (key != -1) {
                addContext("Found string decryption method & key");
                detected = true;
            }

            key = getFileDecryptionKey(classNode);
            if (key != -1) {
                addContext("Found file decryption method & key");
                detected = true;
            }
        }

        return detected;
    }

    private int getStringDecryptionKey(ClassNode classNode) {
        MethodNode decryptMethod = classNode.methods.stream()
                .filter(methodNode -> methodNode.desc.equals("(Ljava/lang/String;)Ljava/lang/String;"))
                .findFirst().orElse(null);
        if (decryptMethod == null)
            return -1;

        PatternScanner patternScanner = new PatternScanner(new int[] {
                P_NUMBER,
                IXOR
        });
        for (InsnResult result : patternScanner.scanMethod(decryptMethod))
            return ASMUtil.getIntValue(result.getFirst());

        return -1;
    }
    private int getFileDecryptionKey(ClassNode classNode) {
        MethodNode decryptMethod = classNode.methods.stream()
                .filter(methodNode -> methodNode.desc.equals("([B)[B"))
                .findFirst().orElse(null);
        if (decryptMethod == null)
            return -1;

        PatternScanner patternScanner = new PatternScanner(new int[] {
                P_NUMBER,
                IXOR
        });
        for (InsnResult result : patternScanner.scanMethod(decryptMethod))
            return ASMUtil.getIntValue(result.getFirst());

        return -1;
    }
}