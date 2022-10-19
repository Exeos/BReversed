package xyz.breversed.api.interfaces;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import xyz.breversed.api.JarLoader;

import java.util.ArrayList;

public interface IMethods {

    default ArrayList<ClassNode> getClasses() {
        return JarLoader.classes;
    }

    default MethodNode getMethod(ClassNode classNode, String name, String desc) {
        return classNode.methods.stream().filter(methodNode -> methodNode.name.equals(name) && methodNode.desc.equals(desc)).findFirst().orElse(null);
    }

    default MethodNode getMethod(ClassNode classNode, String name, String desc, String signature) {
        return classNode.methods.stream().filter(methodNode -> methodNode.name.equals(name) && methodNode.desc.equals(desc) && methodNode.signature.equals(signature)).findFirst().orElse(null);
    }

    default AbstractInsnNode getNext(AbstractInsnNode current, int count) {
        AbstractInsnNode next = current;
        for (int i = 0; i < count; i++) {
            if (next.getNext() == null)
                return next;
            else
                next = next.getNext();
        }

        return next;
    }
}
