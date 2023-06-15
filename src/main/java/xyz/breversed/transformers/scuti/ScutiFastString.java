package xyz.breversed.transformers.scuti;

import org.objectweb.asm.tree.*;
import xyz.breversed.api.asm.pattern.PatternParts;
import xyz.breversed.api.asm.pattern.PatternScanner;
import xyz.breversed.api.asm.pattern.result.InsnResult;
import xyz.breversed.api.asm.transformer.Transformer;
import xyz.breversed.api.asm.utils.ASMUtil;

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
        return ASMUtil.getIntValue(methodNode.instructions.get(12));
    }

    private String decrypt(String string, int key) {
        StringBuilder stringBuilder = new StringBuilder();
        int n = 0;
        while (n < string.length()) {
            stringBuilder.append((char)(string.charAt(n) ^ key));
            ++n;
        }
        return stringBuilder.toString();
    }
}