package xyz.breversed.transformers.bozar;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import xyz.breversed.api.asm.transformer.Transformer;
import xyz.breversed.api.asm.utils.ASMUtil;
public class BozarNumber extends Transformer {

    @Override
    protected void transform() {
        getClasses().forEach(classNode -> classNode.methods.forEach(methodNode -> {
            for (AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                if (ASMUtil.isRemoved(insnNode, methodNode))
                    continue;
                /* removing "\0".length() */
                if (insnNode instanceof LdcInsnNode) {
                    LdcInsnNode ldcInsn = (LdcInsnNode) insnNode;

                    if (ldcInsn.cst instanceof String ) {
                        String value = (String) ldcInsn.cst;

                        if (!value.replace("\0", "").isEmpty()) {
                            System.out.println("b:" + value);
                            return;
                        }

                        methodNode.instructions.remove(insnNode.getNext());
                        methodNode.instructions.insert(insnNode, ASMUtil.getIntPush(value.length()));
                        methodNode.instructions.remove(insnNode);
                    }
                }
            }

            /* XOR & Shift to single ins */
            for (AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                if (ASMUtil.isRemoved(insnNode, methodNode))
                    continue;
                if (ASMUtil.isLongOrIntPush(insnNode) && ASMUtil.isLongOrIntPush(insnNode.getNext())) {
                    /* XOR */
                    if (ASMUtil.getNext(insnNode, 2).getOpcode() == IXOR) {
                        int result = ASMUtil.getIntValue(insnNode) ^ ASMUtil.getIntValue(insnNode.getNext());

                        methodNode.instructions.remove(insnNode.getNext());
                        methodNode.instructions.remove(insnNode.getNext());
                        methodNode.instructions.insert(insnNode, ASMUtil.getIntPush(result));
                        methodNode.instructions.remove(insnNode);
                    } else if (ASMUtil.getNext(insnNode, 2).getOpcode() == LXOR) {
                        long result = ASMUtil.getLongValue(insnNode) ^ ASMUtil.getLongValue(insnNode.getNext());

                        methodNode.instructions.remove(insnNode.getNext());
                        methodNode.instructions.remove(insnNode.getNext());
                        methodNode.instructions.insert(insnNode, ASMUtil.getLongPush(result));
                        methodNode.instructions.remove(insnNode);
                    } else /* Shift */{
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