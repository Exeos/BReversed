package xyz.breversed.transformers.bozar;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;
import xyz.breversed.api.asm.pattern.PatternParts;
import xyz.breversed.api.asm.pattern.PatternScanner;
import xyz.breversed.api.asm.pattern.result.InsnResult;
import xyz.breversed.api.asm.transformer.Transformer;
import xyz.breversed.api.asm.utils.ASMUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BozarString extends Transformer implements PatternParts {

    @Override
    protected void transform() {
        PatternScanner patternScanner = new PatternScanner(new int[] {
                P_NUMBER,
                NEWARRAY,
                ASTORE,

                P_SKIPTO,

                NEW,
                DUP,
                ALOAD,
                INVOKESPECIAL
        });

        for (ClassNode classNode : getClasses()) {
            for (MethodNode methodNode : classNode.methods) {
                for (InsnResult result : patternScanner.scanMethod(methodNode)) {
                    List<AbstractInsnNode> toRemove = new ArrayList<>();

                    /* key = index in array, value = byte value*/
                    HashMap<Integer, Byte> strMap = new HashMap<>();
                    byte[] strBytes;

                    /* first time index 0 is assigned a random number*/
                    boolean overFake = false;


                    for (int i = 3; i < result.pattern.length - 4; i++) {
                        AbstractInsnNode current = result.pattern[i];

                        toRemove.add(current);
                        if (current.getOpcode() == BASTORE) {
                            if (overFake) {
                                try {
                                    int at = ASMUtil.getIntValue(ASMUtil.getPrev(current, 2));
                                    int value = ASMUtil.getIntValue(current.getPrevious());

                                    strMap.put(at, (byte) value);
                                } catch (ClassCastException e) {
                                    break;
                                }
                            }
                            overFake = true;
                        }
                    }

                    /* Pattern isn't string obf */
                    if (!overFake)
                        continue;

                    /* Remove current and 3 prev before */
                    for (int i = 0; i < 4; i++) {
                        toRemove.add(ASMUtil.getPrev(result.getLast(), i));
                    }

                    /* initializing the byte array with correct size */
                    strBytes = new byte[strMap.size()];
                    /* Assigning each offset the correct bytes */
                    strMap.forEach((index, value) -> strBytes[index] = value);

                    methodNode.instructions.insert(result.getFirst(), new LdcInsnNode(new String(strBytes)));
                    ASMUtil.removeInsnNodes(methodNode, result.pattern);
                    ASMUtil.removeInsnNodes(methodNode, toRemove);
                }
            }
        }
    }
}
