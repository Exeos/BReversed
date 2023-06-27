package xyz.breversed.core.transformers.bozar;

import me.exeos.asmplus.pattern.PatternParts;
import me.exeos.asmplus.pattern.PatternScanner;
import me.exeos.asmplus.pattern.result.InsnResult;
import me.exeos.asmplus.utils.ASMUtils;
import org.objectweb.asm.tree.*;
import xyz.breversed.core.api.asm.transformer.Transformer;

import java.util.ArrayList;
import java.util.List;

public class BozarHeavyFlow extends Transformer implements PatternParts {

    @Override
    protected void transform() {
        PatternScanner patternScanner = new PatternScanner();

        for (ClassNode classNode : getClasses()) {
            /* Bozar always adds this field */
            FieldNode flowField = ASMUtils.getField(classNode.name, String.valueOf((char)5097), "J");
            if (flowField == null)
                continue;

            classNode.fields.remove(flowField);
            for (MethodNode methodNode : classNode.methods) {
                removeFirstLayer(methodNode, firstLayer0(patternScanner, methodNode));
                removeFirstLayer(methodNode, firstLayer1(patternScanner, methodNode));
                removeSecondLayer(patternScanner, methodNode);

                /* removing bozar pseudo code */
            }
        }
    }

    private void removeFirstLayer(MethodNode methodNode, List<InsnResult> results) {
        for (InsnResult result : results) {
            if (!((FieldInsnNode) result.getFirst()).name.equals(String.valueOf((char)5097))) {
                continue;
            }

            List<AbstractInsnNode> toRemove = new ArrayList<>();
            AbstractInsnNode current = result.getLast();
            LabelNode dltLabel = ((LookupSwitchInsnNode) current).dflt;

            /*if (result.getFirst().getPrevious().getType() == AbstractInsnNode.LABEL)
                toRemove.add(result.getFirst().getPrevious());*/

            while ((current = current.getNext()) != dltLabel) {
                toRemove.add(current);
            }
            toRemove.add(current); /* dflt label */
            while ((current = current.getNext()).getType() != AbstractInsnNode.LABEL) {
                toRemove.add(current);
            }
            current = current.getNext(); /* insn after end label */
            int amount = switch (current.getOpcode()) {
                case LXOR -> 10;
                case LCMP -> ASMUtils.getNext(current, 6).getOpcode() == IF_ICMPNE ? 6 : 3;
                case LAND -> 3;
                default -> 0;
            };
            /* pattern mismatch */
            if (amount == 0) {
               // System.out.println("Pattern mismatch");
                continue;
            }

            for (int i = 0; i <= amount; i++) {
                toRemove.add(ASMUtils.getNext(current, i));
            }
            /* we need to remove insns after real*/
            if (current.getOpcode() == LAND) {
                current = ASMUtils.getNext(current, 5); /* first after insn */
                for (int i = 0; i <= 6; i++) {
                    toRemove.add(ASMUtils.getNext(current, i));
                }
            }
            ASMUtils.removeInstructions(result.pattern, methodNode);
            ASMUtils.removeInstructions(toRemove, methodNode);
        }
    }

    private void removeSecondLayer(PatternScanner patternScanner, MethodNode methodNode) {
        for (InsnResult result : secondLayer0(patternScanner, methodNode)) {
            List<AbstractInsnNode> toRemove = new ArrayList<>();

            for (int i = 2; i <= 8; i++) {
                toRemove.add(ASMUtils.getNext(result.getLast(), i));
            }

            ASMUtils.removeInstructions(result.pattern, methodNode);
            ASMUtils.removeInstructions(toRemove, methodNode);
        }

        for (InsnResult result : secondLayer1(patternScanner, methodNode)) {
            if (!((FieldInsnNode) result.getFirst()).name.equals(String.valueOf((char)5097)))
                continue;

            List<AbstractInsnNode> toRemove = new ArrayList<>();
            AbstractInsnNode current = result.getLast();

            while ((current = current.getNext()) != ((LookupSwitchInsnNode) result.getLast()).dflt) {
                toRemove.add(current);
            }
          //  toRemove.add(current) /* dflt label */;
            for (int i = 0; i <= 2; i++) {
                toRemove.add(ASMUtils.getNext(current, i));
            }

            ASMUtils.removeInstructions(result.pattern, methodNode);
            ASMUtils.removeInstructions(toRemove, methodNode);
        }
    }

    private List<InsnResult> firstLayer0(PatternScanner patternScanner, MethodNode methodNode) {
        patternScanner.setPattern(new int[] {
                GETSTATIC,
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

    private List<InsnResult> firstLayer1(PatternScanner patternScanner, MethodNode methodNode) {
        patternScanner.setPattern(new int[] {
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


    private List<InsnResult> secondLayer0(PatternScanner patternScanner, MethodNode methodNode) {
        patternScanner.setPattern(new int[] {
                P_NUMBER,
                P_NUMBER,
                LABEL,
                POP2,
                GETSTATIC,
                P_NUMBER,
                LCMP,
                ICONST_0,
                SWAP,
                DUP,
                IFEQ,
                IFEQ,
                POP
        });
        return patternScanner.scanMethod(methodNode);
    }

    private List<InsnResult> secondLayer1(PatternScanner patternScanner, MethodNode methodNode) {
        patternScanner.setPattern(new int[] {
                GETSTATIC,
                L2I,
                LOOKUPSWITCH
        });
        return patternScanner.scanMethod(methodNode);
    }

    private List<InsnResult> pseudoCode(PatternScanner patternScanner, MethodNode methodNode) {
        patternScanner.setPattern(new int[] {
                ICONST_1,
                GOTO,
                LABEL,
                ICONST_5,
                LABEL,
                ICONST_M1,
                IF_ICMPLE
        });
        return patternScanner.scanMethod(methodNode);
    }
}