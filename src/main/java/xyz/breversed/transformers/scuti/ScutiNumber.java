package xyz.breversed.transformers.scuti;

import org.objectweb.asm.tree.*;
import xyz.breversed.api.asm.pattern.PatternParts;
import xyz.breversed.api.asm.pattern.PatternScanner;
import xyz.breversed.api.asm.pattern.result.InsnResult;
import xyz.breversed.api.asm.transformer.Transformer;
import xyz.breversed.api.asm.utils.ASMUtil;

import java.util.List;

public class ScutiNumber extends Transformer implements PatternParts {

    @Override
    protected void transform() {
        PatternScanner patternScanner = new PatternScanner(new int[] {
                P_NUMBER,
                P_NUMBER,
                IXOR,
                P_NUMBER,
                IXOR,
                P_NUMBER,
                IXOR
        });

        for (ClassNode classNode : getClasses()) {
            for (MethodNode methodNode : classNode.methods) {
                List<InsnResult> results = patternScanner.scanMethod(methodNode);

                while (!results.isEmpty()) {
                    for (InsnResult result : results) {
                        // put all the xor numbers into an array
                        int[] values = new int[] { ASMUtil.getIntValue(result.pattern[0]), ASMUtil.getIntValue(result.pattern[1]),
                                ASMUtil.getIntValue(result.pattern[3]), ASMUtil.getIntValue(result.pattern[5]) };

                        int value = values[0] ^ values[1] ^ values[2] ^ values[3];
                        methodNode.instructions.insertBefore(result.getFirst(), ASMUtil.getIntPush(value));

                        for (AbstractInsnNode node : result.pattern)
                            methodNode.instructions.remove(node);
                    }
                    results = patternScanner.scanMethod(methodNode);
                }
            }
        }
    }
}