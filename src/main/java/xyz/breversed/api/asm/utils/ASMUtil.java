package xyz.breversed.api.asm.utils;

import lombok.experimental.UtilityClass;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

@UtilityClass
public class ASMUtil implements Opcodes {

    public MethodNode getMethod(ClassNode classNode, String name, String desc) {
        return classNode.methods.stream().filter(methodNode -> methodNode.name.equals(name) && methodNode.desc.equals(desc)).findFirst().orElse(null);
    }

    public MethodNode getMethod(ClassNode classNode, String name, String desc, String signature) {
        return classNode.methods.stream().filter(methodNode -> methodNode.name.equals(name) && methodNode.desc.equals(desc) && methodNode.signature.equals(signature)).findFirst().orElse(null);
    }

    /*
    * Just like getNext()V from AbstractInsnNode except:
    *
    * You avoid null pointers & repeating yourself
    */
    public AbstractInsnNode getNext(AbstractInsnNode current, int count) {
        AbstractInsnNode next = current;
        for (int i = 0; i < count; i++) {
            if (next.getNext() == null)
                return next;
            else
                next = next.getNext();
        }
        return next;
    }

    /*
     * Just like getPrev()V from AbstractInsnNode except:
     *
     * You avoid null pointers & repeating yourself
     */
    public AbstractInsnNode getPrev(AbstractInsnNode current, int count) {
        AbstractInsnNode prev = current;
        for (int i = 0; i < count; i++) {
            if (prev.getPrevious() == null)
                return prev;
            else
                prev = prev.getPrevious();
        }
        return prev;
    }

    /*
     * Detect if a long gets pushed onto the stack
     */
    private boolean isLongPush(AbstractInsnNode insnNode) {
        return insnNode.getOpcode() == ICONST_0 || insnNode.getOpcode() == ICONST_1 || insnNode instanceof LdcInsnNode;
    }

    /*
     * Detect if an int gets pushed onto the stack
     */
    private boolean isIntPush(AbstractInsnNode insnNode) {
        return insnNode instanceof IntInsnNode || insnNode instanceof LdcInsnNode || (insnNode.getOpcode() >= ICONST_M1 && insnNode.getOpcode() <= ICONST_5);
    }
}