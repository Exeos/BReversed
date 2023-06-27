package xyz.breversed.core.transformers.bozar;

import me.exeos.asmplus.utils.ASMUtils;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import xyz.breversed.core.api.asm.transformer.Transformer;

public class BozarNumber extends Transformer {

    @Override
    protected void transform() {
        getClasses().forEach(classNode -> classNode.methods.forEach(methodNode -> {
            for (AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                if (!ASMUtils.isPresent(insnNode, methodNode))
                    continue;
                /* removing "\0".length() */
                if (insnNode instanceof LdcInsnNode ldcInsn && ldcInsn.cst instanceof String value) {
                    if (!value.replace("\0", "").isEmpty())
                        continue;

                    methodNode.instructions.remove(insnNode.getNext());
                    methodNode.instructions.insert(insnNode, ASMUtils.getIntPush(value.length()));
                    methodNode.instructions.remove(insnNode);
                }
            }

            /* XOR & Shift to single ins */
            for (AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                if (!ASMUtils.isPresent(insnNode, methodNode))
                    continue;
                if (ASMUtils.isNumberPush(insnNode) && ASMUtils.isNumberPush(insnNode.getNext())) {
                    /* XOR */
                    if (ASMUtils.getNext(insnNode, 2).getOpcode() == IXOR || ASMUtils.getNext(insnNode, 2).getOpcode() == LXOR) {
                        boolean isIXOR = ASMUtils.getNext(insnNode, 2).getOpcode() == IXOR;
                        Object realValuePushInsn = (isIXOR ? ASMUtils.getIntPush(ASMUtils.getIntValue(insnNode) ^ ASMUtils.getIntValue(insnNode.getNext())) :
                                ASMUtils.getLongPush(ASMUtils.getLongValue(insnNode) ^ ASMUtils.getLongValue(insnNode.getNext())));
                        methodNode.instructions.remove(insnNode.getNext());
                        methodNode.instructions.remove(insnNode.getNext());
                        if (isIXOR)
                            methodNode.instructions.insert(insnNode, (AbstractInsnNode) realValuePushInsn);
                        else
                            methodNode.instructions.insert(insnNode, (AbstractInsnNode) realValuePushInsn);
                        methodNode.instructions.remove(insnNode);
                    } else /* Shift */ {
                        if (ASMUtils.getNext(insnNode, 2).getOpcode() == IUSHR) {
                            int result = ASMUtils.getIntValue(insnNode) >>> ASMUtils.getIntValue(insnNode.getNext());

                            methodNode.instructions.remove(insnNode.getNext());
                            methodNode.instructions.remove(insnNode.getNext());
                            methodNode.instructions.insert(insnNode, ASMUtils.getIntPush(result));
                            methodNode.instructions.remove(insnNode);
                        } else if (ASMUtils.getNext(insnNode, 2).getOpcode() == LUSHR) {
                            long result = ASMUtils.getLongValue(insnNode) >>> ASMUtils.getIntValue(insnNode.getNext());

                            methodNode.instructions.remove(insnNode.getNext());
                            methodNode.instructions.remove(insnNode.getNext());
                            methodNode.instructions.insert(insnNode, ASMUtils.getLongPush(result));
                            methodNode.instructions.remove(insnNode);
                        }
                    }
                }
            }
        }));
    }
}