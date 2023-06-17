package xyz.breversed.core.transformers.scuti;

import org.objectweb.asm.tree.*;
import xyz.breversed.core.api.asm.pattern.PatternParts;
import xyz.breversed.core.api.asm.pattern.PatternScanner;
import xyz.breversed.core.api.asm.pattern.result.InsnResult;
import xyz.breversed.core.api.asm.transformer.Transformer;
import xyz.breversed.core.api.asm.utils.ASMUtil;

public class ScutiFastString extends Transformer implements PatternParts {

    @Override
    protected void transform() {
        PatternScanner patternScanner = new PatternScanner(new int[] {
                LDC,
                INVOKESTATIC
        });

        for (ClassNode classNode : getClasses()) {
            MethodNode decryptMethod = null;

            for (MethodNode methodNode : classNode.methods) {
                for (InsnResult result : patternScanner.scanMethod(methodNode)) {
                    if (!(((LdcInsnNode) result.getFirst()).cst instanceof String encrypted))
                        continue;

                    MethodInsnNode decryptCall = (MethodInsnNode) result.getLast();
                    decryptMethod = ASMUtil.getMethod(classNode, decryptCall);

                    int key = getKeyByMethod(decryptMethod);

                    ((LdcInsnNode) result.getFirst()).cst = decrypt(encrypted, key);
                    methodNode.instructions.remove(decryptCall);
                }
            }

            if (decryptMethod != null)
                classNode.methods.remove(decryptMethod);
        }
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

    private String decrypt(String string, int key) {
        StringBuilder decrypted = new StringBuilder();
        for (int i = 0; i < string.length(); i++) {
            decrypted.append((char)(string.charAt(i) ^ key));
        }
        return decrypted.toString();
    }
}