package xyz.breversed.transformers.bozar;

import org.objectweb.asm.tree.AbstractInsnNode;
import xyz.breversed.api.asm.pattern.PatternParts;
import xyz.breversed.api.asm.pattern.PatternScanner;
import xyz.breversed.api.asm.pattern.result.InsnResult;
import xyz.breversed.api.asm.transformer.Transformer;
import xyz.breversed.api.asm.utils.ASMUtil;

import java.util.Arrays;
import java.util.List;

public class BozarString extends Transformer implements PatternParts {

    @Override
    protected void transform() {
        PatternScanner patternScanner = new PatternScanner(new int[] {
                P_ANY,
                NEWARRAY,
                ASTORE
        });

        List<Integer> included = Arrays.stream(new int[] {
                ALOAD,

                ICONST_0 - 1,
                ICONST_0,
                ICONST_1,
                ICONST_2,
                ICONST_3,
                ICONST_4,
                ICONST_5,
                BIPUSH,
                SIPUSH,
                LDC,

                BASTORE
        }).boxed().toList();

        getClasses().forEach(classNode -> classNode.methods.forEach(methodNode -> {
            for (InsnResult result : patternScanner.scanMethod(methodNode)) {
                AbstractInsnNode current = result.getLast().getNext();

                int amount = 0;

                while (included.contains(current.getOpcode())) {
                    if (current.getOpcode() == BASTORE)
                        amount++;
                    current = ASMUtil.getNext(current, 1);
                }
            }
        }));
        patternScanner.scanArchive(getClasses()).forEach(classResult -> classResult.methodResults.forEach(methodResult -> methodResult.foundPatterns.forEach(insnResult -> {
            AbstractInsnNode last = insnResult.getLast();
        })));
    }
}
