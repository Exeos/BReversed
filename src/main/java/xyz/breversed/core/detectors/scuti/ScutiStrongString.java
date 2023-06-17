package xyz.breversed.core.detectors.scuti;

import org.objectweb.asm.tree.*;
import xyz.breversed.api.asm.detection.AbstractDetector;
import xyz.breversed.api.asm.pattern.PatternParts;
import xyz.breversed.api.asm.pattern.PatternScanner;
import xyz.breversed.api.asm.pattern.result.InsnResult;
import xyz.breversed.api.asm.utils.ASMUtil;

import java.util.ArrayList;
import java.util.List;

public class ScutiStrongString extends AbstractDetector implements PatternParts {

    @Override
    protected boolean detect() {
        PatternScanner patternScanner = new PatternScanner(new int[] {
                LDC,
                P_NUMBER
        });

        for (ClassNode classNode : getClasses()) {
            for (MethodNode methodNode : classNode.methods) {
                int possibility = 0;
                for (InsnResult result : patternScanner.scanMethod(methodNode)) {
                    if (!(((LdcInsnNode) result.getFirst()).cst instanceof String))
                        continue;
                    if (!ASMUtil.isIntPush(result.getLast()))
                        continue;
                    int key = ASMUtil.getIntValue(result.getLast());

                    AbstractInsnNode current = result.getLast();
                    MethodInsnNode decryptCall = null;

                    /* we don't know how many NEG and SWAP instructions are in between the key and the decrypt call because its randomised,
                       so we have to iterate through the instructions until we find the decrypt call
                       TODO: find a cleaner way to do this
                     */
                    while (decryptCall == null) {
                        current = current.getNext();
                        possibility++; // there are probably better ways to do this, but it works for now

                        if (current instanceof MethodInsnNode &&
                                ((MethodInsnNode) current).owner.equals(classNode.name) &&
                                ((MethodInsnNode) current).desc.equals("(Ljava/lang/String;I)Ljava/lang/String;")) {
                            decryptCall = (MethodInsnNode) current;
                        }
                    }

                    if (possibility > 0 && decryptCall.name != null && key != 0) {
                        addContext("Found strong encrypted string, key and decryption method");
                        return true;
                    }
                }
            }
        }
        return false;
    }
}