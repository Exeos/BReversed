package xyz.breversed.core.transformers.scuti;

import me.exeos.asmplus.pattern.PatternParts;
import me.exeos.asmplus.pattern.PatternScanner;
import me.exeos.asmplus.pattern.result.InsnResult;
import me.exeos.asmplus.utils.ASMUtils;
import org.objectweb.asm.tree.*;
import xyz.breversed.core.api.asm.transformer.Transformer;

public class ScutiInvokeDynamic extends Transformer implements PatternParts {

    @Override
    protected void transform() {
        PatternScanner patternScanner = new PatternScanner(new int[] {
                INVOKEDYNAMIC
        });

        for (ClassNode classNode : getClasses()) {
            MethodNode stringDecryptMethod = classNode.methods.stream().filter(methodNode -> methodNode.access == 4170 &&
                    methodNode.desc.equals("(Ljava/lang/String;)Ljava/lang/String;")).findFirst().orElse(null);
            MethodNode callDecryptMethod = classNode.methods.stream().filter(methodNode -> methodNode.access == 4169 &&
                    methodNode.desc.equals("(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Integer;)Ljava/lang/invoke/CallSite;")).findFirst().orElse(null);

            if (stringDecryptMethod == null || callDecryptMethod == null)
                continue;

            int key = getKeyByMethod(stringDecryptMethod);

            for (MethodNode methodNode : classNode.methods) {
                for (InsnResult result : patternScanner.scanMethod(methodNode)) {
                    InvokeDynamicInsnNode invokeDynamicCall = (InvokeDynamicInsnNode) result.getFirst();

                    Object[] decryptedArgs = new Object[invokeDynamicCall.bsmArgs.length];

                    for (int i = 0; i < invokeDynamicCall.bsmArgs.length; i++) {
                        Object bsmArgs = invokeDynamicCall.bsmArgs[i];

                        if (bsmArgs instanceof Integer) {
                            decryptedArgs[i] = bsmArgs;
                            continue;
                        }

                        if (!(bsmArgs instanceof String string))
                            continue;

                        decryptedArgs[i] = decrypt(string, key);
                    }

                    MethodInsnNode decryptedCall = new MethodInsnNode((int) decryptedArgs[3] == 0 ? INVOKESTATIC : INVOKEVIRTUAL,
                            ((String) (decryptedArgs[0])).replace(".", "/"), (String) decryptedArgs[1], (String) decryptedArgs[2]);
                    methodNode.instructions.set(invokeDynamicCall, decryptedCall);
                }
            }

            classNode.methods.remove(stringDecryptMethod);
            classNode.methods.remove(callDecryptMethod);
        }
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

    private String decrypt(String string, int key) {
        StringBuilder decrypted = new StringBuilder();
        for (int i = 0; i < string.length(); i++) {
            decrypted.append((char)(string.charAt(i) ^ key));
        }
        return decrypted.toString();
    }
}