package xyz.breversed.transformers.scuti;

import org.objectweb.asm.tree.*;
import xyz.breversed.api.asm.pattern.PatternParts;
import xyz.breversed.api.asm.pattern.PatternScanner;
import xyz.breversed.api.asm.pattern.result.InsnResult;
import xyz.breversed.api.asm.transformer.Transformer;
import xyz.breversed.api.asm.utils.ASMUtil;

import java.util.ArrayList;
import java.util.List;

public class ScutiStrongString extends Transformer implements PatternParts {

    @Override
    protected void transform() {
        PatternScanner patternScanner = new PatternScanner(new int[] {
                LDC,
                P_NUMBER
        });

        for (ClassNode classNode : getClasses()) {
            MethodNode decryptMethod = null;

            for (MethodNode methodNode : classNode.methods) {
                for (InsnResult result : patternScanner.scanMethod(methodNode)) {
                    if (!(((LdcInsnNode) result.getFirst()).cst instanceof String encrypted))
                        continue;
                    if (!ASMUtil.isIntPush(result.getLast()))
                        continue;
                    int key = ASMUtil.getIntValue(result.getLast());

                    List<AbstractInsnNode> toRemove = new ArrayList<>();
                    AbstractInsnNode current = result.getLast();
                    MethodInsnNode decryptCall = null;

                    /* we don't know how many NEG and SWAP instructions are in between the key and the decrypt call because its randomised,
                       so we have to iterate through the instructions until we find the decrypt call
                       TODO: find a cleaner way to do this
                     */
                    while (decryptCall == null) {
                        current = current.getNext();

                        toRemove.add(current);

                        if (current instanceof MethodInsnNode &&
                                ((MethodInsnNode) current).owner.equals(classNode.name) &&
                                ((MethodInsnNode) current).desc.equals("(Ljava/lang/String;I)Ljava/lang/String;")) {
                            decryptCall = (MethodInsnNode) current;
                        }
                    }

                    decryptMethod = ASMUtil.getMethod(classNode, decryptCall);

                    for (AbstractInsnNode abstractInsnNode : toRemove)
                        methodNode.instructions.remove(abstractInsnNode);
                    methodNode.instructions.remove(result.getLast());

                    ((LdcInsnNode) result.getFirst()).cst = decrypt(decryptCall.name, encrypted, key);
                }

                classNode.methods.remove(decryptMethod);
            }
        }
    }

    private String decrypt(String methodName, String string, int key) {
        int methodHash = methodName.hashCode();
        char[] strArray = new char[string.length()];
        for (int i = 0; i < string.length(); i++) {
            strArray[i] = (char)(string.charAt(i) ^ (key ^ methodHash));
        }
        return new String(strArray);
    }
}