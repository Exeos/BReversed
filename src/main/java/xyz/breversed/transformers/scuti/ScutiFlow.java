package xyz.breversed.transformers.scuti;

import me.exeos.asmplus.JarLoader;
import me.exeos.asmplus.pattern.PatternParts;
import me.exeos.asmplus.pattern.PatternScanner;
import me.exeos.asmplus.pattern.result.InsnResult;
import me.exeos.asmplus.utils.ASMUtils;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;
import xyz.breversed.api.asm.transformer.Transformer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ScutiFlow extends Transformer implements PatternParts {

    @Override
    protected void transform() {
        PatternScanner flowPattern = new PatternScanner(new int[] {
                GETSTATIC,
                GETSTATIC,
                JUMP_INSN,
                ACONST_NULL,
                P_ANY,
                ATHROW,
                P_ANY,
                ATHROW,
                P_ANY
        });
        PatternScanner secondFlowPattern = getSecondFlowPattern();
        PatternScanner thirdFlowPattern = getThirdFlowPattern();
        PatternScanner staticInitializerPattern = getStaticInitializerPattern();

        for (ClassNode classNode : getClasses()) {
            if (classNode.name.startsWith("                          ")) {
                removeClass(classNode);
                continue;
            }

            for (MethodNode methodNode : classNode.methods) {
                if (methodNode.name.equals("<clinit>"))
                    for (InsnResult result : staticInitializerPattern.scanMethod(methodNode))
                        ASMUtils.removeInstructions(result.pattern, methodNode);

                for (InsnResult result : flowPattern.scanMethod(methodNode)) {

                    // Remove trashy instructions
                    List<AbstractInsnNode> toRemove = new ArrayList<>(Arrays.asList(result.pattern));
                    toRemove.removeIf(node -> node instanceof LabelNode);
                    ASMUtils.removeInstructions(toRemove, methodNode);

                    // Remove try catch blocks
                    methodNode.tryCatchBlocks.removeIf(tryCatchBlockNode -> tryCatchBlockNode.type.startsWith("                          "));
                }

                for (InsnResult result : secondFlowPattern.scanMethod(methodNode)) {

                    // Remove trashy instructions
                    List<AbstractInsnNode> toRemove = new ArrayList<>(Arrays.asList(result.pattern));
                    toRemove.removeIf(node -> node instanceof LabelNode);
                    ASMUtils.removeInstructions(toRemove, methodNode);
                }

                for (InsnResult result : thirdFlowPattern.scanMethod(methodNode)) {

                    // Remove trashy instructions
                    List<AbstractInsnNode> toRemove = new ArrayList<>(Arrays.asList(result.pattern));
                    toRemove.removeIf(node -> node instanceof LabelNode);
                    ASMUtils.removeInstructions(toRemove, methodNode);
                }

                // Remove fields
                classNode.fields.removeIf(fieldNode -> fieldNode.name.startsWith("                          "));
            }
        }
    }

    private PatternScanner getStaticInitializerPattern() {
        return new PatternScanner(new int[] {
                P_NUMBER,
                P_NUMBER,
                IXOR,
                PUTSTATIC,
                P_NUMBER,
                P_NUMBER,
                IXOR,
                PUTSTATIC
        });
    }

    private PatternScanner getSecondFlowPattern() {
        return new PatternScanner(new int[] {
                GOTO,
                P_ANY,
                GOTO,
                P_ANY,
                GETSTATIC,
                GETSTATIC,
                JUMP_INSN,
                ACONST_NULL,
                ATHROW
        });
    }

    private PatternScanner getThirdFlowPattern() {
        return new PatternScanner(new int[] {
                ATHROW,
                P_ANY,
                JUMP_INSN,
                P_ANY,
                GETSTATIC,
                GETSTATIC,
                JUMP_INSN,
                ACONST_NULL,
                ATHROW
        });
    }
}