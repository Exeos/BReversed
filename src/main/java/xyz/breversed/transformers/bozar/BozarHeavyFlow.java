package xyz.breversed.transformers.bozar;

import org.objectweb.asm.tree.*;
import xyz.breversed.api.asm.pattern.PatternParts;
import xyz.breversed.api.asm.pattern.PatternScanner;
import xyz.breversed.api.asm.pattern.result.InsnResult;
import xyz.breversed.api.asm.transformer.Transformer;
import xyz.breversed.api.asm.utils.ASMUtil;

import java.util.ArrayList;
import java.util.List;

public class BozarHeavyFlow extends Transformer implements PatternParts {

    @Override
    protected void transform() {
        PatternScanner patternScanner = new PatternScanner();

        for (ClassNode classNode : getClasses()) {
            /* Bozar always adds this field */
            FieldNode flowField = ASMUtil.getField(classNode, String.valueOf((char)5097), "J");
            if (flowField == null)
                continue;

            classNode.fields.remove(flowField);
            for (MethodNode methodNode : classNode.methods) {
                List<AbstractInsnNode> toRemove = new ArrayList<>();
                for (InsnResult result : before(patternScanner, methodNode)) {
                    AbstractInsnNode current = result.getLast();
                    LabelNode dltLabel = ((LookupSwitchInsnNode) current).dflt;

                    while ((current = current.getNext()) != dltLabel) {
                        toRemove.add(current);
                    }
                    toRemove.add(current);
                    while ((current = current.getNext()).getType() != AbstractInsnNode.LABEL) {
                        toRemove.add(current);
                    }
                    current = current.getNext();
                    int amount = switch (current.getOpcode()) {
                        case LXOR -> 10;
                        case LCMP -> ASMUtil.getNext(current, 6).getOpcode() == IF_ICMPNE ? 6 : 3;
                        case LAND -> 3;
                        default -> 0;
                    };
                    if (amount == 0) {
                        System.out.println("FAILED!");
                        continue;
                    }

                    for (int i = 0; i < amount + 1; i++) {
                        toRemove.add(ASMUtil.getNext(current, i));
                    }
                }
                System.out.println(toRemove.size());
                ASMUtil.removeInsnNodes(methodNode, toRemove);
            }
        }
    }

    private List<InsnResult> before(PatternScanner patternScanner, MethodNode methodNode) {
        patternScanner.setPattern(new int[] {
                LABEL,
                GETSTATIC,
                LABEL,
                P_NUMBER,
                GOTO,
                LABEL,
                P_NUMBER,
                P_NUMBER,
                LABEL,
                DUP2,
                L2I,
                LOOKUPSWITCH
        });
        return patternScanner.scanMethod(methodNode);
    }
}