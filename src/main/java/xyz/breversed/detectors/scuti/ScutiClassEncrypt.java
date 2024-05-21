package xyz.breversed.detectors.scuti;

import me.exeos.asmplus.pattern.PatternParts;
import me.exeos.asmplus.pattern.PatternScanner;
import me.exeos.asmplus.pattern.result.InsnResult;
import me.exeos.asmplus.utils.ASMUtils;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import xyz.breversed.api.asm.detection.AbstractDetector;

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
            return ASMUtils.getIntValue(result.getFirst());

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
            return ASMUtils.getIntValue(result.getFirst());

        return -1;
    }
}