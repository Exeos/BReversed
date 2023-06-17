package xyz.breversed.core.transformers.bozar;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import xyz.breversed.core.api.asm.pattern.PatternParts;
import xyz.breversed.core.api.asm.pattern.PatternScanner;
import xyz.breversed.core.api.asm.pattern.result.InsnResult;
import xyz.breversed.core.api.asm.transformer.Transformer;
import xyz.breversed.core.api.asm.utils.ASMUtil;

import java.util.ArrayList;
import java.util.List;

public class BozarConstantFlow extends Transformer implements PatternParts {

    @Override
    protected void transform() {
        PatternScanner patternScanner = new PatternScanner();

        for (ClassNode classNode : getClasses()) {
            for (MethodNode methodNode : classNode.methods) {
                for (InsnResult result : scanBefore(patternScanner, methodNode)) {
                    List<AbstractInsnNode> toRemove = new ArrayList<>();
                    for (int i = 0; i < 10; i++) {
                        toRemove.add(ASMUtil.getNext(result.getFirst(), i));
                    }
                    for (int i = 2; i < 11; i++) {
                        toRemove.add(ASMUtil.getNext(result.getLast(), i));
                    }
                    ASMUtil.removeInsnNodes(methodNode, toRemove);
                }
            }
        }
    }

    private List<InsnResult> scanBefore(PatternScanner patternScanner, MethodNode methodNode) {
        patternScanner.setPattern(new int[] {
                P_NUMBER,
                P_NUMBER,
                LCMP,
                ISTORE,
                ILOAD,
                IFNE,
                LABEL,
                P_NUMBER,
                GOTO,
                LABEL,
        });
        return patternScanner.scanMethod(methodNode);
    }
}
