package xyz.breversed.core.transformers.test;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import xyz.breversed.core.api.asm.pattern.PatternParts;
import xyz.breversed.core.api.asm.pattern.PatternScanner;
import xyz.breversed.core.api.asm.pattern.result.InsnResult;
import xyz.breversed.core.api.asm.transformer.Transformer;

public class TestTrany extends Transformer implements PatternParts {

    @Override
    protected void transform() {
        PatternScanner patternScanner = new PatternScanner(new int[] {
                INVOKESPECIAL,
                LDC,
                INVOKEVIRTUAL,

                P_SKIPTO,

                INVOKESPECIAL,
                ASTORE
        });
        for (ClassNode classNode : getClasses()) {
            System.out.println(classNode.name + " {");
            for (MethodNode methodNode : classNode.methods) {
                System.out.println("\t" + methodNode.name + " {");
                for (InsnResult result : patternScanner.scanMethod(methodNode)) {
                    System.out.println("\t\tfirst index: " + methodNode.instructions.indexOf(result.getFirst()));
                    System.out.println("\t\tlast index: " + methodNode.instructions.indexOf(result.getLast()));
                    System.out.println("\t\t {");
                    for (AbstractInsnNode insnNode : result.pattern) {
                        System.out.println("\t\t\topcode: " + insnNode.getOpcode());
                    }
                    System.out.println("\t\t}");
                }
                System.out.println("\t}");
            }
            System.out.println("}");
        }
    }
}
