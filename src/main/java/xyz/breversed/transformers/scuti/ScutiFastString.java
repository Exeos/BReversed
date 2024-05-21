package xyz.breversed.transformers.scuti;

import me.exeos.asmplus.pattern.PatternParts;
import me.exeos.asmplus.pattern.PatternScanner;
import me.exeos.asmplus.pattern.result.InsnResult;
import me.exeos.asmplus.utils.ASMUtils;
import org.objectweb.asm.tree.*;
import xyz.breversed.api.asm.transformer.Transformer;

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
                    decryptMethod = ASMUtils.getMethod(classNode, decryptCall.name, decryptCall.desc);

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
            if (!ASMUtils.isIntPush(result.getLast()))
                continue;
            return ASMUtils.getIntValue(result.getLast());
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