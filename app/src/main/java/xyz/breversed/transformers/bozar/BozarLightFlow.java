package xyz.breversed.transformers.bozar;

import me.exeos.asmplus.pattern.PatternParts;
import me.exeos.asmplus.pattern.PatternScanner;
import me.exeos.asmplus.pattern.result.InsnResult;
import me.exeos.asmplus.utils.ASMUtils;
import org.objectweb.asm.tree.*;
import xyz.breversed.api.asm.transformer.Transformer;

import java.util.ArrayList;
import java.util.List;

public class BozarLightFlow extends Transformer implements PatternParts {

    @Override
    protected void transform() {
        PatternScanner patternScanner = new PatternScanner();

        for (ClassNode classNode : getClasses()) {
            /* Bozar always adds this field */
            FieldNode flowField = ASMUtils.getField(classNode.name, String.valueOf((char)5096), "J");
            if (flowField == null)
                continue;

            classNode.fields.remove(flowField);
            for (MethodNode methodNode : classNode.methods) {
                for (InsnResult result : before0(patternScanner, methodNode)) {
                    /* This is the first insn of the pattern that follows the real insn */
                    AbstractInsnNode firstAfter = ASMUtils.getNext(result.getLast(), 2);
                    List<AbstractInsnNode> toRemove = new ArrayList<>();

                    for (int i = 0; i < 5; i++) {
                        toRemove.add(ASMUtils.getNext(firstAfter, i));
                    }
                    ASMUtils.removeInstructions(result.pattern, methodNode);
                    ASMUtils.removeInstructions(toRemove, methodNode);
                }

                for (InsnResult result : before1(patternScanner, methodNode)) {
                    /* Now that the pattern is found, it will remove the random switch. The correct code is always in the dflt label */
                    LookupSwitchInsnNode lookupSwitch = (LookupSwitchInsnNode) result.pattern[result.pattern.length - 2];
                    /* This is the beginning of the lookup body */
                    AbstractInsnNode current = result.getLast();
                    List<AbstractInsnNode> toRemove = new ArrayList<>();

                    while ((current = ASMUtils.getNext(current, 1)) != lookupSwitch.dflt) {
                        toRemove.add(current);
                    }
                    ASMUtils.removeInstructions(result.pattern, methodNode);
                    ASMUtils.removeInstructions(toRemove, methodNode);
                }
            }
        }
    }

    private List<InsnResult> before0(PatternScanner patternScanner, MethodNode methodNode) {
        patternScanner.setPattern(new int[] {
                GOTO,
                LABEL,
                POP,
                LABEL,
                GETSTATIC,
                P_NUMBER,
                LCMP,
                DUP,
                IFEQ,
                P_NUMBER,
                IF_ICMPNE
        });
        return patternScanner.scanMethod(methodNode);
    }

    private List<InsnResult> before1(PatternScanner patternScanner, MethodNode methodNode) {
        patternScanner.setPattern(new int[] {
                GETSTATIC,
                GOTO,
                LABEL,
                P_NUMBER,
                LDIV,
                LABEL,
                L2I,
                LOOKUPSWITCH,
                LABEL
        });
        return patternScanner.scanMethod(methodNode);
    }
}