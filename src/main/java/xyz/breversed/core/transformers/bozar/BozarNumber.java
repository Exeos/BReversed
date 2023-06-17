package xyz.breversed.core.transformers.bozar;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import xyz.breversed.core.api.asm.transformer.Transformer;
import xyz.breversed.core.api.asm.utils.ASMUtil;

public class BozarNumber extends Transformer {

    @Override
    protected void transform() {
        getClasses().forEach(classNode -> classNode.methods.forEach(methodNode -> {
            for (AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                if (ASMUtil.isRemoved(insnNode, methodNode))
                    continue;
                /* removing "\0".length() */
                if (insnNode instanceof LdcInsnNode ldcInsn && ldcInsn.cst instanceof String value) {
                    if (!value.replace("\0", "").isEmpty())
                        continue;

                    methodNode.instructions.remove(insnNode.getNext());
                    methodNode.instructions.insert(insnNode, ASMUtil.getIntPush(value.length()));
                    methodNode.instructions.remove(insnNode);
                }
            }

            /* XOR & Shift to single ins */
            for (AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                if (ASMUtil.isRemoved(insnNode, methodNode))
                    continue;
                if (ASMUtil.isLongOrIntPush(insnNode) && ASMUtil.isLongOrIntPush(insnNode.getNext())) {
                    /* XOR */
                    if (ASMUtil.getNext(insnNode, 2).getOpcode() == IXOR || ASMUtil.getNext(insnNode, 2).getOpcode() == LXOR) {
                        boolean isIXOR = ASMUtil.getNext(insnNode, 2).getOpcode() == IXOR;
                        Object realValuePushInsn = (isIXOR ? ASMUtil.getIntPush(ASMUtil.getIntValue(insnNode) ^ ASMUtil.getIntValue(insnNode.getNext())) :
                                ASMUtil.getLongPush(ASMUtil.getLongValue(insnNode) ^ ASMUtil.getLongValue(insnNode.getNext())));
                        methodNode.instructions.remove(insnNode.getNext());
                        methodNode.instructions.remove(insnNode.getNext());
                        if (isIXOR)
                            methodNode.instructions.insert(insnNode, (AbstractInsnNode) realValuePushInsn);
                        else
                            methodNode.instructions.insert(insnNode, (AbstractInsnNode) realValuePushInsn);
                        methodNode.instructions.remove(insnNode);
                    } else /* Shift */ {
                        if (ASMUtil.getNext(insnNode, 2).getOpcode() == IUSHR) {
                            int result = ASMUtil.getIntValue(insnNode) >>> ASMUtil.getIntValue(insnNode.getNext());

                            methodNode.instructions.remove(insnNode.getNext());
                            methodNode.instructions.remove(insnNode.getNext());
                            methodNode.instructions.insert(insnNode, ASMUtil.getIntPush(result));
                            methodNode.instructions.remove(insnNode);
                        } else if (ASMUtil.getNext(insnNode, 2).getOpcode() == LUSHR) {
                            long result = ASMUtil.getLongValue(insnNode) >>> ASMUtil.getIntValue(insnNode.getNext());

                            methodNode.instructions.remove(insnNode.getNext());
                            methodNode.instructions.remove(insnNode.getNext());
                            methodNode.instructions.insert(insnNode, ASMUtil.getLongPush(result));
                            methodNode.instructions.remove(insnNode);
                        }
                    }
                }
            }

        }));
    }
}