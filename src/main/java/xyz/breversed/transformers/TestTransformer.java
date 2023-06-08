package xyz.breversed.transformers;

import org.objectweb.asm.tree.AbstractInsnNode;
import xyz.breversed.api.asm.pattern.PatternParts;
import xyz.breversed.api.asm.pattern.PatternScanner;
import xyz.breversed.api.asm.transformer.Transformer;

public class TestTransformer extends Transformer implements PatternParts {

    @Override
    protected void transform() {
        int[] pattern = new int[] {
                P_LABEL,
                P_LINE,
                NEW,
                DUP,
                LDC,
                P_ANY,
                ASTORE
        };
        PatternScanner patternScanner = new PatternScanner(pattern);

        getClasses().forEach(classNode -> {
            classNode.methods.forEach(methodNode -> {

                AbstractInsnNode patternStart = patternScanner.scanMethod(methodNode);

                if (patternStart != null) {
                    System.out.println("Pattern Found!");
                    System.out.println("Class: " + classNode.name);
                    System.out.println("Method: " + methodNode.name);
                    System.out.println("Start index: " + methodNode.instructions.indexOf(patternStart));
                }

            });
        });
    }
}