package xyz.breversed.core.api.asm.utils;

import lombok.experimental.UtilityClass;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import xyz.breversed.core.api.asm.JarLoader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@UtilityClass
public class ASMUtil implements Opcodes {

    public void removeInsnNodes(MethodNode from, List<AbstractInsnNode> target) {
        for (AbstractInsnNode instruction : from.instructions.toArray()) {
            if (target.contains(instruction)) {
                target.remove(instruction);
                from.instructions.remove(instruction);
            }
        }
    }

    public void removeInsnNodes(MethodNode from, AbstractInsnNode[] target) {
        removeInsnNodes(from, new ArrayList<>(Arrays.asList(target)));
    }

    public MethodNode getMethod(ClassNode classNode, MethodInsnNode methodInsnNode) {
        return getMethod(classNode, methodInsnNode.name, methodInsnNode.desc);
    }

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
                return null;
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
                return null;
            else
                prev = prev.getPrevious();
        }
        return prev;
    }

    public ClassNode getClass(String name) {
        return JarLoader.classes.stream().filter(classNode -> classNode.name.equals(name)).findFirst().orElse(null);
    }

    /*
     * Look for field in a class by its owner, name and desc
     */
    public FieldNode getField(String owner, String name, String desc) {
        ClassNode classNode = getClass(owner);
        if (classNode == null)
            return null;
        else
            return getField(classNode, name, desc);
    }

    /*
     * Look for field in a class by its name and desc
     */
    public FieldNode getField(ClassNode classNode, String name, String desc) {
        return classNode.fields.stream().filter(fieldNode -> fieldNode.name.equals(name) && fieldNode.desc.equals(desc)).findFirst().orElse(null);
    }

    public AbstractInsnNode getIntPush(int value) {
        if (value >= -1 && value <= 5)
            return new InsnNode(ICONST_0 + value);
        if (value >= Byte.MIN_VALUE && value <= Byte.MAX_VALUE)
            return new IntInsnNode(BIPUSH, value);
        if (value >= Short.MIN_VALUE && value <= Short.MAX_VALUE)
            return new IntInsnNode(SIPUSH, value);

        return new LdcInsnNode(value);
    }

    public AbstractInsnNode getLongPush(long value) {
        if (value == 0)
            return new InsnNode(LCONST_0);
        if (value == 1)
            return new InsnNode(LCONST_1);

        return new LdcInsnNode(value);
    }

    public int getIntValue(AbstractInsnNode insnNode) {
        if (insnNode.getOpcode() >= ICONST_M1 && insnNode.getOpcode() <= ICONST_5)
            return insnNode.getOpcode() - 3;

        return switch (insnNode.getOpcode()) {
            case BIPUSH, SIPUSH -> ((IntInsnNode) insnNode).operand;
            default -> (int) ((LdcInsnNode) insnNode).cst;
        };
    }

    public long getLongValue(AbstractInsnNode insnNode) {
        return switch (insnNode.getOpcode()) {
            case LCONST_0 -> 0;
            case LCONST_1 -> 1;
            default -> (long) ((LdcInsnNode) insnNode).cst;
        };
    }

    /*
     * Detect if a long gets pushed onto the stack
     */
    public boolean isLongPush(AbstractInsnNode insnNode) {
        return (insnNode instanceof InsnNode && (insnNode.getOpcode() == LCONST_0 || insnNode.getOpcode() == LCONST_1)) || insnNode instanceof LdcInsnNode;
    }

    /*
     * Detect if an int gets pushed onto the stack
     */
    public boolean isIntPush(AbstractInsnNode insnNode) {
        return (insnNode instanceof LdcInsnNode ldcInsnNode && (ldcInsnNode.cst instanceof Integer || ldcInsnNode.cst instanceof Long || ldcInsnNode.cst instanceof Float || ldcInsnNode.cst instanceof Double)) ||
                insnNode instanceof IntInsnNode || (insnNode instanceof InsnNode && (insnNode.getOpcode() >= ICONST_M1 && insnNode.getOpcode() <= ICONST_5));
    }

    public boolean isLongOrIntPush(AbstractInsnNode insnNode) {
        return isLongPush(insnNode) || isIntPush(insnNode);
    }

    public boolean isRemoved(AbstractInsnNode insnNode, MethodNode methodNode) {
        return methodNode.instructions.indexOf(insnNode) < 0;
    }
}