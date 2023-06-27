package xyz.breversed.core.transformers.bozar;

import me.exeos.asmplus.pattern.PatternParts;
import me.exeos.asmplus.pattern.PatternScanner;
import me.exeos.asmplus.pattern.result.InsnResult;
import me.exeos.asmplus.utils.ASMUtils;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import xyz.breversed.core.api.asm.transformer.Transformer;

import java.util.ArrayList;
import java.util.List;

public class BozarConstantFlow extends Transformer implements PatternParts {

    @Override
    protected void transform() {
        PatternScanner patternScanner = new PatternScanner(new int[] {
                P_NUMBER,
                P_NUMBER,
                LCMP,
                ISTORE,
                ILOAD,
                IFNE,
                LABEL,
                P_NUMBER,
                GOTO,
                LABEL
        });

        for (ClassNode classNode : getClasses()) {
            for (MethodNode methodNode : classNode.methods) {
                for (InsnResult result : patternScanner.scanMethod(methodNode)) {
                    List<AbstractInsnNode> toRemove = new ArrayList<>();
                    for (int i = 0; i < 10; i++) {
                        toRemove.add(ASMUtils.getNext(result.getFirst(), i));
                    }
                    for (int i = 2; i < 11; i++) {
                        toRemove.add(ASMUtils.getNext(result.getLast(), i));
                    }
                    ASMUtils.removeInstructions(toRemove, methodNode);
                }
            }
        }
    }
}
