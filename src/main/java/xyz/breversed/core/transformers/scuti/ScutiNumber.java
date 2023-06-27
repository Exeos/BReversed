package xyz.breversed.core.transformers.scuti;

import me.exeos.asmplus.pattern.PatternParts;
import me.exeos.asmplus.pattern.PatternScanner;
import me.exeos.asmplus.pattern.result.InsnResult;
import me.exeos.asmplus.utils.ASMUtils;
import org.objectweb.asm.tree.*;
import xyz.breversed.core.api.asm.transformer.Transformer;

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
                        int[] values = new int[] { ASMUtils.getIntValue(result.pattern[0]), ASMUtils.getIntValue(result.pattern[1]),
                                ASMUtils.getIntValue(result.pattern[3]), ASMUtils.getIntValue(result.pattern[5]) };

                        int value = values[0] ^ values[1] ^ values[2] ^ values[3];
                        methodNode.instructions.insertBefore(result.getFirst(), ASMUtils.getIntPush(value));

                        for (AbstractInsnNode node : result.pattern)
                            methodNode.instructions.remove(node);
                    }
                    results = patternScanner.scanMethod(methodNode);
                }
            }
        }
    }
}