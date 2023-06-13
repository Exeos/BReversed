package xyz.breversed.transformers.bozar;

import org.objectweb.asm.tree.*;
import xyz.breversed.api.asm.pattern.PatternParts;
import xyz.breversed.api.asm.pattern.PatternScanner;
import xyz.breversed.api.asm.pattern.result.InsnResult;
import xyz.breversed.api.asm.transformer.Transformer;
import xyz.breversed.api.asm.utils.ASMUtil;

import java.util.ArrayList;
import java.util.List;

public class BozarLightFlow extends Transformer implements PatternParts {

    @Override
    protected void transform() {
        PatternScanner patternScanner = new PatternScanner();

        for (ClassNode classNode : getClasses()) {
            /* Bozar always adds this field */
            if (ASMUtil.getField(classNode, String.valueOf((char)5096), "J") == null)
                continue;

            for (MethodNode methodNode : classNode.methods) {

                for (InsnResult result : before0(patternScanner, methodNode)) {
                    /* This is the first insn of the pattern that follows the real insn */
                    AbstractInsnNode firstAfter = ASMUtil.getNext(result.getLast(), 2);
                    List<AbstractInsnNode> toRemove = new ArrayList<>();

                    for (int i = 0; i < 5; i++) {
                        toRemove.add(ASMUtil.getNext(firstAfter, i));
                    }
                    ASMUtil.removeInsnNodes(methodNode, result.pattern);
                    ASMUtil.removeInsnNodes(methodNode, toRemove);
                }

                for (InsnResult result : before1(patternScanner, methodNode)) {
                    /* Now that the pattern is found, it will remove the random switch. The correct code is always in the dflt label */
                    LookupSwitchInsnNode lookupSwitch = (LookupSwitchInsnNode) result.pattern[result.pattern.length - 2];
                    /* This is the beginning of the lookup body */
                    AbstractInsnNode current = result.getLast();
                    List<AbstractInsnNode> toRemove = new ArrayList<>();

                    while ((current = ASMUtil.getNext(current, 1)) != lookupSwitch.dflt) {
                        toRemove.add(current);
                    }
                    ASMUtil.removeInsnNodes(methodNode, result.pattern);
                    ASMUtil.removeInsnNodes(methodNode, toRemove);
                }
            }
        }
    }

    private List<InsnResult> before0(PatternScanner patternScanner, MethodNode methodNode) {
        patternScanner.setPattern(new int[] {
                GOTO,
                P_LABEL,
                POP,
                P_LABEL,
                GETSTATIC,
                P_ANY,
                LCMP,
                DUP,
                IFEQ,
                P_ANY,
                IF_ICMPNE
        });
        return patternScanner.scanMethod(methodNode);
    }

    private List<InsnResult> before1(PatternScanner patternScanner, MethodNode methodNode) {
        patternScanner.setPattern(new int[] {
                GETSTATIC,
                GOTO,
                P_LABEL,
                P_ANY,
                LDIV,
                P_LABEL,
                L2I,
                LOOKUPSWITCH,
                P_LABEL
        });
        return patternScanner.scanMethod(methodNode);
    }
}